(ns slack-api.specs.request (:require [clojure.spec-alpha2 :as s]))

(s/def :slack.admin.users.session.reset/req (s/schema {}))

(s/def
  :slack.api.test/req
  (s/schema
   #:slack.req{:query (s/schema {:foo string?, :error string?})}))

(s/def
  :slack.apps.permissions.info/req
  (s/schema #:slack.req{:query (s/schema {:token string?})}))

(s/def
  :slack.apps.permissions.request/req
  (s/schema
   #:slack.req{:query
               (s/schema
                {:scopes     string?,
                 :token      string?,
                 :trigger-id string?})}))

(s/def
  :slack.apps.permissions.resources.list/req
  (s/schema
   #:slack.req{:query
               (s/schema
                {:cursor string?, :token string?, :limit int?})}))

(s/def
  :slack.apps.permissions.scopes.list/req
  (s/schema #:slack.req{:query (s/schema {:token string?})}))

(s/def
  :slack.apps.permissions.users.list/req
  (s/schema
   #:slack.req{:query
               (s/schema
                {:cursor string?, :token string?, :limit int?})}))

(s/def
  :slack.apps.permissions.users.request/req
  (s/schema
   #:slack.req{:query
               (s/schema
                {:scopes     string?,
                 :token      string?,
                 :user       string?,
                 :trigger-id string?})}))

(s/def
  :slack.apps.uninstall/req
  (s/schema
   #:slack.req{:query
               (s/schema
                {:client-secret string?,
                 :token         string?,
                 :client-id     string?})}))

(s/def
  :slack.auth.revoke/req
  (s/schema
   #:slack.req{:query (s/schema {:test boolean?, :token string?})}))

(s/def :slack.auth.test/req (s/schema {}))

(s/def
  :slack.bots.info/req
  (s/schema
   #:slack.req{:query (s/schema {:token string?, :bot string?})}))

(s/def
  :slack.channels.archive/req
  (s/schema
   #:slack.req{:headers (s/schema {:token string?}),
               :payload (s/schema {:channel string?})}))

(s/def
  :slack.channels.create/req
  (s/schema
   #:slack.req{:headers (s/schema {:token string?}),
               :payload (s/schema {:validate boolean?, :name string?})}))

(s/def
  :slack.channels.history/req
  (s/schema
   #:slack.req{:query
               (s/schema
                {:count     int?,
                 :unreads   boolean?,
                 :inclusive boolean?,
                 :token     string?,
                 :oldest    number?,
                 :channel   string?,
                 :latest    number?})}))

(s/def
  :slack.channels.info/req
  (s/schema
   #:slack.req{:query
               (s/schema
                {:token          string?,
                 :include-locale boolean?,
                 :channel        string?})}))

(s/def
  :slack.channels.invite/req
  (s/schema
   #:slack.req{:headers (s/schema {:token string?}),
               :payload (s/schema {:user string?, :channel string?})}))

(s/def
  :slack.channels.join/req
  (s/schema
   #:slack.req{:headers (s/schema {:token string?}),
               :payload (s/schema {:validate boolean?, :name string?})}))

(s/def
  :slack.channels.kick/req
  (s/schema
   #:slack.req{:headers (s/schema {:token string?}),
               :payload (s/schema {:user string?, :channel string?})}))

(s/def
  :slack.channels.leave/req
  (s/schema
   #:slack.req{:headers (s/schema {:token string?}),
               :payload (s/schema {:channel string?})}))

(s/def
  :slack.channels.list/req
  (s/schema
   #:slack.req{:query
               (s/schema
                {:exclude-members  boolean?,
                 :cursor           string?,
                 :token            string?,
                 :limit            int?,
                 :exclude-archived boolean?})}))

(s/def
  :slack.channels.mark/req
  (s/schema
   #:slack.req{:headers (s/schema {:token string?}),
               :payload (s/schema {:ts number?, :channel string?})}))

(s/def
  :slack.channels.rename/req
  (s/schema
   #:slack.req{:headers (s/schema {:token string?}),
               :payload
               (s/schema
                {:validate boolean?, :name string?, :channel string?})}))

