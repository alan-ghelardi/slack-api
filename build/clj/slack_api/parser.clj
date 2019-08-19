(ns slack-api.parser
  (:require [clojure.data.json :as json]
            [clojure.set :as set]
            [clojure.java.io :as io]
            [clojure.string :as string]
            [slack-api.misc :as misc]))

(def read-open-api*
  (comp json/read io/reader #(io/resource "open-api.json")))

(def read-open-api
  (memoize read-open-api*))

(def json-types->predicate-symbols
  "Map of JSON types to their equivalent Clojure predicate symbols."
  {"boolean" 'boolean?
   "integer" 'int?
   "number"  'number?
   "string"  'string?})

(defn- resolve-predicate-symbol
  [json-type]
  (or (json-types->predicate-symbols json-type)
      (throw (IllegalArgumentException. (format "Unsupported JSON type `%s`" json-type)))))

(def parameter-types->keywords
  {"formData" :slack.req/payload
   "header"   :slack.req/headers
   "query"    :slack.req/query})

(defn- resolve-parameter-group
  [param-type]
  (or (parameter-types->keywords param-type)
      (throw (IllegalArgumentException. (format "Unknown parameter type `%s`" param-type)))))

(defn- parse-parameters
  [parameters]
  (reduce (fn [result {:keys [description name type]}]
            (assoc result
                   (keyword (misc/kebab-case name))
                   #:doc{:description description
                          :pred        (resolve-predicate-symbol type)}))
          {} parameters))

(defn- parse-request-parameters
  [parameters]
  (->> parameters
       (group-by :in)
       (map (fn [[name parameters]]
              [(resolve-parameter-group name) (parse-parameters parameters)]))
       (into {})))

(defn- parse-slack-methods [url methods]
  (->> methods
       misc/dasherize-keys
       (map (fn [[verb slack-method]]
              (let [{:keys [description tags externaldocs security consumes produces parameters]}
                    slack-method
                    request-params (parse-request-parameters parameters)]
                (merge {:doc/description         description
                        :doc/tags                (set tags)
                        :doc/link                (:url externaldocs)
                        :endpoint/url             url
                        :endpoint/verb            verb
                        :endpoint/required-scopes (set (flatten (map vals security)))
                        :endpoint/consumes        (set consumes)
                        :endpoint/produces        (set produces)}
                       request-params))))
       (into {})))

(defn- url
  "Assembles the URL from its constituent parts."
  [{:keys [scheme host base-path path]}]
  (str scheme "://" host base-path path))

(defn path->slack-method
  "Turns a relative path into a namespaced keyword representing a Slack
  method."
  [path]
  (let [parts (remove string/blank? (string/split path #"/|\."))]
    (keyword (str (string/join "." (butlast parts))
                  "/" (last parts)))))

(defn- url-components
  "Extract the base components for assembling an URL from the supplied
  open api."
  [open-api]
  {:scheme    (first (get open-api "schemes"))
   :host      (get open-api "host")
   :base-path (get open-api "basePath")})

(defn parse-paths
  [open-api]
  (let [url-components (url-components open-api)]
    (->> (get open-api "paths")
         (map (fn [[path methods]]
                [(path->slack-method path)
                 (parse-slack-methods (url (assoc url-components :path path)) methods)]))
         (into {}))))

(defn parse-web-api [open-api]
  #:slack.api{:version (get-in open-api ["info" "version"])
              :methods (parse-paths open-api)})

(defn write-web-api
  [web-api dest]
  (spit dest
        (pr-str web-api)))

(comment
  (write-web-api  (parse-web-api (read-open-api)) (io/file "resources/slack_api/web-api.edn")))