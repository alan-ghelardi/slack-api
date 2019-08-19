(ns slack-api.misc
  (:require [clojure.string :as string]
            [clojure.walk :as walk]))

(def kebab-case
  "Converts a string in camel or snake case to kebap case."
  (comp string/lower-case #(string/replace % #"([a-z])([A-Z])|_+" "$1-$2")))

(def snake-case
  "Converts a string in kebab case to snake case."
  (comp #(string/replace % #"-" "_") string/lower-case))

(defn dasherize-keys [m]
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
