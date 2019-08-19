(ns slack-api.misc-test
  (:require [slack-api.misc :as misc]
            [clojure.test :refer :all]))

(deftest kebab-case-test
  (are [value result] (= result (misc/kebab-case value))
    "first-name"    "first-name"
    "first_name"    "first-name"
    "FIRST_NAME"    "first-name"
    "my_first_name" "my-first-name"))

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