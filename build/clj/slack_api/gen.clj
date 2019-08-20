(ns slack-api.gen
  (:require [clojure.java.io :as io]
            [slack-api.open-api-parser :as parser]))

(def descriptor-file (io/file "resources" "slack_api" "web_api.edn"))

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

(defn -main [& [open-api-spec]]
  {:pre [open-api-spec]}
  (gen-web-api-descriptor open-api-spec))
