(ns slack-api.open-api-parser-test
  (:require [clojure.test :refer :all]
            [matcher-combinators.test :refer [match?]]
            [slack-api.open-api-parser :as parser]))

(deftest path->slack-method-test
  (testing "takes a relative path and converts it to a namespaced keyword
    representing a Slack method"
    (are [path slack-method] (= slack-method (parser/path->slack-method path))
      "/conversations.list"        :conversations/list
      "/channels.setTopic"         :channels/set-topic
      "/admin.users.session.reset" :admin.users.session/reset)))

(deftest parse-test
  (let [descriptor (parser/parse (parser/read-open-api "dev-resources/simplified_slack_web.json"))]
    (testing "the descriptor contains the current API's version"
      (is (match? {:slack.api/version "1.2.0"}
                  descriptor)))

    (testing "the descriptor contains a set of Slack methods"
      (is (match?
           {:slack.api/methods
            {:conversations/list
             {:doc/description
              "Lists all channels in a Slack team."
              :doc/link
              "https://api.slack.com/methods/conversations.list"
              :doc/tags          #{"conversations"}
              :endpoint/url
              "https://slack.com/api/conversations.list"
              :endpoint/verb     :get
              :endpoint/consumes
              #{"application/x-www-form-urlencoded"}
              :endpoint/produces #{"application/json"}
              :endpoint/required-scopes
              #{"channels:read" "groups:read" "mpim:read"
                "im:read"}
              :slack.req/query
              {:cursor
               #:doc {:description
                      "Paginate through collections of data by setting the `cursor` parameter to a `next_cursor` attribute returned by a previous request's `response_metadata`. Default value fetches the first \"page\" of the collection. See [pagination](/docs/pagination) for more detail."
                      :pred 'string?}
               :token
               #:doc {:description
                      "Authentication token. Requires scope: `conversations:read`"
                      :pred 'string?}
               :limit
               #:doc {:description
                      "The maximum number of items to return. Fewer than the requested number of items may be returned, even if the end of the list hasn't been reached. Must be an integer no larger than 1000."
                      :pred 'int?}
               :exclude-archived
               #:doc {:description
                      "Set to `true` to exclude archived channels from the list"
                      :pred 'boolean?}
               :types
               #:doc {:description
                      "Mix and match channel types by providing a comma-separated list of any combination of `public_channel`, `private_channel`, `mpim`, `im`"
                      :pred 'string?}}}}}
           descriptor)))))
