(ns slack-api.validator-test
  (:require [clojure.spec-alpha2 :as s]
            [clojure.test :refer :all]
            [clojure.test.check.clojure-test :refer [defspec]]
            [clojure.test.check.properties :refer [for-all]]
            [matcher-combinators.test :refer [match?]]
            [slack-api.validator :as validator]))

(deftest valid?-test
  (are [x result] (= result (validator/valid? x))
    nil                                                   true
    {}                                                    true
    {:slack.errors/category :slack.errors/missing-method} false))

(defspec validate-method-data-gen-test
  {:num-tests 50}
  (for-all [method-data (s/gen :slack/method-data)]
           (is (validator/valid?
                (validator/validate-method-data method-data)))))

(deftest validate-method-data-test
  (testing "returns an error data structure for each violation below"
    (is (match? {:slack.errors/category :slack.errors/missing-method}
                (validator/validate-method-data {}))
        "missing Slack method")

    (is (match? {:slack.errors/category :slack.errors/no-such-method}
                (validator/validate-method-data {:slack/method :foo}))
        "this method doesn't exist")

    (is (match? {:slack.errors/category :slack.errors/malformed-data}
                (validator/validate-method-data {:slack/method    :api/test
                                                 :slack.req/query {:foo 42}}))
        "foo must be a string")))
