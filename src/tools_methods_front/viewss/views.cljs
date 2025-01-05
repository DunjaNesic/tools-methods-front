(ns tools-methods-front.viewss.views
  (:require
   [tools-methods-front.viewss.one-to-one :refer [one-to-one-chat-panel]]
   [tools-methods-front.viewss.group-chat :refer [group-chat-panel]]
   [tools-methods-front.viewss.personalized-treatment :refer [personalized-treatment-panel]]
   [tools-methods-front.viewss.symptom-checker :refer [symptom-checker-panel]]
   [tools-methods-front.viewss.chatbot :refer [chatbot-panel]]))

(defn main-panel []
  [:div.main-panel
   [:h2 "Welcome to Tools&Methods Project"]
   [one-to-one-chat-panel]
   [:hr]
   [group-chat-panel]
   [:hr]
   [symptom-checker-panel]
   [:hr]
   [personalized-treatment-panel]
   [:hr]
   [chatbot-panel]])
