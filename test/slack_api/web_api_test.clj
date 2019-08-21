(ns slack-api.web-api-test
  (:require [clojure.spec-alpha2 :as s]
            [clojure.spec-alpha2.gen :as gen]
            [clojure.test :refer :all]
            [clojure.test.check.clojure-test :refer [defspec]]
            [clojure.test.check.properties :refer [for-all]]
            [slack-api.web-api :as web-api]))

(deftest get-api-version-test
  (testing "Returns the current version of Slack Web API"
    (is (re-find #"^(\d+\.){2}\d+$"
                 (web-api/get-api-version)))))

(deftest get-slack-methods-test
  (testing "returns a valid map of Slack methods along with their descriptors"
    (is (nil? (s/explain-data :slack/methods (web-api/get-slack-methods))))))

(deftest req-spec-name
  (testing "takes a Slack method and returns a request spec name"
    (are [method spec-name] (= spec-name (web-api/req-spec-name method))
      :conversations/archive     :slack.conversations.archive/req
      :admin.users.session/reset :slack.admin.users.session.reset/req)))

(defspec method-data-test
  {:num-tests (count (web-api/get-slack-methods))}
  (for-all [method (s/gen :slack/method)]
           (testing "there is a method-data implementation for each existing Slack method"
             (is (fn?
                  (get-method web-api/method-data method))))))

(defspec sort-method-descriptor-test
  {:num-tests 25}
  (for-all [method-descriptor (gen/fmap #(assoc % :slack.req/headers {}
                                                :slack.req/payload {}
                                                :slack.req/query {})
                                        (s/gen :slack/method-descriptor))]
           (let [expected-order (map first (sort-by second (into [] web-api/sorting-weights)))]
             (is (= expected-order
                    (keys (web-api/sort-method-descriptor method-descriptor)))))))
