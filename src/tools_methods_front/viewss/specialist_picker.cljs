(ns tools-methods-front.viewss.specialist-picker
  (:require
   [tools-methods-front.subs :as subs]
   [tools-methods-front.events :as events]
   [re-frame.core :as re-frame]))

(defn specialist-card [specialist]
  (let [images ["/images/female-doc.png" "/images/male-doc.png"]
        random-image (rand-nth images)]
    [:div.specialist-card
     [:img {:src random-image
            :alt (:userr/name specialist)
            :class "specialist-image"}]
     [:h3 (:userr/name specialist)]
     [:p (:userr/specialty specialist)]]))

(defn specs []
  (let [specialists (re-frame/subscribe [::subs/specialists])
        spec-error (re-frame/subscribe [::subs/login-error])]
    (fn []

      (when @spec-error
        [:div.error @spec-error])

      [:div.specialist-picker
       [:div.spec-header
        [:h1 "Pick a specialist to chat with"]
        [:button.btn.light-btn {:on-click #(re-frame/dispatch [::events/load-specialists])} "Refresh"]]
       [:div.specialist-grid
        (for [specialist @specialists]
          ^{:key (:userr/id specialist)}
          [specialist-card specialist])]])))