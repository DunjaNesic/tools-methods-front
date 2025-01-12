(ns tools-methods-front.viewss.patient-picker
  (:require
   [reagent.core :as reagent]
   [tools-methods-front.subs :as subs]
   [tools-methods-front.events :as events]
   [re-frame.core :as re-frame]))

(defn patient-card [patient]
  (let [images ["/images/patient1.png" "/images/patient2.png"]
        random-image (rand-nth images)]
    [:div.specialist-card
     {:on-click #(do
                   (re-frame/dispatch [::events/update-1to1-receiver (:userr/email patient)])
                   (re-frame/dispatch [::events/start-1to1])
                   (re-frame/dispatch [::events/show-1to1-messages]))}
     [:img {:src random-image
            :alt (:userr/name patient)
            :class "specialist-image"}]
     [:h3 (:userr/name patient)]]))

(defn patients []
  (let [patients (re-frame/subscribe [::subs/patients])]

    (reagent.core/after-render
     (fn []
       (re-frame/dispatch [::events/load-patients])))

    [:div.scroll-container
     [:div.specialist-grid
      {:style {:margin-top "7rem"}}
      (for [patient @patients]
        ^{:key (:userr/id patient)}
        [patient-card patient])]]))