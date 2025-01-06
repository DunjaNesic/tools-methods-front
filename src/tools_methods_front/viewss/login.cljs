(ns tools-methods-front.viewss.login
  (:require
   [reagent.core :as reagent]
   [tools-methods-front.events :as events]
   [tools-methods-front.subs :as subs]
   [re-frame.core :as re-frame]))

(defn login-panel []
  (let [email (reagent/atom "")
        password (reagent/atom "")
        login-error (re-frame/subscribe [::subs/login-error])]
    (fn []
      [:div.login-panel
       [:h2 "Login"]
       [:div
        [:label "Email: "]
        [:input {:type "email" :value @email :on-change #(reset! email (-> % .-target .-value))}]]
       [:div
        [:label "Password: "]
        [:input {:type "password" :value @password :on-change #(reset! password (-> % .-target .-value))}]]
       (when @login-error
         [:div.error @login-error])
       [:button
        {:on-click #(re-frame/dispatch [::events/login @email @password])}
        "Login"]])))