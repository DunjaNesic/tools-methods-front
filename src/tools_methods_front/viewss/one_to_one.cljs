(ns tools-methods-front.viewss.one-to-one
  (:require
   [re-frame.core :as re-frame]
   [tools-methods-front.events :as events]
   [tools-methods-front.subs :as subs]))

(defn one-to-one-chat-panel []

  (let [sender       (re-frame/subscribe [::subs/user])
        role         (re-frame/subscribe [::subs/role])
        cost-message (re-frame/subscribe [::subs/cost-message])
        receiver     (re-frame/subscribe [::subs/one-to-one-receiver])
        messages     (re-frame/subscribe [::subs/one-to-one-messages])
        error        (re-frame/subscribe [::subs/one-to-one-error])
        user-input   (re-frame/subscribe [::subs/one-to-one-user-input])]

    (js/setInterval
     #(re-frame/dispatch [::events/fetch-1to1-messages @sender (js/Date.now)])
     5000)

    [:div.one-to-one-chat-panel
     (cond
       (not= @role "specialist")
       [:div.left-of-chat
        [:div.chat-wrap1
         [:p "A consultation with a specialist doctor is charged at 30 RSD per minute."]
         [:p @cost-message]
         [:p "The cost of your consultation is 900 RSD."]]
        [:div.chat-wrap2
         [:p "Don't like the professional opinion of an expert? You have the chance to chat with completely random people who might be even worse than Google doctors"]
         [:button.btn.green-btn "Join group chat"]]]

       (= @role "specialist")
       [:div.left-of-chat
        [:button.btn.green-btn {:on-click #(re-frame/dispatch [::events/start-charging])}
         "Start Charging"]
        [:button.btn.red-btn {:on-click #(re-frame/dispatch [::events/stop-charging])}
         "Stop Charging"]])

     [:div.chat
      (when @error [:div.error @error])

      [:div
       [:h1 @receiver]
       [:ul
        (for [[idx m] (map-indexed vector @messages)]
          ^{:key (str idx "-" (:sender m))}
          [:li (str (:sender m) ": " (:message m))])]]

      [:div.lbll
       [:input {:type      "text"
                :placeholder "Message"
                :value     @user-input
                :on-change #(re-frame/dispatch
                             [::events/update-1to1-user-input (-> % .-target .-value)])}]
       [:button.btn.green-btn {:on-click #(re-frame/dispatch [::events/send-1to1-message])}
        "Send"]]]]))
