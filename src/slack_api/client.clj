(ns slack-api.client
  "HTTP client to interact with Slack Web API."
  (:refer-clojure :exclude [send])
  (:require [clojure.core.async :as async :refer [>!!]]
            [clojure.data.json :as json]
            [clojure.java.io :as io]
            [clojure.string :as string]
            [clojure.walk :as walk]
            [org.httpkit.client :as httpkit-client]
            [slack-api.misc :as misc])
  (:import java.net.URLEncoder))

(def http-defaults
  {:as               :stream
   :follow-redirects false
   :user-agent       "slack-client"
   :keepalive        120000
   :timeout          30000})

(def json-request-parser
  #(json/write-str % :key-fn misc/snake-case))

(def json-response-parser
  (comp #(json/read % :key-fn (comp keyword misc/kebab-case))
        io/reader))

(def request-body-parsers
  {#"^application/json"                  json-request-parser
   #"^application/x-www-form-urlencoded" identity})

(def response-body-parsers
  {#"^application/json" json-response-parser})

(defn- select-parser
  "Selects the most suited parser function in the map m according to the
  supplied media-type."
  [m media-type]
  (some (fn [[k v]]
          (when (re-find k media-type)
            v))
        m))

(defn- handle-http-response
  [{:keys [status headers body]}]
  (let [normalized-headers (walk/stringify-keys headers)
        parser-fn (select-parser response-body-parsers (get normalized-headers "content-type"))
        parsed-body (parser-fn body)]
    parsed-body))

(defn get-auth-token
  []
  (slurp (io/file (System/getProperty "user.home") ".slack-token")))

(defn- add-query-string
  "If `:slack.req/query` is given, appends the query string to the
  request url."
  [request {:slack.req/keys [query]}]
  (let [build-query-string #(string/join "&"
                                         (map (fn [[k v]]
                                                (str (misc/snake-case (name k))
                                                     "=" (URLEncoder/encode (str v) "UTF-8")))
                                              query))]
    (if-not query
      request
      (update request :url #(str % "?" (build-query-string))))))

(defn- parse-request-body
  "If `:slack.req/payload` is given, parses it according to the supplied
  content-type and assoc's the parsed value as `:body` into the
  request."
  [request {:slack.req/keys [payload]}]
  (if-not payload
    request
    (assoc request :body
           ((select-parser request-body-parsers (get-in request [:headers "content-type"])) payload))))

(defn- add-headers
  [request {:slack.req/keys [headers]}]
  (if-not headers
    request
    (update :headers #(merge % (walk/stringify-keys headers)))))

(defn- preferred-media-type
  "Decides the most suited media type, by choosing between the supplied options.

  If options has more than one media type and `application/json` is
  included between them, prefers it over others."
  [options]
  (if (= 1 (count options))
    (first options)
    (some #(when (re-find #"^application/json" %)
             %)
          options)))

(defn build-http-request
  "Returns a suited HTTP request map for the supplied Slack method."
  [{:endpoint/keys [verb url consumes produces] :as method-data}]
  (-> {:method  verb
       :url     url
       :headers {"content-type" (preferred-media-type consumes)
                 "accept"       (preferred-media-type produces)}}
      (merge http-defaults)
      (add-headers method-data)
      (parse-request-body method-data)
      (add-query-string method-data)
      (assoc :oauth-token (get-auth-token))))

(defn send
  "Sends an asynchronous HTTP request to Slack API."
  [channel method-data]
  (httpkit-client/request (build-http-request method-data)
                          #(async/go (>!! channel (handle-http-response %))))
  channel)