(s/def
  :slack.channels.replies/req
  (s/schema
   #:slack.req{:query
               (s/schema
                {:thread-ts number?,
                 :token     string?,
                 :channel   string?})}))

(s/def
  :slack.channels.setPurpose/req
  (s/schema
   #:slack.req{:headers (s/schema {:token string?}),
               :payload
               (s/schema {:purpose string?, :channel string?})}))

(s/def
  :slack.channels.setTopic/req
  (s/schema
   #:slack.req{:headers (s/schema {:token string?}),
               :payload (s/schema {:topic string?, :channel string?})}))

(s/def
  :slack.channels.unarchive/req
  (s/schema
   #:slack.req{:headers (s/schema {:token string?}),
               :payload (s/schema {:channel string?})}))

(s/def
  :slack.chat.delete/req
  (s/schema
   #:slack.req{:headers (s/schema {:token string?}),
               :payload
               (s/schema
                {:as-user boolean?, :ts number?, :channel string?})}))

(s/def :slack.chat.deleteScheduledMessage/req (s/schema {}))

(s/def
  :slack.chat.getPermalink/req
  (s/schema
   #:slack.req{:query
               (s/schema
                {:token      string?,
                 :message-ts number?,
                 :channel    string?})}))

(s/def
  :slack.chat.meMessage/req
  (s/schema
   #:slack.req{:headers (s/schema {:token string?}),
               :payload (s/schema {:text string?, :channel string?})}))

(s/def
  :slack.chat.postEphemeral/req
  (s/schema
   #:slack.req{:headers (s/schema {:token string?}),
               :payload
               (s/schema
                {:link-names  boolean?,
                 :attachments string?,
                 :channel     string?,
                 :parse       string?,
                 :thread-ts   number?,
                 :blocks      string?,
                 :as-user     boolean?,
                 :user        string?,
                 :text        string?})}))

(s/def
  :slack.chat.postMessage/req
  (s/schema
   #:slack.req{:headers (s/schema {:token string?}),
               :payload
               (s/schema
                {:mrkdwn          boolean?,
                 :link-names      boolean?,
                 :unfurl-links    boolean?,
                 :attachments     string?,
                 :username        string?,
                 :channel         string?,
                 :icon-url        string?,
                 :unfurl-media    boolean?,
                 :parse           string?,
                 :reply-broadcast boolean?,
                 :thread-ts       number?,
                 :blocks          string?,
                 :icon-emoji      string?,
                 :as-user         boolean?,
                 :text            string?})}))

(s/def
  :slack.chat.scheduleMessage/req
  (s/schema
   #:slack.req{:headers (s/schema {:token string?}),
               :payload
               (s/schema
                {:link-names      boolean?,
                 :unfurl-links    boolean?,
                 :post-at         string?,
                 :attachments     string?,
                 :channel         string?,
                 :unfurl-media    boolean?,
                 :parse           string?,
                 :reply-broadcast boolean?,
                 :thread-ts       number?,
                 :blocks          string?,
                 :as-user         boolean?,
                 :text            string?})}))

(s/def
  :slack.chat.scheduledMessages.list/req
  (s/schema
   #:slack.req{:headers (s/schema {:token string?}),
               :query
               (s/schema
                {:cursor  string?,
                 :limit   int?,
                 :oldest  number?,
                 :channel string?,
                 :latest  number?})}))

(s/def
  :slack.chat.unfurl/req
  (s/schema
   #:slack.req{:headers (s/schema {:token string?}),
               :payload
               (s/schema
                {:user-auth-message  string?,
                 :user-auth-required boolean?,
                 :unfurls            string?,
                 :ts                 string?,
                 :user-auth-url      string?,
                 :channel            string?})}))

(s/def
  :slack.chat.update/req
  (s/schema
   #:slack.req{:headers (s/schema {:token string?}),
               :payload
               (s/schema
                {:blocks      string?,
                 :attachments string?,
                 :text        string?,
                 :ts          number?,
                 :parse       string?,
                 :as-user     boolean?,
                 :link-names  boolean?,
                 :channel     string?})}))

