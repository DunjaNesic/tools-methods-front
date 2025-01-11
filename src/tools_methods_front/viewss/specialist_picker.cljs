(ns tools-methods-front.viewss.specialist-picker
  (:require
   [reagent.core :as reagent]
   [tools-methods-front.subs :as subs]
   [tools-methods-front.events :as events]
   [re-frame.core :as re-frame]))

(defn specialist-card [specialist]
  (let [images ["/images/female-doc.png" "/images/male-doc.png"]
        random-image (rand-nth images)]
    [:div.specialist-card
     {:on-click #(do
                   (re-frame/dispatch [::events/update-1to1-receiver (:userr/email specialist)])
                   (re-frame/dispatch [::events/start-1to1])
                   (re-frame/dispatch [::events/show-1to1-messages]))}
     [:img {:src random-image
            :alt (:userr/name specialist)
            :class "specialist-image"}]
     [:h3 (:userr/name specialist)]
     [:p (:userr/specialty specialist)]]))

(defn specs []
  (let [specialists (re-frame/subscribe [::subs/specialists])
        spec-error (re-frame/subscribe [::subs/login-error])
        selected-specialty (reagent/atom "")]

    (reagent.core/after-render
     (fn []
       (re-frame/dispatch [::events/load-specialists])))

    (fn []
      (when @spec-error
        [:div.error @spec-error])

      [:div.specialist-picker
       [:div.spec-header
        [:h1 "Pick a specialist to chat with"]

        [:select
         {:value @selected-specialty
          :on-change #(do
                        (reset! selected-specialty (-> % .-target .-value))
                        (js/console.log "Dispatching select-specialty with value:" @selected-specialty)
                        (re-frame/dispatch [::events/select-specialty @selected-specialty]))}
         [:option {:value " "} "Who do you need?"]
         [:option {:value "Infectious Disease Specialist"} "Infectious Disease Specialist"]
         [:option {:value "Pulmologist"} "Pulmologist"]
         [:option {:value "Neurologist"} "Neurologist"]
         [:option {:value "General Practitioner"} "General Practitioner"]
         [:option {:value "ENT Specialist"} "ENT Specialist"]
         [:option {:value "Cardiologist"} "Cardiologist"]
         [:option {:value "Gastroenterologist"} "Gastroenterologist"]
         [:option {:value "Psychiatrist"} "Psychiatrist"]
         [:option {:value "Hematologist"} "Hematologist"]
         [:option {:value "Endocrinologist"} "Endocrinologist"]
         [:option {:value "Surgeon"} "Surgeon"]
         [:option {:value "Allergist"} "Allergist"]
         [:option {:value "Dermatologist"} "Dermatologist"]]

        [:button.btn.light-btn {:on-click #(re-frame/dispatch [::events/load-specialists])} "Refresh"]]

       [:div.scroll-container
        [:div.specialist-grid
         (for [specialist @specialists]
           ^{:key (:userr/id specialist)}
           [specialist-card specialist])]]])))