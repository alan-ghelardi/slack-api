(ns slack-api.web-api
  "Internal bridge to interact with the web_api descriptor file."
  (:require [clojure.edn :as edn]
            [clojure.java.io :as io]
            [clojure.spec-alpha2 :as s]
            [slack-api.misc :as misc]
            [clojure.set :as set]))

(def descriptor
  "The EDN file that describes all Slack methods."
  "slack_api/web_api.edn")

(def ^:private read-web-api*
  (comp edn/read-string slurp io/input-stream io/resource))

(def read-web-api
  "Memoized reader for the web_api.edn descriptor."
  (memoize read-web-api*))

(defn get-api-version
  "Returns the current version of Slack Web API."
  []
  (get (read-web-api descriptor) :slack.api/version))

(s/fdef get-api-version
  :ret string?)

(defn get-slack-methods
  "Returns a map containing all known Slack methods along with their
  descriptors."
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
(s/def :endpoint/required-scopes (s/coll-of string? :kind set? :min-count 1))
(def consumed-media-types (apply set/union (vals (misc/map-vals :endpoint/consumes (get-slack-methods)))))
(def produced-media-types (apply set/union (vals (misc/map-vals :endpoint/produces (get-slack-methods)))))
(s/def :endpoint/consumes (s/coll-of consumed-media-types :kind set? :min-count 1))
(s/def :endpoint/produces (s/coll-of produced-media-types :kind set? :min-count 1))
(s/def :slack/method-descriptor (s/keys :req [:doc/description :doc/tags :doc/link
                                              :endpoint/url :endpoint/verb :endpoint/required-scopes :endpoint/consumes :endpoint/produces]))
(s/def :slack/methods (s/map-of :slack/method :slack/method-descriptor))
(s/fdef get-slack-methods
  :ret :slack/methods)

(defn req-spec-name
  "Given a qualified keyword representing a Slack method, returns the
  corresponding request spec name."
  [method]
  (let [the-namespace (namespace method)
        the-name      (name method)]
    (keyword (str "slack." the-namespace "." the-name "/" "req"))))

(s/fdef req-spec-name
  :args (s/cat :method :slack/method)
  :ret keyword?)

(defn- resolve-request-spec
  "Given a qualified keyword representing a Slack method, returns the
  corresponding request spec object."
  [method]
  (require 'slack-api.specs.request)
  (s/get-spec (req-spec-name method)))

(defmulti method-data :slack/method)

;; Dynamically add multi-methods for each Slack method.
(doseq [method slack-methods]
  (. method-data addMethod method
     (fn [_] (resolve-request-spec method))))

(s/def :slack/method-data (s/multi-spec method-data :slack/method))

(def sorting-weights
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
  (misc/sort-map #(< (get sorting-weights %1)
                     (get sorting-weights %2))
                 method-descriptor))