(s/def
  :slack.conversations.archive/req
  (s/schema
   #:slack.req{:headers (s/schema {:token string?}),
               :payload (s/schema {:channel string?})}))

(s/def
  :slack.conversations.close/req
  (s/schema
   #:slack.req{:headers (s/schema {:token string?}),
               :payload (s/schema {:channel string?})}))

(s/def
  :slack.conversations.create/req
  (s/schema
   #:slack.req{:headers (s/schema {:token string?}),
               :payload
               (s/schema
                {:user-ids   string?,
                 :name       string?,
                 :is-private boolean?})}))

(s/def
  :slack.conversations.history/req
  (s/schema
   #:slack.req{:query
               (s/schema
                {:inclusive boolean?,
                 :cursor    string?,
                 :token     string?,
                 :limit     int?,
                 :oldest    number?,
                 :channel   string?,
                 :latest    number?})}))

(s/def
  :slack.conversations.info/req
  (s/schema
   #:slack.req{:query
               (s/schema
                {:include-num-members boolean?,
                 :token               string?,
                 :channel             string?,
                 :include-locale      boolean?})}))

(s/def
  :slack.conversations.invite/req
  (s/schema
   #:slack.req{:headers (s/schema {:token string?}),
               :payload (s/schema {:users string?, :channel string?})}))

(s/def
  :slack.conversations.join/req
  (s/schema
   #:slack.req{:headers (s/schema {:token string?}),
               :payload (s/schema {:channel string?})}))

(s/def
  :slack.conversations.kick/req
  (s/schema
   #:slack.req{:headers (s/schema {:token string?}),
               :payload (s/schema {:user string?, :channel string?})}))

(s/def
  :slack.conversations.leave/req
  (s/schema
   #:slack.req{:headers (s/schema {:token string?}),
               :payload (s/schema {:channel string?})}))

(s/def
  :slack.conversations.list/req
  (s/schema
   #:slack.req{:query
               (s/schema
                {:cursor           string?,
                 :token            string?,
                 :limit            int?,
                 :exclude-archived boolean?,
                 :types            string?})}))

(s/def
  :slack.conversations.members/req
  (s/schema
   #:slack.req{:query
               (s/schema
                {:cursor  string?,
                 :token   string?,
                 :limit   int?,
                 :channel string?})}))

(s/def
  :slack.conversations.open/req
  (s/schema
   #:slack.req{:headers (s/schema {:token string?}),
               :payload
               (s/schema
                {:return-im boolean?,
                 :users     string?,
                 :channel   string?})}))

(s/def
  :slack.conversations.rename/req
  (s/schema
   #:slack.req{:headers (s/schema {:token string?}),
               :payload (s/schema {:name string?, :channel string?})}))

(s/def
  :slack.conversations.replies/req
  (s/schema
   #:slack.req{:query
               (s/schema
                {:inclusive boolean?,
                 :ts        number?,
                 :cursor    string?,
                 :token     string?,
                 :limit     int?,
                 :oldest    number?,
                 :channel   string?,
                 :latest    number?})}))

(s/def
  :slack.conversations.setPurpose/req
  (s/schema
   #:slack.req{:headers (s/schema {:token string?}),
               :payload
               (s/schema {:purpose string?, :channel string?})}))

(s/def
  :slack.conversations.setTopic/req
  (s/schema
   #:slack.req{:headers (s/schema {:token string?}),
               :payload (s/schema {:topic string?, :channel string?})}))

(s/def
  :slack.conversations.unarchive/req
  (s/schema
   #:slack.req{:headers (s/schema {:token string?}),
               :payload (s/schema {:channel string?})}))

(s/def :slack.dialog.open/req (s/schema {}))

(s/def :slack.dnd.endDnd/req (s/schema {}))

(s/def :slack.dnd.endSnooze/req (s/schema {}))

