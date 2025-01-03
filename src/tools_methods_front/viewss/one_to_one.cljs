(ns tools-methods-front.viewss.one-to-one
  (:require
   [re-frame.core :as re-frame]
   [tools-methods-front.events :as events]
   [tools-methods-front.subs :as subs]))

(defn one-to-one-chat-panel []
  (let [sender       (re-frame/subscribe [::subs/one-to-one-sender])
        receiver     (re-frame/subscribe [::subs/one-to-one-receiver])
        messages     (re-frame/subscribe [::subs/one-to-one-messages])
        loading?     (re-frame/subscribe [::subs/one-to-one-loading?])
        error        (re-frame/subscribe [::subs/one-to-one-error])
        user-input   (re-frame/subscribe [::subs/one-to-one-user-input])]

    ;; (js/console.log "Sender:" @sender)
    ;; (js/console.log "Receiver:" @receiver)
    ;; (js/console.log "Messages:" @messages)
    ;; (js/console.log "Loading?" @loading?)
    ;; (js/console.log "Error:" @error)
    ;; (js/console.log "User Input:" @user-input)

    [:div.one-to-one-chat-panel
     [:h3 "1-to-1 Chat"]
     (when @loading? [:p "Loading..."])
     (when @error [:p {:style {:color "red"}} (str "Error: " @error)])

     [:div
      [:label "Sender Email: "]
      [:input {:type      "text"
               :value     @sender
               :on-change #(re-frame/dispatch
                            [::events/update-1to1-sender (-> % .-target .-value)])}]]

     [:div
      [:label "Receiver Email: "]
      [:input {:type      "text"
               :value     @receiver
               :on-change #(re-frame/dispatch
                            [::events/update-1to1-receiver (-> % .-target .-value)])}]]

     [:div
      [:button {:on-click #(re-frame/dispatch [::events/start-1to1])}
       "Start Chat"]
      [:button {:on-click #(re-frame/dispatch [::events/show-1to1-messages])}
       "Show Messages"]]

     [:ul
      (for [m @messages]
        ^{:key (str (:sender m) "-" (:message m))}
        [:li (str (:sender m) ": " (:message m))])]

     [:div.message-input
      [:label "Message: "]
      [:input {:type      "text"
               :value     @user-input
               :on-change #(re-frame/dispatch
                            [::events/update-1to1-user-input (-> % .-target .-value)])}]
      [:button {:on-click #(re-frame/dispatch [::events/send-1to1-message])}
       "Send"]]]))
