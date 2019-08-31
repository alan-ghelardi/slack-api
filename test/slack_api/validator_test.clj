(ns slack-api.validator-test
  (:require [clojure.spec-alpha2 :as s]
            [clojure.test :refer :all]
            [clojure.test.check.clojure-test :refer [defspec]]
            [clojure.test.check.properties :refer [for-all]]
            [matcher-combinators.matchers :as m]
            [matcher-combinators.test :refer [match?]]
            [slack-api.validator :as validator]))

(deftest validation-error?-test
  (are [x result] (= result (validator/validation-error? x))
    nil false
    {} false
    {:slack.errors/category :slack.errors/missing-method} true))

(defspec validate-method-data-gen-test
  {:num-tests 50}
  (for-all [method-data (s/gen :slack/method-data)]
           (is (not (validator/validation-error?
                     (validator/validate-method-data method-data))))))

(deftest validate-method-data-test
  (testing "returns a validation error data structure for each violation below"
    (is (match? {:slack.errors/category :slack.errors/missing-method}
                (validator/validate-method-data {}))
        "missing Slack method")

    (is (match? {:slack.errors/category :slack.errors/no-such-method}
                (validator/validate-method-data {:slack/method :foo}))
        "this method doesn't exist")

    (is (match? {:slack.errors/category :slack.errors/malformed-data}
                (validator/validate-method-data {:slack/method    :api/test
                                                 :slack.req/query {:foo 42}}))
        "foo must be a string"))

  (testing ""
    (is (match? #:slack.errors{:category :slack.errors/malformed-data
                               :problems
                               (m/in-any-order ['(expected string? got 42 at [:slack.req/query :foo])
                                                '(expected string? got true at [:slack.req/query :error])])}

                (validator/validate-method-data {:slack/method    :api/test
                                                 :slack.req/query {:foo   42
                                                                   :error true}})))))
