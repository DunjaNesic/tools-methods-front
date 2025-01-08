(ns tools-methods-front.viewss.symptom-checker
  (:require
   [clojure.string :as str]
   [reagent.core :as reagent]
   [tools-methods-front.subs :as subs]
   [tools-methods-front.events :as events]
   [re-frame.core :as re-frame]))

(defn symptom-checker-panel []
  (let [symptoms        @(re-frame/subscribe [::subs/symptoms])
        local-symptoms (reagent/atom "")
        error-msg       @(re-frame/subscribe [::subs/checker-error])
        checker-result  @(re-frame/subscribe [::subs/checker-result])]

    (fn []

      [:div.symptom-checker-container
       [:div.left
        (when error-msg
          [:p {:style {:color "red"}} error-msg])

        [:div.lbl
         [:label "Tell us your symptoms"]
         [:input {:type "text"
                  :placeholder "e.g. back_pain, cough..."
                  :value @local-symptoms :on-change #(reset! local-symptoms (-> % .-target .-value))}]]

        [:button.btn.green-btn {:on-click #(re-frame/dispatch [::events/check-symptoms @local-symptoms])}
         "Check Symptoms"]

        (when checker-result
          (let [{:keys [diagnoses specialists error]} checker-result]
            [:div
             [:h3 "Diagnosis Result"]
             (if error
               [:p {:style {:color "red"}} error]
               [:div
                [:p (str "Diagnoses: " (pr-str diagnoses))]
                [:p (str "Suggested Specialists: " (pr-str specialists))]])]))]
       [:div.right
        [:h2 "FOR A BETTER TOMORROW"]
        [:button.btn.green-btn "Get personalized plan"]]])))
