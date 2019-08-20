(ns slack-api.web-api
  (:require [clojure.edn :as edn]
            [clojure.java.io :as io]
            [clojure.spec-alpha2 :as s]
            [slack-api.misc :as misc]))

(def descriptor "slack_api/web_api.edn")

(def read-web-api*
  (comp edn/read-string slurp io/resource))

(def read-web-api
  (memoize read-web-api*))

(defn get-api-version
  []
  (get (read-web-api descriptor) :slack.api/version))

(s/fdef get-api-version
  :ret string?)

(defn get-slack-methods
  []
  (get (read-web-api descriptor) :slack.api/methods))

(def slack-methods (set (keys (get-slack-methods))))
(s/def :slack/method slack-methods)
(s/def :doc/description string?)
(s/def :doc/tags (s/coll-of string? :kind set?))
(s/def :doc/link string?)
(s/def :endpoint/url string?)
(def http-verbs (set (vals (misc/map-vals :endpoint/verb (get-slack-methods)))))
(s/def :endpoint/verb http-verbs)
(s/def :endpoint/required-scopes (s/coll-of string? :kind set?))
(def consumed-media-types (set (flatten (vals (misc/map-vals :endpoint/consumes (get-slack-methods))))))
(def produced-media-types (set (flatten (vals (misc/map-vals :endpoint/produces (get-slack-methods))))))
(s/def :endpoint/consumes (s/coll-of consumed-media-types :kind set?))
(s/def :endpoint/produces (s/coll-of produced-media-types :kind set?))
(s/def :slack/method-descriptor (s/keys :req [:doc/description :doc/tags :doc/link
                                              :endpoint/url :endpoint/verb :endpoint/required-scopes :endpoint/consumes :endpoint/produces]))
(s/fdef get-slack-methods
  :ret (s/map-of :slack/method :slack/method-descriptor))

(defn spec-name [method]
  (let [the-namespace (namespace method)
        the-name      (name method)]
    (keyword (str "slack." the-namespace "." the-name "/" "req"))))

(defn- resolve-multi-spec [method]
  (require 'slack-api.specs.request)
  (s/get-spec (spec-name method)))

(defmulti method-data :slack/method)

(doseq [method slack-methods]
    (. method-data addMethod method
       (fn [_] (resolve-multi-spec method))))

(s/def :slack/method-data (s/multi-spec method-data :slack/method))

(def ^:private sorting-weights
  "Map of keywords to integers used to determine the natural sorting
  order of the keys that constitute a `method-descriptor`."
  (apply merge
         (map-indexed #(hash-map %2 %1)
                      [:doc/description :doc/tags :doc/link :endpoint/url :endpoint/verb :endpoint/required-scopes :endpoint/consumes :endpoint/produces :slack.req/headers :slack.req/payload :slack.req/query])))

(defn sort-method-descriptor
  "Sorts the keys of the supplied method-descriptor in such way that the
  information contained in the map can be presented in a logical order
  when printed in the REPL."
  [method-descriptor]
  (->> method-descriptor
       (into [])
       (mapcat identity)
       (apply sorted-map-by #(< (get sorting-weights %1)
                                (get sorting-weights %2)))))
