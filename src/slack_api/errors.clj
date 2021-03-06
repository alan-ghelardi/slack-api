(ns slack-api.errors
  (:require [clojure.spec-alpha2 :as s]
            [slack-api.web-api :as web-api]))

(s/def :slack.errors/category #{:slack.errors/missing-method
                                :slack.errors/no-such-method
                                :slack.errors/malformed-data
                                :slack.errors/unexpected-error})

(s/def :slack.errors/message string?)

(s/def :slack.errors/problems (s/coll-of list? :kind vector? :min-count 1))

(s/def :slack.errors/throwable #(instance? Throwable %))

(s/def :slack.errors/validation-error (s/keys :req [:slack.errors/category :slack.errors/message]
                                              :opt [:slack.errors/problems :slack.errors/throwable]))

(def ^:private see-available-methods "Tip: call `(slack.core/methods)` or `(slack.core/describe-methods)` to see a comprehensive list of available Slack methods.")

(defn no-such-slack-method
  "Returns a descriptive data structure explaining that the method in
  question doesn't exist."
  [method]
  #:slack.errors{:category :slack.errors/no-such-method
                 :message
                 (format "No such method `%s`. %s" method see-available-methods)})

(defn- slack-method-must-be-present
  [{:slack/keys [method]}]
  (when-not method
    #:slack.errors{:category :slack.errors/missing-method
                   :message
                   (format "Please, provide a `:slack/method` key containing a valid Slack method. %s" see-available-methods)}))

(defn- slack-method-must-exist
  [{:slack/keys [method]}]
  (when-not (get (web-api/get-slack-methods) method)
    (no-such-slack-method method)))

(defn- explain-problems
  "Returns a more succinct and friendly version of the problems returned
  by the `clojure.spec-alpha2/explain-data` function."
  [method-data]
  (->> method-data
       (s/explain-data :slack/method-data)
       :clojure.spec.alpha/problems
       (mapv (fn [{:keys [path pred val]}]
               (list 'expected (symbol (name pred))
                     'got val
                     'at (vec (rest path)))))))

(defn- data-must-satisfy-spec
  [method-data]
  (when-not (s/valid? :slack/method-data method-data)
    #:slack.errors{:category :slack.errors/malformed-data
                   :message
                   (format "Call `(slack-api.core/describe-method %s)` to see a detailed description about how to call this method." (:slack/method method-data))
                   :problems (explain-problems method-data)}))

(defn validation-error?
  "Returns true if the argument represents a validation error, or false
  otherwise."
  [result]
  (boolean (get result :slack.errors/category)))

(s/fdef validation-error?
  :args (s/cat :result any?)
  :ret boolean?)

(def validators
  "Sequence of 1-arity functions that take a method-data and returns a
  validation error when it is invalid."
  [slack-method-must-be-present
   slack-method-must-exist
   data-must-satisfy-spec])

(defn validate-method-data
  "Applies a set of validation rules to the method-data in question in
  order to guarantee that the request is valid, before sending it to
  Slack API.

  Returns the own method-data when it is considered valid or a
  meaningful data structure describing the error otherwise."
  [method-data]
  (or
   (some #(let [result (% method-data)]
            (when (validation-error? result)
              result))
         validators)
   method-data))

(s/fdef validate-method-data
  :args (s/cat :method-data :slack/method-data)
  :ret (s/or :ok :slack/method-data
             :error :slack.errors/validation-error))

(defn unexpected-error
  "Returns a data structure representing an arbitrary and unexpected
  error."
  [^String message ^Throwable error]
  #:slack.errors{:category  :slack.errors/unexpected-error
                 :message   message
                 :throwable error}
  )
