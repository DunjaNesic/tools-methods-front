(ns tools-methods-front.viewss.symptom-checker
  (:require
   [tools-methods-front.subs :as subs]
   [tools-methods-front.events :as events]
   [re-frame.core :as re-frame]))

(defn symptom-checker-panel []
  (let [symptoms        @(re-frame/subscribe [::subs/symptoms])
        loading?        @(re-frame/subscribe [::subs/checker-loading?])
        error-msg       @(re-frame/subscribe [::subs/checker-error])
        checker-result  @(re-frame/subscribe [::subs/checker-result])
        local-symptom   @(re-frame/subscribe [::subs/local-symptom])]

    [:div.symptom-checker-container
     [:h2 "Symptom Checker"]

     (when loading?
       [:p "Checking symptoms..."])
     (when error-msg
       [:p {:style {:color "red"}} error-msg])

     [:p "Selected symptoms: " (pr-str symptoms)]

     [:div
      [:input {:type "text"
               :placeholder "Enter symptom (e.g. back_pain)"
               :on-change #(re-frame/dispatch
                            [::events/local-symptom-input (-> % .-target .-value)])}]
      [:button {:on-click #(do
                             (re-frame/dispatch [::events/add-symptom]))}
       "Add Symptom"]]

     [:div.button-wrapper
      [:button {:on-click #(re-frame/dispatch [::events/set-symptoms []])}
       "Clear Symptoms"]

      [:button {:on-click #(re-frame/dispatch [::events/check-symptoms])}
       "Check Symptoms"]]

     (when checker-result
       (let [{:keys [diagnoses specialists error]} checker-result]
         [:div
          [:h3 "Diagnosis Result"]
          (if error
            [:p {:style {:color "red"}} error]
            [:div
             [:p (str "Diagnoses: " (pr-str diagnoses))]
             [:p (str "Suggested Specialists: " (pr-str specialists))]])]))]))
