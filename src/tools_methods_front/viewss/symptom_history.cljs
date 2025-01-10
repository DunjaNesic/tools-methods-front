(ns tools-methods-front.viewss.symptom-history
  (:require
   [tools-methods-front.subs :as subs]
   [tools-methods-front.events :as events]
   [re-frame.core :as re-frame]
   [clojure.string :as str]))

(defn history-card [{:keys [date_time symptom]}]
  (let [date-time (js/Date. date_time)
        time (.toLocaleTimeString date-time "sr-RS" #js {:hour "2-digit" :minute "2-digit"})
        date (.toLocaleDateString date-time "sr-RS" #js {:year "numeric" :month "2-digit" :day "2-digit"})]
    [:div.history-card
     [:div.history-time
      [:h1 time]
      [:h1 date]]
     [:div.history-symptoms
      [:p (str/join ", " (str/split symptom #", "))]]]))

(defn symptom-history-panel []
  (let [user-id @(re-frame/subscribe [::subs/user-id])
        history @(re-frame/subscribe [::subs/symptom-history])]

    (when (and user-id (nil? history))
      (re-frame/dispatch [::events/get-history user-id]))

    [:div.symptom-history-container
     [:h1.title "HISTORY OF ALL THE SYMTOMS YOU EVER HAD"]
     [:div.history-wrapper
      (if (seq history)
        (for [entry history]
          ^{:key (:id entry)} [history-card entry])
        [:p "No symptom history available."])]
     [:img.pattern {:src "/images/layered-steps-haikei.png" :alt "Pattern"}]]))
