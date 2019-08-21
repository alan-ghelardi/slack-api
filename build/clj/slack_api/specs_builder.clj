(ns slack-api.specs-builder
  (:require [clojure.java.io :as io]
            [clojure.pprint :as pprint]
            [clojure.string :as string]
            [slack-api.misc :as misc]
            [slack-api.web-api :as web-api])
  (:import java.io.StringWriter))

(defn- wrap-in-schema-form
  "Wraps each request parameter in a s/schema form."
  [req-data]
  (misc/map-vals (comp #(list 's/schema %) (partial misc/map-vals :doc/pred)) req-data))

(defn build-schema-forms
  "Takes a map of Slack methods and returns a lazy sequence of schema
  forms describing the request parameters supported by those methods."
  [slack-methods]
  (->> slack-methods
       (misc/map-vals (comp wrap-in-schema-form #(select-keys % [:slack.req/headers :slack.req/payload :slack.req/query])))
       (map (fn [[method literal-map]]
              (list 's/def (web-api/req-spec-name method)
                    (list 's/schema literal-map))))
       (sort-by second)))

(defn ns-form
  "Builds a ns form from the supplied ns-name and doc-string."
  [ns-name doc-string]
  (list 'ns ns-name
        doc-string
        (list :require '[clojure.spec-alpha2 :as s])))

(defn ns-symbol->file
  "Given a namespace symbol, returns the corresponding file path
  relative to the project's root dir, by applying Clojure conventions."
  [ns-symbol]
  (let [parts (string/split (misc/snake-case (name ns-symbol)) #"\.")]
    (->> (update parts (dec (count parts)) #(str % ".clj"))
         (cons "src")
         (apply io/file))))

(defn- write-form
  "Writes an arbitrary Clojure form to the supplied writer, by appending
  a new line after it."
  [writer form]
  (let [text (with-out-str (pprint/pprint form))]
    (doto writer
      (.write text 0 (count text))
      (.newLine))))

(defn gen-specs-ns
  "Generates a formatted namespace string containing the supplied
  specs."
  [ns-symbol doc-string spec-forms]
  (let [string-writer (StringWriter.)]
    (with-open [writer (io/writer string-writer :encoding "UTF-8")]
      (write-form writer (ns-form ns-symbol doc-string))
      (run! (partial write-form writer) spec-forms))
    (-> string-writer .getBuffer str)))