(s/def
  :slack.dnd.info/req
  (s/schema
   #:slack.req{:query (s/schema {:token string?, :user string?})}))

(s/def :slack.dnd.setSnooze/req (s/schema {}))

(s/def
  :slack.dnd.teamInfo/req
  (s/schema
   #:slack.req{:query (s/schema {:token string?, :users string?})}))

(s/def
  :slack.emoji.list/req
  (s/schema #:slack.req{:query (s/schema {:token string?})}))

(s/def
  :slack.files.comments.delete/req
  (s/schema
   #:slack.req{:headers (s/schema {:token string?}),
               :payload (s/schema {:id string?, :file string?})}))

(s/def
  :slack.files.delete/req
  (s/schema
   #:slack.req{:headers (s/schema {:token string?}),
               :payload (s/schema {:file string?})}))

(s/def
  :slack.files.info/req
  (s/schema
   #:slack.req{:query
               (s/schema
                {:count  string?,
                 :cursor string?,
                 :token  string?,
                 :limit  int?,
                 :file   string?,
                 :page   string?})}))

(s/def
  :slack.files.list/req
  (s/schema
   #:slack.req{:query
               (s/schema
                {:count   string?,
                 :channel string?,
                 :ts-to   number?,
                 :ts-from number?,
                 :token   string?,
                 :user    string?,
                 :page    string?,
                 :types   string?})}))

(s/def
  :slack.files.revokePublicURL/req
  (s/schema
   #:slack.req{:headers (s/schema {:token string?}),
               :payload (s/schema {:file string?})}))

(s/def
  :slack.files.sharedPublicURL/req
  (s/schema
   #:slack.req{:headers (s/schema {:token string?}),
               :payload (s/schema {:file string?})}))

(s/def
  :slack.files.upload/req
  (s/schema
   #:slack.req{:payload
               (s/schema
                {:initial-comment string?,
                 :channels        string?,
                 :content         string?,
                 :file            string?,
                 :title           string?,
                 :token           string?,
                 :filename        string?,
                 :thread-ts       number?,
                 :filetype        string?})}))

(s/def
  :slack.groups.archive/req
  (s/schema
   #:slack.req{:headers (s/schema {:token string?}),
               :payload (s/schema {:channel string?})}))

(s/def
  :slack.groups.create/req
  (s/schema
   #:slack.req{:headers (s/schema {:token string?}),
               :payload (s/schema {:validate boolean?, :name string?})}))

(s/def
  :slack.groups.createChild/req
  (s/schema
   #:slack.req{:payload (s/schema {:token string?, :channel string?})}))

(s/def
  :slack.groups.history/req
  (s/schema
   #:slack.req{:query
               (s/schema
                {:count     int?,
                 :unreads   boolean?,
                 :inclusive boolean?,
                 :token     string?,
                 :oldest    number?,
                 :channel   string?,
                 :latest    number?})}))

(s/def
  :slack.groups.info/req
  (s/schema
   #:slack.req{:query
               (s/schema
                {:token          string?,
                 :include-locale boolean?,
                 :channel        string?})}))

(s/def
  :slack.groups.invite/req
  (s/schema
   #:slack.req{:headers (s/schema {:token string?}),
               :payload (s/schema {:user string?, :channel string?})}))

(s/def
  :slack.groups.kick/req
  (s/schema
   #:slack.req{:headers (s/schema {:token string?}),
               :payload (s/schema {:user string?, :channel string?})}))

(s/def
  :slack.groups.leave/req
  (s/schema
   #:slack.req{:headers (s/schema {:token string?}),
               :payload (s/schema {:channel string?})}))

(s/def
  :slack.groups.list/req
  (s/schema
   #:slack.req{:query
               (s/schema
                {:cursor           string?,
                 :exclude-members  boolean?,
                 :token            string?,
                 :exclude-archived boolean?,
                 :limit            int?})}))

(s/def
  :slack.groups.mark/req
  (s/schema
   #:slack.req{:headers (s/schema {:token string?}),
               :payload (s/schema {:ts number?, :channel string?})}))

(s/def
  :slack.groups.open/req
  (s/schema
   #:slack.req{:headers (s/schema {:token string?}),
               :payload (s/schema {:channel string?})}))

