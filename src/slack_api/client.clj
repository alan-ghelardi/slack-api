(ns slack-api.client
  "HTTP client to interact with Slack Web API."
  (:refer-clojure :exclude [send])
  (:require [clojure.core.async :as async :refer [>!!]]
            [clojure.data.json :as json]
            [clojure.string :as string]
            [clojure.walk :as walk]
            [org.httpkit.client :as httpkit-client]
            [slack-api.errors :as errors]
            [slack-api.misc :as misc])
  (:import [java.net URI URLEncoder]
           [javax.net.ssl SNIHostName SSLEngine SSLParameters]))

(defn- ssl-configurer
  "Configures the SSL engine to enable server name indication (SNI)."
  [^SSLEngine ssl-engine ^URI uri]
  (let [^SSLParameters ssl-params (.getSSLParameters ssl-engine)]
    (.setServerNames ssl-params [(SNIHostName. (.getHost uri))])
    (.setUseClientMode ssl-engine true)
    (.setSSLParameters ssl-engine ssl-params)))

(def http-defaults
  {:as               :text
   :client           (httpkit-client/make-client {:ssl-configurer ssl-configurer})
   :follow-redirects false
   :user-agent       "slack-client"
   :keepalive        120000
   :timeout          30000})

(defn form-encoder
  "Turns a Clojure map into a string in the x-www-form-urlencoded
  format."
  [data]
  (string/join "&"
               (map (fn [[k v]]
                      (str (misc/snake-case (name k))
                           "=" (URLEncoder/encode (str v) "UTF-8")))
                    data)))

(def json-request-parser
  "JSON parser for request's bodies."
  #(json/write-str % :key-fn (comp misc/snake-case name)))

(def json-response-parser
  "JSON parser for response's bodies."
  #(json/read-str % :key-fn (comp keyword misc/kebab-case)))

(def request-body-parsers
  "Map of media types to parser functions."
  {#"^application/json"                  json-request-parser
   #"^application/x-www-form-urlencoded" form-encoder})

(def response-body-parsers
  "Map of media types to parser functions.

  Notice that currently only application/json is supported by Slack
  API. It may be extended in the future if needed."
  {#"^application/json" json-response-parser})

(defn select-parser
  "Selects the most suited parser function in the map m according to the
  supplied media-type."
  [m media-type]
  (some (fn [[k v]]
          (when (re-find k media-type)
            v))
        m))

(defn- normalize-header-names
  "Given a map containing header names and their values, transforms all
  keys to downcased strings."
  [headers]
  (letfn [(downcase [[k v]]
            [(string/lower-case (name k)) v])]
    (walk/postwalk #(if-not (map-entry? %)
                      %
                      (downcase %)) headers)))

(defn handle-http-response
  "Takes a response map sent by the Slack API and returns the response's body as a Clojure data structure.

  The original response (with normalized headers) will be available in
  the :slack.resp/raw key in the meta of the returned map."
  [response]
  (if (:error response)
    (errors/unexpected-error "Unexpected HTTP error when trying to call Slack API" (:error response))

    (let [{:keys [headers body] :as raw-resp} (dissoc (update response :headers normalize-header-names) :opts)
          parser-fn                           (select-parser response-body-parsers (get headers "content-type"))
          resp-data                           (parser-fn body)]
      (vary-meta resp-data
                 assoc :slack.resp/raw raw-resp))))

(defn- add-query-string
  "If `:slack.req/query` is given, appends the query string to the
  request url."
  [request {:slack.req/keys [query]}]
  (if-not query
    request
    (update request :url #(str % "?" (form-encoder query)))))

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
  "When :slack.req/headers is present, includes additional headers in
  the request."
  [request {:slack.req/keys [headers]}]
  (if-not headers
    request
    (update request :headers #(merge % (normalize-header-names headers)))))

(defn- add-oauth-token
  "Adds the oauth token header to the request if there is a token
  function declared at [:slack.client/opts :oauth-token-fn] in the
  method-data."
  [request method-data]
  (let [oauth-token-fn (get-in method-data [:slack.client/opts :oauth-token-fn] (constantly nil))]
    (if-let [oauth-token (oauth-token-fn)]
      (assoc-in request [:headers "authorization"]
                (str "Bearer " oauth-token))
      request)))

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
      (add-oauth-token method-data)
      (add-headers method-data)
      (parse-request-body method-data)
      (add-query-string method-data)))

(defn send
  "Sends an asynchronous HTTP request to Slack API and returns the
  channel filled out with the response."
  [channel method-data]
  (httpkit-client/request (build-http-request method-data)
                          #(async/go (>!! channel (handle-http-response %))))
  channel)
