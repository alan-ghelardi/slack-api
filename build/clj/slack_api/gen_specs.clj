(ns slack-api.gen-specs
  (:require [clojure.java.io :as io]
            [clojure.pprint :as pprint]
            [clojure.string :as string]
            [slack-api.misc :as misc]
            [slack-api.web-api :as web-api]))

(defn- wrap-in-schema-form  [req-data]
  (misc/map-vals (comp #(list 's/schema %) (partial misc/map-vals :doc/pred)) req-data))

(defn build-schema-forms [slack-methods]
  (->> slack-methods
   (misc/map-vals (comp wrap-in-schema-form #(select-keys % [:slack.req/headers :slack.req/payload :slack.req/query])))
       (map (fn [[method literal-map]]
              (list 's/def (web-api/spec-name method)
                    (list 's/schema literal-map))))
       (sort-by second)))

(defn ns-form [ns-name]
  (list 'ns ns-name
        (list :require '[clojure.spec-alpha2 :as s])))

(defn write-form [writer form]
  (let [text (with-out-str (pprint/pprint form))]
    (doto writer
      (.write text 0 (count text))
      (.newLine))))

(defn ns-symbol->file [ns-symbol]
  (let [parts (string/split (misc/snake-case (name ns-symbol)) #"\.")]
    (->> (update parts (dec (count parts)) #(str % ".clj"))
         (cons "src")
         (apply io/file))))

(defn gen-specs-ns [ns-symbol spec-forms]
  (let [file (ns-symbol->file ns-symbol)]
    (io/make-parents file)
    (with-open [writer (io/writer file :append false :encoding "UTF-8")]
      (write-form writer (ns-form ns-symbol))
      (run! (partial write-form writer) spec-forms))))

(comment
  (gen-specs-ns 'slack-api.specs.request
                (build-schema-forms (web-api/get-slack-methods))))