(s/def
  :slack.groups.rename/req
  (s/schema
   #:slack.req{:headers (s/schema {:token string?}),
               :payload
               (s/schema
                {:validate boolean?, :name string?, :channel string?})}))

(s/def
  :slack.groups.replies/req
  (s/schema
   #:slack.req{:query
               (s/schema
                {:thread-ts number?,
                 :token     string?,
                 :channel   string?})}))

(s/def
  :slack.groups.setPurpose/req
  (s/schema
   #:slack.req{:headers (s/schema {:token string?}),
               :payload
               (s/schema {:purpose string?, :channel string?})}))

(s/def
  :slack.groups.setTopic/req
  (s/schema
   #:slack.req{:headers (s/schema {:token string?}),
               :payload (s/schema {:topic string?, :channel string?})}))

(s/def
  :slack.groups.unarchive/req
  (s/schema
   #:slack.req{:headers (s/schema {:token string?}),
               :payload (s/schema {:channel string?})}))

(s/def :slack.im.close/req (s/schema {}))

(s/def
  :slack.im.history/req
  (s/schema
   #:slack.req{:query
               (s/schema
                {:count     int?,
                 :unreads   boolean?,
                 :inclusive boolean?,
                 :token     string?,
                 :oldest    number?,
                 :channel   string?,
                 :latest    number?})}))

(s/def
  :slack.im.list/req
  (s/schema
   #:slack.req{:query
               (s/schema
                {:cursor string?, :token string?, :limit int?})}))

(s/def
  :slack.im.mark/req
  (s/schema
   #:slack.req{:headers (s/schema {:token string?}),
               :payload (s/schema {:channel string?, :ts number?})}))

(s/def
  :slack.im.open/req
  (s/schema
   #:slack.req{:headers (s/schema {:token string?}),
               :payload
               (s/schema
                {:return-im      boolean?,
                 :user           string?,
                 :include-locale boolean?})}))

(s/def
  :slack.im.replies/req
  (s/schema
   #:slack.req{:query
               (s/schema
                {:thread-ts number?,
                 :token     string?,
                 :channel   string?})}))

(s/def :slack.migration.exchange/req (s/schema {}))

(s/def
  :slack.mpim.close/req
  (s/schema
   #:slack.req{:headers (s/schema {:token string?}),
               :payload (s/schema {:channel string?})}))

(s/def
  :slack.mpim.history/req
  (s/schema
   #:slack.req{:query
               (s/schema
                {:count     int?,
                 :unreads   boolean?,
                 :inclusive boolean?,
                 :token     string?,
                 :oldest    number?,
                 :channel   string?,
                 :latest    number?})}))

(s/def
  :slack.mpim.list/req
  (s/schema
   #:slack.req{:query
               (s/schema
                {:cursor string?, :token string?, :limit int?})}))

(s/def
  :slack.mpim.mark/req
  (s/schema
   #:slack.req{:headers (s/schema {:token string?}),
               :payload (s/schema {:ts number?, :channel string?})}))

(s/def
  :slack.mpim.open/req
  (s/schema
   #:slack.req{:headers (s/schema {:token string?}),
               :payload (s/schema {:users string?})}))

(s/def
  :slack.mpim.replies/req
  (s/schema
   #:slack.req{:query
               (s/schema
                {:thread-ts number?,
                 :token     string?,
                 :channel   string?})}))

(s/def
  :slack.oauth.access/req
  (s/schema
   #:slack.req{:query
               (s/schema
                {:code           string?,
                 :redirect-uri   string?,
                 :client-id      string?,
                 :client-secret  string?,
                 :single-channel boolean?})}))

(s/def
  :slack.oauth.token/req
  (s/schema
   #:slack.req{:query
               (s/schema
                {:client-secret  string?,
                 :code           string?,
                 :single-channel boolean?,
                 :client-id      string?,
                 :redirect-uri   string?})}))

(s/def
  :slack.pins.add/req
  (s/schema
   #:slack.req{:headers (s/schema {:token string?}),
               :payload
               (s/schema
                {:file-comment string?,
                 :timestamp    number?,
                 :file         string?,
                 :channel      string?})}))

