(ns tools-methods-front.viewss.symptom-checker
  (:require
   [clojure.string :as str]
   [reagent.core :as reagent]
   [tools-methods-front.subs :as subs]
   [tools-methods-front.events :as events]
   [re-frame.core :as re-frame]))

(defn symptom-checker-panel []
  (let [local-symptoms (reagent/atom "")
        error-msg       (re-frame/subscribe [::subs/checker-error])
        ;; symptoms        @(re-frame/subscribe [::subs/symptoms])
        checker-result  (re-frame/subscribe [::subs/checker-result])]

    (fn []
      [:div.symptom-checker-container
       [:div.left
        [:div.wrapper
         (when @error-msg
           [:div.error @error-msg])

         [:div.lbl
          [:label "Tell us your symptoms"]
          [:input {:type "text"
                   :placeholder "e.g. back pain, cough, mild fever..."
                   :value @local-symptoms :on-change #(reset! local-symptoms (-> % .-target .-value))}]]]

        (when @checker-result
          (let [{:keys [diagnoses specialists]} @checker-result
                formatted-diagnoses (->> diagnoses
                                         (keys)
                                         (map name)
                                         (str/join ", "))
                formatted-specialists (->> specialists
                                           (map str/capitalize)
                                           (str/join ", "))]
            [:div.diagnoses-res
             [:p (str "You have been diagnosed with " formatted-diagnoses
                      ", and it is recommended that you visit " formatted-specialists ".")]]))

        [:button.btn.green-btn {:on-click #(re-frame/dispatch [::events/check-symptoms @local-symptoms])}
         "Check Symptoms"]]

       [:div.right
        [:h2 "FOR A BETTER TOMORROW"]
        [:button.btn.green-btn "Get personalized plan"]]])))
