(ns slack-api.core
  "Full featured, data driven, REPL oriented client to Slack Web API."
  (:refer-clojure :exclude [methods])
  (:require [clojure.core.async :as async :refer [<!!]]
            [slack-api.client :as client]
            [slack-api.misc :as misc]
            [slack-api.web-api :as web-api]))

(defn methods
  "Returns a set containing all known methods exposed by the Slack Web API."
  []
  (apply sorted-set (keys (web-api/get-slack-methods))))

(defn describe-methods
  []
  (misc/map-vals :docs/description (web-api/get-slack-methods)))

(defn describe
  "Returns a descriptive data structure for the method in question."
  [method]
  (web-api/sort-method-descriptor
   (get (web-api/get-slack-methods) method)))

(defn call-async
  [{:slack/keys [method] :as method-data}]
  (let [output-channel    (async/chan)
        method-descriptor (dissoc (get (web-api/get-slack-methods) method) :slack.req/headers :slack.req/payload :slack.req/query)]
    (client/send output-channel (merge method-descriptor method-data))
    output-channel))

(defn call
  [method-data]
  (<!! (call-async method-data)))

(comment
  (call {:slack/method :conversations/list
         :slack.req/query {:limit 2}})
  )
