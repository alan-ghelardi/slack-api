(ns slack-api.core-test
  (:require [clojure.core.async :refer [<!!]]
            [clojure.spec-alpha2 :as s]
            [clojure.test :refer :all]
            [matcher-combinators.test :refer [match?]]
            [slack-api.core :as slack]))

(deftest methods-test
  (is (not (empty? (slack/methods))))

  (is (true? (every? #(s/valid? :slack/method %) (slack/methods)))))

(deftest describe-methods-test
  (is (nil? (s/explain-data (s/map-of :slack/method string?)
                            (slack/describe-methods)))))

(deftest describe-test
  (testing "returns an error when the method doesn't exist"
    (is (match? {:slack.errors/category :slack.errors/no-such-method}
                (slack/describe :foo/bar)))))

(deftest call-async-test
  (testing "performs a preflight check of the call in question"
    (is (match? {:slack.errors/category :slack.errors/no-such-method}
                (<!! (slack/call-async {:slack/method :foo/bar}))))))
