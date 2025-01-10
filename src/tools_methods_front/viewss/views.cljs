(ns tools-methods-front.viewss.views
  (:require
   [tools-methods-front.viewss.one-to-one :refer [one-to-one-chat-panel]]
   [tools-methods-front.viewss.group-chat :refer [group-chat-panel]]
   [tools-methods-front.viewss.navbar :refer [nav]]
   [tools-methods-front.viewss.symptom-history :refer [symptom-history-panel]]
   [tools-methods-front.viewss.specialist-picker :refer [specs]]
   [tools-methods-front.viewss.personalized-treatment :refer [personalized-treatment-panel]]
   [tools-methods-front.viewss.symptom-checker :refer [symptom-checker-panel]]
   [tools-methods-front.viewss.chatbot :refer [chatbot-panel]]))

(defn main-panel []
  [:div.main-panel
   [nav]
   [symptom-checker-panel]
   [specs]
   [personalized-treatment-panel]
   [symptom-history-panel]
   [chatbot-panel]
   [:hr]
   [one-to-one-chat-panel]
   [:hr]
   [group-chat-panel]])
