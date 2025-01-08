(ns tools-methods-front.viewss.login
  (:require
   [reagent.core :as reagent]
   [tools-methods-front.events :as events]
   [tools-methods-front.subs :as subs]
   [re-frame.core :as re-frame]))

(defn login-panel []
  (let [email (reagent/atom "")
        password (reagent/atom "")
        new-email (reagent/atom "")
        new-pass (reagent/atom "")
        name (reagent/atom "")
        role (reagent/atom "pacient")
        specialty (reagent/atom nil)
        login-error (re-frame/subscribe [::subs/login-error])]
    (fn []
      [:div.welcome-wrapper
       [:div.login-panel
        [:h2 "Welcome back"]
        [:div.lbl
         [:label "Email: "]
         [:input {:type "email" :value @email :on-change #(reset! email (-> % .-target .-value))}]]
        [:div.lbl
         [:label "Password: "]
         [:input {:type "password" :value @password :on-change #(reset! password (-> % .-target .-value))}]]
        (when @login-error
          [:div.error @login-error])
        [:button.btn.green-btn
         {:on-click #(re-frame/dispatch [::events/login @email @password])}
         "Login"]]
       [:div.register-panel
        [:h2
         [:span {:style {:font-size "medium"}} "Don't have an acc? "] "Register"]
        [:div.lbl
         [:label "Name: "]
         [:input {:type "text" :value @name :on-change #(reset! name (-> % .-target .-value))}]]
        [:div.lbl
         [:label "Email: "]
         [:input {:type "email" :value @new-email :on-change #(reset! new-email (-> % .-target .-value))}]]
        [:div.lbl
         [:label "Password: "]
         [:input {:type "password" :value @new-pass :on-change #(reset! new-pass (-> % .-target .-value))}]]
        [:div.radio
         [:label "Role: "]
         [:div.roles
          [:label
           [:input {:type "radio"
                    :name "role"
                    :value "pacient"
                    :checked (= @role "pacient")
                    :on-change #(reset! role "pacient")}]
           " Pacient"]
          [:label
           [:input {:type "radio"
                    :name "role"
                    :value "specialist"
                    :checked (= @role "specialist")
                    :on-change #(reset! role "specialist")}]
           " Specialist"]]]
        [:div.lbl
         [:label "Specialty: "]
         [:input {:type "text"
                  :value @specialty
                  :on-change #(reset! specialty (-> % .-target .-value))
                  :disabled (= @role "pacient")}]]
        [:button.btn.green-btn
         {:on-click #(re-frame/dispatch [::events/register @name @new-email @new-pass @role @specialty])}
         "Register"]]])))