(ns slack-api.misc-test
  (:require [clojure.test :refer :all]
            [slack-api.misc :as misc]))

(deftest kebab-case-test
  (are [value result] (= result (misc/kebab-case value))
    "first-name"       "first-name"
    "first_name"       "first-name"
    "FIRST_NAME"       "first-name"
    "my_first_name"    "my-first-name"
    "setTopic"         "set-topic"
    "setActiveChannel" "set-active-channel"))

(deftest snake-case-test
  (are [value result] (= result (misc/snake-case value))
    "first_name"    "first_name"
    "first-name"    "first_name"
    "FIRST-NAME"    "first_name"
    "my-first-name" "my_first_name"))

(deftest symmetry-test
  (is (= "my-first-name"
         (misc/kebab-case (misc/snake-case "my-first-name"))))

  (is (= "my_first_name"
         (misc/snake-case (misc/kebab-case "my_first_name")))))

(deftest dasherize-keys-test
  (is (= {:relevant-channels [{:name-normalized "channel1"}
                              {:name-normalized "channel2"}]}
         (misc/dasherize-keys {"relevant_channels" [{"name_normalized" "channel1"}
                                                    {"name_normalized" "channel2"}]}))))

(deftest map-vals-test
  (are [map result] (= result (misc/map-vals inc map))
    {}          {}
    {:a 1}      {:a 2}
    {:a 1 :b 2} {:a 2 :b 3}))
