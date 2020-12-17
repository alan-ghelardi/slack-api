(ns slack-api.core
  "Full featured, data driven, REPL oriented client to Slack Web API."
  (:refer-clojure :exclude [methods])
  (:require [clojure.core.async :as async :refer [<!!]]
            [clojure.edn :as edn]
            [clojure.java.io :as io]
            [clojure.spec-alpha2 :as s]
            [slack-api.client :as client]
            [slack-api.errors :as errors]
            [slack-api.misc :as misc]
            [slack-api.web-api :as web-api]))

(defn methods
  "Returns a set containing all known methods exposed by the Slack Web API.

  Slack methods are sorted alphabetically to facilitate the
  visualization in the REPL."
  []
  (apply sorted-set (keys (web-api/get-slack-methods))))

(s/fdef methods
  :ret (s/coll-of :slack/method :kind set?))

(defn describe-methods
  "Returns a map from known Slack methods (qualified keywords) to their
  descriptions (strings).

  Slack methods are sorted alphabetically to facilitate the
  visualization in the REPL."
  []
  (misc/sort-map
   (misc/map-vals :doc/description (web-api/get-slack-methods))))

(s/fdef describe-methods
  :ret (s/map-of :slack/method string?))

(defn describe
  "Returns a descriptive data structure for the method in question."
  [method]
  (if-let [descriptor (get (web-api/get-slack-methods) method)]
    (web-api/sort-method-descriptor descriptor)
    (errors/no-such-slack-method method)))

(s/fdef describe
  :args (s/cat :method :slack/method)
  :ret :slack/method-descriptor)

(defn- read-oauth-token
  "Attempts to read the oauth token to access the Slack API from a file
  named .slack.edn at the user's home directory."
  []
  (let [credentials (io/file (misc/home-dir) ".slack.edn")]
    (when (misc/file-exists? credentials)
      (:slack.auth/oauth-token (edn/read-string (slurp credentials))))))

(def ^:private default-client-opts
  "Default options to control the Slack client behavior."
  {:oauth-token-fn read-oauth-token

   :throw-errors? false})

(defn- build-input-data
  [{:slack/keys [method] :as method-data}]
  (let [method-descriptor (dissoc (get (web-api/get-slack-methods) method) :slack.req/headers :slack.req/payload :slack.req/query)]
    (update
     (merge method-descriptor method-data)
     :slack.client/opts (partial merge default-client-opts))))

(defn call-async
  [method-data]
  (let [output-channel (async/chan)
        result         (errors/validate-method-data method-data)]
    (if (errors/validation-error? result)
      (async/put! output-channel result)
      (client/send output-channel (build-input-data method-data)))
    output-channel))

(defn- throw-error
  "Given a result data representing a preflight error or a
  non-successful response from the Slack API, throws an exception info
  explaining what went wrong."
  [result]
  (throw (ex-info (if (errors/validation-error? result)
                    "Preflight error"
                    "Slack API returned an error")
                  result)))

(defn call
  [method-data]
  (let [result (<!! (call-async method-data))]
    (if (and (get-in method-data [:slack.client/opts :throw-errors?])
             (or (errors/validation-error? result)
                 (not (:ok result))))
      (throw-error result)
      result)))

(comment
  (call {:slack/method    :conversations/list
         :slack.req/query {:limit 2}}))
