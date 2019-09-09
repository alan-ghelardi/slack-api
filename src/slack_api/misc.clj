(ns slack-api.misc
  (:require [clojure.string :as string]
            [clojure.walk :as walk]))

(def kebab-case
  "Converts a string in camel or snake case to kebap case."
  (comp string/lower-case #(string/replace % #"([a-z])([A-Z])|_+" "$1-$2")))

(def snake-case
  "Converts a string in kebab case to snake case."
  (comp #(string/replace % #"-" "_") string/lower-case))

(defn dasherize-keys
  "Recursively applies the kebab-case function to all keys of the map
  m."
  [m]
  (letfn [(dasherize [[k v]]
            [(keyword (kebab-case k)) v])]
    (walk/postwalk #(if-not (map-entry? %)
                      %
                      (dasherize %)) m)))

(defn map-vals
  "Applies the function f to each value in the map m and return the
  resulting map."
  [f m]
  (into {} (map (fn [[k v]]
                  [k (f v)]) m)))

(defn sort-map
  "Returns a sorted view of the map m by applying the supplied
  comparator. If no comparator is supplied, uses compare."
  ([m]
   (sort-map compare m))
  ([comparator m]
   (apply sorted-map-by comparator (mapcat identity (into [] m)))))
