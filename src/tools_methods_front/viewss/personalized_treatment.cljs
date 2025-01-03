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
     [:h2 "Personalized Treatment"]

     (when treatment-loading?
       [:p "Loading recommendations..."])
     (when treatment-error
       [:p {:style {:color "red"}} treatment-error])

     [:div
      [:p "Medical Conditions: " (pr-str medical-conditions)]
      [:input {:type "text"
               :placeholder "comma-separated (e.g. hypertension,diabetes)"
               :on-change #(let [val (-> % .-target .-value)
                                 splitted (-> val
                                              (clojure.string/split #",")
                                              (->> (map clojure.string/trim)
                                                   (remove empty?)
                                                   (map keyword)))]
                             (re-frame/dispatch [::events/set-medical-conditions splitted]))}]]

     [:div
      [:p "Lifestyle: " (str lifestyle)]
      [:select {:value (str (or lifestyle ""))
                :on-change #(let [val (-> % .-target .-value)]
                              (re-frame/dispatch [::events/set-lifestyle (keyword val)]))}
       [:option {:value ""} "-- select lifestyle --"]
       [:option {:value "sedentary"} "Sedentary"]
       [:option {:value "active"} "Active"]]]

     [:div
      [:p "Genetic Markers: " (pr-str genetic-markers)]
      [:input {:type "text"
               :placeholder "comma-separated marker list"
               :on-change #(let [val (-> % .-target .-value)
                                 splitted (-> val
                                              (clojure.string/split #",")
                                              (->> (map clojure.string/trim)
                                                   (remove empty?)
                                                   (map keyword)))]
                             (re-frame/dispatch [::events/set-genetic-markers splitted]))}]]

     [:button {:on-click #(re-frame/dispatch [::events/recommend-treatment])}
      "Get Recommendations"]

     (when treatment-result
       [:div
        [:h3 "Recommended Treatment Plan"]
        [:p (str treatment-result)]])]))