(s/def
  :slack.pins.list/req
  (s/schema
   #:slack.req{:query (s/schema {:token string?, :channel string?})}))

(s/def
  :slack.pins.remove/req
  (s/schema
   #:slack.req{:headers (s/schema {:token string?}),
               :payload
               (s/schema
                {:file-comment string?,
                 :timestamp    number?,
                 :file         string?,
                 :channel      string?})}))

(s/def
  :slack.reactions.add/req
  (s/schema
   #:slack.req{:headers (s/schema {:token string?}),
               :payload
               (s/schema
                {:name         string?,
                 :file-comment string?,
                 :timestamp    number?,
                 :file         string?,
                 :channel      string?})}))

(s/def
  :slack.reactions.get/req
  (s/schema
   #:slack.req{:query
               (s/schema
                {:full         boolean?,
                 :file-comment string?,
                 :timestamp    number?,
                 :token        string?,
                 :file         string?,
                 :channel      string?})}))

(s/def
  :slack.reactions.list/req
  (s/schema
   #:slack.req{:query
               (s/schema
                {:count  string?,
                 :full   boolean?,
                 :cursor string?,
                 :token  string?,
                 :limit  int?,
                 :user   string?,
                 :page   string?})}))

(s/def
  :slack.reactions.remove/req
  (s/schema
   #:slack.req{:headers (s/schema {:token string?}),
               :payload
               (s/schema
                {:name         string?,
                 :file-comment string?,
                 :timestamp    number?,
                 :file         string?,
                 :channel      string?})}))

(s/def
  :slack.reminders.add/req
  (s/schema
   #:slack.req{:headers (s/schema {:token string?}),
               :payload
               (s/schema
                {:text string?, :user string?, :time string?})}))

(s/def
  :slack.reminders.complete/req
  (s/schema
   #:slack.req{:headers (s/schema {:token string?}),
               :payload (s/schema {:reminder string?})}))

(s/def
  :slack.reminders.delete/req
  (s/schema
   #:slack.req{:headers (s/schema {:token string?}),
               :payload (s/schema {:reminder string?})}))

(s/def
  :slack.reminders.info/req
  (s/schema
   #:slack.req{:query (s/schema {:token string?, :reminder string?})}))

(s/def
  :slack.reminders.list/req
  (s/schema #:slack.req{:query (s/schema {:token string?})}))

(s/def
  :slack.rtm.connect/req
  (s/schema
   #:slack.req{:query
               (s/schema
                {:presence-sub         boolean?,
                 :token                string?,
                 :batch-presence-aware boolean?})}))

(s/def
  :slack.search.messages/req
  (s/schema
   #:slack.req{:query
               (s/schema
                {:sort-dir  string?,
                 :query     string?,
                 :sort      string?,
                 :count     string?,
                 :token     string?,
                 :highlight boolean?,
                 :page      string?})}))

(s/def
  :slack.stars.add/req
  (s/schema
   #:slack.req{:headers (s/schema {:token string?}),
               :payload
               (s/schema
                {:file-comment string?,
                 :timestamp    number?,
                 :channel      string?,
                 :file         string?})}))

(s/def
  :slack.stars.list/req
  (s/schema
   #:slack.req{:query
               (s/schema
                {:count  string?,
                 :cursor string?,
                 :token  string?,
                 :limit  int?,
                 :page   string?})}))

(s/def
  :slack.stars.remove/req
  (s/schema
   #:slack.req{:headers (s/schema {:token string?}),
               :payload
               (s/schema
                {:file-comment string?,
                 :timestamp    number?,
                 :channel      string?,
                 :file         string?})}))

(s/def
  :slack.team.accessLogs/req
  (s/schema
   #:slack.req{:query
               (s/schema
                {:count  string?,
                 :token  string?,
                 :page   string?,
                 :before int?})}))

(s/def :slack.team.billableInfo/req (s/schema {}))

