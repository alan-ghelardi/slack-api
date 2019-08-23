(ns slack-api.validator-test
  (:require [clojure.spec-alpha2 :as s]
            [clojure.test :refer :all]
            [clojure.test.check.clojure-test :refer [defspec]]
            [clojure.test.check.properties :refer [for-all]]
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
