(ns slack-api.open-api-parser
  (:require [clojure.data.json :as json]
            [clojure.java.io :as io]
            [clojure.string :as string]
            [slack-api.misc :as misc]))

(def read-open-api
  "Reads the Slack's OpenAPI specification from the supplied path."
  (comp json/read io/reader (partial io/file)))

(def json-types->predicate-symbols
  "Map of JSON types to their equivalent Clojure predicate symbols."
  {"boolean" 'boolean?
   "integer" 'int?
   "number"  'number?
   "string"  'string?})

(defn- resolve-predicate-symbol
  "Takes a JSON type (string, number, etc.) and returns its equivalent
  Clojure predicate symbol."
  [json-type]
  (or (json-types->predicate-symbols json-type)
      (throw (IllegalArgumentException. (format "Unsupported JSON type `%s`" json-type)))))

(def req-parameter-types->qualified-keywords
  "Map of request parameter types (such as form-data, header and query),
  to their qualified keywords (:slack.req/payload, :slack.req.headers
  and :slack.req/query)."
  {"formData" :slack.req/payload
   "header"   :slack.req/headers
   "query"    :slack.req/query})

(defn- resolve-req-qualified-key
  "Returns a qualified keyword (such as :slack.req.payload) for the
  parameter type (such as form-data) in question."
  [parameter-type]
  (or (req-parameter-types->qualified-keywords parameter-type)
      (throw (IllegalArgumentException. (format "Unknown parameter type `%s`" parameter-type)))))

(defn- parse-request-parameters
  "Converts the request parameters to their internal representation."
  [parameters]
  (let [parse-parameters #(reduce (fn [result {:keys [description name type]}]
                                    (assoc result
                                           (keyword (misc/kebab-case name))
                                           #:doc{:description description
                                                 :pred        (resolve-predicate-symbol type)}))
                                  {} %)]
    (->> parameters
         (group-by :in)
         (map (fn [[name parameters]]
                [(resolve-req-qualified-key name) (parse-parameters parameters)]))
         (into {}))))

(defn- parse-slack-endpoints
  "Takes an URL and, possibly, a set of existing Slack endpoints, and
  convert them to their internal representation."
  [url endpoints]
  (->> endpoints
       misc/dasherize-keys
       (map (fn [[verb endpoint]]
              (let [{:keys [description tags external-docs security consumes produces parameters]}
                    endpoint
                    request-params (parse-request-parameters parameters)]
                (merge {:doc/description          description
                        :doc/tags                 (set tags)
                        :doc/link                 (:url external-docs)
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
  (let [parts (->> (string/split path #"/|\.")
                   (remove string/blank?)
                   (map misc/kebab-case))]
    (keyword (str (string/join "." (butlast parts))
                  "/" (last parts)))))

(defn- url-components
  "Extract the base components for assembling an URL from the supplied
  open api."
  [open-api]
  {:scheme    (first (get open-api "schemes"))
   :host      (get open-api "host")
   :base-path (get open-api "basePath")})

(defn- parse-paths
  "Converts the `paths` property of the supplied OpenAPI object to a map
  of Slack methods."
  [open-api]
  (let [url-components (url-components open-api)]
    (->> (get open-api "paths")
         (map (fn [[path endpoints]]
                [(path->slack-method path)
                 (parse-slack-endpoints (url (assoc url-components :path path)) endpoints)]))
         (into {}))))

(defn parse
  "Parses the OpenAPI object and converts it to a Web API descriptor in
  the EDN format.

  The resulting data structure contains a set of attributes that will
  be read in runtime by `slack-api.web-api` namespace."
  [open-api]
  #:slack.api{:version (get-in open-api ["info" "version"])
              :methods (parse-paths open-api)})

(defn write-web-api
  [web-api dest]
  (spit dest
        (pr-str web-api)))

#_(defn -main
    "Reads the supplied Open API specification, converts it into an
  internal data structure and writes the resulting EDN to the
  resources path."
    [& [open-api-file]]
    (write-web-api  (parse-web-api (read-open-api open-api-file)) (io/file "resources/slack_api/web-api.edn")))