(s/def
  :slack.team.info/req
  (s/schema
   #:slack.req{:query (s/schema {:token string?, :team string?})}))

(s/def
  :slack.team.integrationLogs/req
  (s/schema
   #:slack.req{:query
               (s/schema
                {:count       string?,
                 :change-type string?,
                 :app-id      int?,
                 :token       string?,
                 :user        string?,
                 :service-id  int?,
                 :page        string?})}))

(s/def
  :slack.team.profile.get/req
  (s/schema
   #:slack.req{:query (s/schema {:token string?, :visibility string?})}))

(s/def
  :slack.usergroups.create/req
  (s/schema
   #:slack.req{:headers (s/schema {:token string?}),
               :payload
               (s/schema
                {:handle        string?,
                 :description   string?,
                 :channels      string?,
                 :include-count boolean?,
                 :name          string?})}))

(s/def
  :slack.usergroups.disable/req
  (s/schema
   #:slack.req{:headers (s/schema {:token string?}),
               :payload
               (s/schema
                {:include-count boolean?, :usergroup string?})}))

(s/def
  :slack.usergroups.enable/req
  (s/schema
   #:slack.req{:headers (s/schema {:token string?}),
               :payload
               (s/schema
                {:include-count boolean?, :usergroup string?})}))

(s/def
  :slack.usergroups.list/req
  (s/schema
   #:slack.req{:query
               (s/schema
                {:include-users    boolean?,
                 :token            string?,
                 :include-count    boolean?,
                 :include-disabled boolean?})}))

(s/def
  :slack.usergroups.update/req
  (s/schema
   #:slack.req{:headers (s/schema {:token string?}),
               :payload
               (s/schema
                {:handle        string?,
                 :description   string?,
                 :channels      string?,
                 :include-count boolean?,
                 :usergroup     string?,
                 :name          string?})}))

(s/def :slack.usergroups.users.list/req (s/schema {}))

(s/def
  :slack.usergroups.users.update/req
  (s/schema
   #:slack.req{:headers (s/schema {:token string?}),
               :payload
               (s/schema
                {:users string?,
                 :include-count boolean?,
                 :usergroup string?})}))

(s/def
  :slack.users.conversations/req
  (s/schema
   #:slack.req{:query
               (s/schema
                {:cursor string?,
                 :token string?,
                 :limit int?,
                 :user string?,
                 :exclude-archived boolean?,
                 :types string?})}))

(s/def
  :slack.users.deletePhoto/req
  (s/schema #:slack.req{:payload (s/schema {:token string?})}))

(s/def
  :slack.users.getPresence/req
  (s/schema
   #:slack.req{:query (s/schema {:token string?, :user string?})}))

(s/def
  :slack.users.identity/req
  (s/schema #:slack.req{:query (s/schema {:token string?})}))

(s/def
  :slack.users.info/req
  (s/schema
   #:slack.req{:query
               (s/schema
                {:token string?,
                 :user string?,
                 :include-locale boolean?})}))

(s/def
  :slack.users.list/req
  (s/schema
   #:slack.req{:query
               (s/schema
                {:cursor string?,
                 :token string?,
                 :limit int?,
                 :include-locale boolean?})}))

(s/def
  :slack.users.lookupByEmail/req
  (s/schema
   #:slack.req{:query (s/schema {:token string?, :email string?})}))

(s/def
  :slack.users.profile.get/req
  (s/schema
   #:slack.req{:query
               (s/schema
                {:token string?,
                 :include-labels boolean?,
                 :user string?})}))

(s/def
  :slack.users.profile.set/req
  (s/schema
   #:slack.req{:headers (s/schema {:token string?}),
               :payload
               (s/schema
                {:profile string?,
                 :user string?,
                 :value string?,
                 :name string?})}))

(s/def
  :slack.users.setActive/req
  (s/schema #:slack.req{:headers (s/schema {:token string?})}))

(s/def
  :slack.users.setPhoto/req
  (s/schema
   #:slack.req{:payload
               (s/schema
                {:image string?,
                 :crop-w int?,
                 :token string?,
                 :crop-y int?,
                 :crop-x int?})}))

(s/def
  :slack.users.setPresence/req
  (s/schema
   #:slack.req{:headers (s/schema {:token string?}),
               :payload (s/schema {:presence string?})}))
