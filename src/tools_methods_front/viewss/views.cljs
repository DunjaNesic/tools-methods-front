(ns tools-methods-front.viewss.views
  (:require
   [tools-methods-front.viewss.one-to-one :refer [one-to-one-chat-panel]]
   [tools-methods-front.viewss.group-chat :refer [group-chat-panel]]))

(defn main-panel []
  [:div.main-panel
   [:h2 "Welcome to Tools&Methods Project"]
   [one-to-one-chat-panel]
   [:hr]
   [group-chat-panel]])
