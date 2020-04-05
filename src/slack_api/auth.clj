(ns slack-api.auth
  (:require [clojure.edn :as edn]
            [clojure.java.io :as io]
            [slack-api.misc :as misc]))

(def read-token-from-options
  "Reads the oauth token by getting the value from the key
  `:slack.auth/token` in the supplied map."
  (partial :slack.auth/token))

(defn read-token-from-env
  "Attempts to reads the oauth token from the environment variable
  `SLACK_OAUTH_TOKEN`."
  []
  (misc/get-env "SLACK_OAUTH_TOKEN"))

(defn home-dir
  "Returns a file object representing the user's home dir."
  []
  (io/file (System/getProperty "user.home")))

(defn file-exists?
  "Returns true if the file exists or false otherwise."
  [file]
  (.exists file))

(defn read-token-from-edn-file
  "Attempts to read the oauth token from an EDN file named .slack.edn
  located at the home directory of the current user."
  []
  (let [credentials-file (io/file (home-dir) ".slack.edn")]
    (when (file-exists? credentials-file)
      (read-token-from-options (edn/read-string (slurp credentials-file))))))

(defn read-oauth-token
  [options]
  (some #(when-let [oauth-token (%)]
           oauth-token)
        [(partial read-token-from-options options)
         read-token-from-env
         read-token-from-edn-file]))
