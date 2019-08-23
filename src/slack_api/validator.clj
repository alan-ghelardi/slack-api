(ns slack-api.validator
  (:require [clojure.spec-alpha2 :as s]
            [slack-api.web-api :as web-api]))

(s/def :slack.errors/category #{:slack.errors/missing-method
                                :slack.errors/no-such-method
                                :slack.errors/spec-violations})

(s/def :slack.errors/message string?)

(def ^:private see-available-methods "Tip: call `(slack.core/methods)` or `(slack.core/describe-methods)` to see a comprehensive list of available Slack methods.")

(defn- method-is-present [{:slack/keys [method]}]
  (when-not method
    #:slack.errors{:category :slack.errors/missing-method
                   :message
                   (format "Please, provide a `:slack/method` key containing a valid Slack method. %s" see-available-methods)}))

(defn- method-exists [{:slack/keys [method]}]
  (when-not (get (web-api/get-slack-methods) method)
    #:slack.errors{:category :slack.errors/no-such-method
                   :message
                   (format "No such method `%s`. %s" method see-available-methods)}))

(defn- data-satisfy-spec [method-data]
  (let [options {:closed #{:slack/method-data}}]
    (when-not (s/valid? :slack/method-data method-data options)
      #:slack.errors{:category :slack.errors/spec-violations
                     :message
                     (format "Call `(slack-api.core/describe-method %s)` to see a detailed description about how to call this method." (:slack/method method-data))
                     :problems (:clojure.spec.alpha/problems (s/explain-data :slack/method-data method-data options))})))

(defn valid?
  "Returns true if the result represents a possibly valid method-data
  and false otherwise."
  [result]
  (not (boolean (get result :slack.errors/category))))

(def validation-rules
  "Sequence of 1-arity functions that takes a method-data and returns a
  validation error when it is invalid."
  [method-is-present
   method-exists
   data-satisfy-spec])

(defn validate-method-data
  "Applies a set of validation rules to the method-data in question in
  order to guarantee that the request is valid, before sending it to
  Slack API.

  Returns the own method-data when it is considered valid or a
  meaningful data structure describing the error otherwise."
  [method-data]
  (or
   (some #(let [result (% method-data)]
            (when-not (valid? result)
              result))
         validation-rules)
   method-data))

(s/fdef validate-method-data
  :args (s/cat :method-data :slack/method-data))
