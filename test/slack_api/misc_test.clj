(ns slack-api.misc-test
  (:require [clojure.java.io :as io]
            [clojure.spec-alpha2.gen :as gen]
            [clojure.test :refer :all]
            [clojure.test.check.clojure-test :refer [defspec]]
            [clojure.test.check.properties :refer [for-all]]
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

(deftest file-exists-test
  (is (true? (misc/file-exists? (io/file "deps.edn"))))
  (is (false? (misc/file-exists? (io/file "foo.edn")))))

(deftest home-dir-test
  (is (true? (misc/file-exists? (misc/home-dir)))))

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

(defspec sort-map-gen-test
  {:num-tests 25}
  (for-all [m (gen/map (gen/keyword) (gen/any))]
           (is (= m (misc/sort-map m))
               "always returns the same map passed as argument")

           (is (= (some-> m keys sort)
                  (keys (misc/sort-map m)))
               "sorts the keys of the map in question by using the default
               comparator")

           (is (= (misc/sort-map m)
                  (misc/sort-map compare m))
               "sorts the map by using the supplied comparator")))
