(ns slack-api.gen
  "EDN and code generator."
  (:require [clojure.java.io :as io]
            [slack-api.open-api-parser :as parser]
            [slack-api.specs-builder :as specs-builder]
            [slack-api.web-api :as web-api]))

(def descriptor-file
  "EDN file that describes all Slack methods. See also
  slack-api.web-api."
  (io/file "resources" "slack_api" "web_api.edn"))

(defn gen-spec-requests-ns
  "Generates a namespace file containing specs for request parameters
  accepted by all Slack methods."
  []
  (let [ns-symbol 'slack-api.specs.request
        ns-file   (specs-builder/ns-symbol->file ns-symbol)]
    (printf "Generating %s namespace at %s...%n" (name ns-symbol) (.getPath ns-file))
    (spit ns-file
          (specs-builder/gen-specs-ns ns-symbol "Auto-generated specs for Slack's requests. Do not modify this file manually."
                                      (specs-builder/build-schema-forms (web-api/get-slack-methods))))
    (println "Done")))

(defn- write-descriptor
  [descriptor]
  (spit descriptor-file
        (pr-str descriptor)))

(defn gen-web-api-descriptor
  "Reads the supplied OpenAPI specification, converts it to an internal
  descriptor in the EDN format and writes the resulting file to the
  resources path."
  [open-api-spec]
  (printf "Generating %s...%n" (.getPath descriptor-file))
  (write-descriptor (parser/parse (parser/read-open-api open-api-spec)))
  (println "Done"))

(defn -main
  "Call via build/generate.sh."
  [& [open-api-spec]]
  {:pre [open-api-spec]}
  (gen-web-api-descriptor open-api-spec)
  (gen-spec-requests-ns))
