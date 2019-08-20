(ns slack-api.specs-builder-test
  (:require [clojure.test :refer :all]
            [slack-api.specs-builder :as specs-builder]))

(def slack-methods
  #:conversations{:create
                  {:endpoint/verb     :post
                   :endpoint/consumes
                   #{"application/json" "application/x-www-form-urlencoded"}
                   :endpoint/produces #{"application/json"}
                   :slack.req/headers
                   {:token #:doc {:description "doc" :pred 'string?}}
                   :slack.req/payload
                   {:user-ids   #:doc {:description "doc" :pred 'string?}
                    :name       #:doc {:description "doc" :pred 'string?}
                    :is-private #:doc {:description "doc" :pred 'boolean?}}
                   :slack.req/query
                   {:limit #:doc {:description "doc" :pred 'int}}}})

(deftest build-schema-forms-test
  (testing "takes a map of Slack methods and builds a sequence of request specs for them"
    (is (= ['(s/def
               :slack.conversations.create/req
               (s/schema
                #:slack.req{:headers (s/schema {:token string?})
                            :payload
                            (s/schema
                             {:user-ids string? :name string? :is-private boolean?})
                            :query   (s/schema {:limit int})}))]
           (specs-builder/build-schema-forms slack-methods)))))

(deftest ns-form-test
  (testing "returns an appropriate ns form for the auto-generated specs"
    (is (= '(ns slack-api.specs.request
              "doc"
              (:require [clojure.spec-alpha2 :as s]))
           (specs-builder/ns-form 'slack-api.specs.request "doc")))))

(deftest ns-symbol->file
  (testing "Given a namespace symbol, returns the corresponding file path
  relative to the project's root dir, by applying Clojure conventions."
    (is (= "src/slack_api/specs/request.clj"
           (.getPath (specs-builder/ns-symbol->file 'slack-api.specs.request))))))

(deftest gen-specs-ns-test
  (testing "Generates a formatted namespace string containing the supplied
  specs."
    (is (= "(ns\n slack-api.specs.request\n \"doc\"\n (:require [clojure.spec-alpha2 :as s]))\n\n(s/def\n :slack.conversations.create/req\n (s/schema\n  #:slack.req{:headers (s/schema {:token string?}),\n              :payload\n              (s/schema\n               {:user-ids string?,\n                :name string?,\n                :is-private boolean?}),\n              :query (s/schema {:limit int})}))\n\n"
           (specs-builder/gen-specs-ns 'slack-api.specs.request "doc" (specs-builder/build-schema-forms slack-methods))))))
