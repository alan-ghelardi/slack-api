(ns slack-api.core
  "Full featured, data driven, REPL oriented client to Slack Web API."
  (:refer-clojure :exclude [methods])
  (:require [clojure.core.async :as async :refer [<!!]]
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

(defn- build-input-data
  [{:slack/keys [method] :as method-data}]
  (let [method-descriptor (dissoc (get (web-api/get-slack-methods) method) :slack.req/headers :slack.req/payload :slack.req/query)]
    (merge method-descriptor method-data)))

(defn call-async
  [method-data]
  (let [output-channel (async/chan)
        result         (errors/validate-method-data method-data)]
    (if (errors/validation-error? result)
      (async/put! output-channel result)
      (client/send output-channel (build-input-data method-data)))
    output-channel))

(defn call
  [method-data]
  (<!! (call-async method-data)))

(comment
  (call {:slack/method    :conversations/list
         :slack.req/query {:limit 2}}))
