(ns tools-methods-front.viewss.personalized-treatment
  (:require
   [clojure.string]
   [tools-methods-front.subs :as subs]
   [tools-methods-front.events :as events]
   [re-frame.core :as re-frame]))

(defn personalized-treatment-panel []
  (let [medical-conditions @(re-frame/subscribe [::subs/medical-conditions])
        lifestyle          @(re-frame/subscribe [::subs/lifestyle])
        genetic-markers    @(re-frame/subscribe [::subs/genetic-markers])
        treatment-loading? @(re-frame/subscribe [::subs/treatment-loading?])
        treatment-error    @(re-frame/subscribe [::subs/treatment-error])
        treatment-result   @(re-frame/subscribe [::subs/treatment-result])]

    [:div.personalized-treatment-container
     [:div.left-col

      [:h2
       [:span "YOUR OWN"]
       [:br]
       [:span "PERSONALIZED"]
       [:br]
       [:span "HEALTH PLAN"]]


      (when treatment-loading?
        [:p "Loading recommendations..."])
      (when treatment-error
        [:div.error treatment-error])

      [:div.lbl
       [:label "What medical conditions do you have? "]
       [:input {:type "text"
                :placeholder "e.g. hypertension, diabetes"
                :on-change #(let [val (-> % .-target .-value)
                                  splitted (-> val
                                               (clojure.string/split #",")
                                               (->> (map clojure.string/trim)
                                                    (remove empty?)
                                                    (map keyword)))]
                              (re-frame/dispatch [::events/set-medical-conditions splitted]))}]]

      [:div.lbl
       [:label "Select your lifestyle"]
       [:select {:value (str (or lifestyle ""))
                 :on-change #(let [val (-> % .-target .-value)]
                               (re-frame/dispatch [::events/set-lifestyle (keyword val)]))}
        [:option {:value ""} "click me"]
        [:option {:value "sedentary"} "Sedentary"]
        [:option {:value "active"} "Active"]]]

      [:div.lbl
       [:label "Do you have any genetic markers?"]
       [:input {:type "text"
                :placeholder "e.g. APOE, BRCA1..."
                :on-change #(let [val (-> % .-target .-value)
                                  splitted (-> val
                                               (clojure.string/split #",")
                                               (->> (map clojure.string/trim)
                                                    (remove empty?)
                                                    (map keyword)))]
                              (re-frame/dispatch [::events/set-genetic-markers splitted]))}]]

      [:button.btn.green-btn {:on-click #(re-frame/dispatch [::events/recommend-treatment])}
       "Get Recommendations"]]

     [:div.right-col
      [:div.upper-row
       [:p "Want to depress yourself and revisit the history of your symptoms?"]
       [:button.btn.light-btn "Click here"]]
      [:div.lower-row
       (when treatment-result
         [:div.recommendation
          [:h4 "OUR RECOMMENDATION FOR YOUR DIET: "]
          [:p (clojure.string/join ", " (:diet treatment-result))]
          [:h4 "FOR YOUR EXERCISES: "]
          [:p (clojure.string/join ", " (:exercise treatment-result))]
          [:h4 "FOR MEDICATIONS (TAKE THIS WITH A GRAIN OF SALT THO): "]
          [:p (if (empty? (:medications treatment-result))
                "Nothing"
                (clojure.string/join ", " (:medications treatment-result)))]])]]]))
