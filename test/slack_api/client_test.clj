(ns slack-api.client-test
  (:require [clojure.spec-alpha2 :as s]
            [matcher-combinators.test :refer [match?]]
            [clojure.spec-alpha2.gen :as gen]
            [clojure.string :as string]
            [clojure.test :refer :all]
            [clojure.test.check.clojure-test :refer [defspec]]
            [clojure.test.check.properties :refer [for-all]]
            [slack-api.client :as client]
            [slack-api.web-api :as web-api])
  (:import java.io.StringReader))

(def custom-keyword-gen
  (gen/fmap #(keyword (string/lower-case (apply str (flatten (interleave (partition 3
                                                                                    %)
                                                                         (repeat "-"))))))
            (gen/vector (gen/char-alpha) 10)))

(deftest form-encoder-test
  (testing "converts a Clojure map to a string in the x-www-form-urlencoded
  format"
    (is (= "first_name=John"
           (client/form-encoder {:first-name "John"})))

    (is (= "first_name=John&last_name=Doe&some_rate=58%25"
           (client/form-encoder {:first-name "John"
                                 :last-name  "Doe"
                                 :some-rate  "58%"})))))

(deftest json-request-parser-test
  (testing "converts the Clojure data structure to a string in the JSON format"
    (is (= "{\"first_name\":\"John\",\"last_name\":\"Doe\"}"
           (client/json-request-parser {:first-name "John"
                                        :last-name  "Doe"})))))

(deftest json-response-parser-test
  (testing "consumes a reader containing a JSON object and converts its content
  back to a Clojure data structure"
    (is (= {:first-name "John"
            :last-name  "Doe"}
           (client/json-response-parser
            (StringReader. "{\"first_name\":\"John\",\"last_name\":\"Doe\"}"))))))

(defspec json-symmetry-parsers-test
  {:num-tests 50}
  (for-all [data (gen/map
                  custom-keyword-gen
                  (gen/one-of [(gen/string-alphanumeric) (gen/boolean) (gen/int)]))]
           (is (= data
                  (client/json-response-parser
                   (StringReader.
                    (client/json-request-parser data)))))))

(defspec parsers-test
  {:num-tests 5}
  (for-all [content-type (gen/elements web-api/consumed-media-types)
            accept (gen/elements web-api/produced-media-types)
            data (gen/map (gen/keyword) (gen/string-alphanumeric))]
           (testing "there is a parser function for each media type consumed and
           produced by the Slack API"
             (is (fn?
                  (client/select-parser client/request-body-parsers content-type)))

             (is (fn?
                  (client/select-parser client/response-body-parsers accept))))

           (testing "request parsers can parse arbitrary Clojure maps"
             (is (string?
                  ((client/select-parser client/request-body-parsers content-type) data))))))

(defspec build-http-request-gen-test
  {:num-tests 75}
  (for-all [method-data (s/gen :slack/method-data)]
           (let [{:slack/keys [method]} method-data
                 method-descriptor      (get (web-api/get-slack-methods) method)]
             (testing "builds a valid request from arbitrary method-data"
               (is (map?
                    (client/build-http-request (merge method-descriptor method-data))))))))

(def method-descriptor
  {:endpoint/url      "https://slack.com/api/this.method"
   :endpoint/verb     :post
   :endpoint/required-scopes
   #{"scope"}
   :endpoint/consumes #{"application/json" "application/x-www-form-urlencoded"}
   :endpoint/produces #{"application/json"}})

(deftest build-http-request-test
  (testing "the returned request includes some default options"
    (is (match? client/http-defaults
                (client/build-http-request method-descriptor))))

  (testing "adds the HTTP method and the endpoint's URL to the request"
    (is (match? {:method :post
                 :url    "https://slack.com/api/this.method"}
                (client/build-http-request method-descriptor))))

  (testing "adds the content-type and accept headers to the request; always
    prefers application/json when the endpoint consumes two different media
    types"
    (is (match? {:headers {"content-type" "application/json"
                           "accept"       "application/json"}}
                (client/build-http-request method-descriptor))))

  (testing "assoc's additional headers in the request"
    (is (match? {:headers {"content-type" string?
                           "token"        "token"}}
                (client/build-http-request (assoc method-descriptor
                                                  :slack.req/headers {:token "token"})))))

  (testing "assoc's a parsed body into the request"
    (is (match? {:body "{\"channel_id\":\"id\"}"}
                (client/build-http-request (assoc method-descriptor
                                                  :slack.req/payload {:channel-id "id"})))))

  (testing "appends a query string to the request's URL"
    (is (match? {:url "https://slack.com/api/this.method?limit=1&exclude_archived=true"}
                (client/build-http-request (assoc method-descriptor
                                                  :slack.req/query {:limit            1
                                                                    :exclude-archived true}))))))
