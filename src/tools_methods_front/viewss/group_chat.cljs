(ns tools-methods-front.viewss.group-chat
  (:require
   [re-frame.core :as re-frame]))

(defn group-chat-panel []
  ;;bukv ne kapiram razliku izmedju :: i :, oba rade
  (let [user-email  @(re-frame/subscribe [:group-user-email])
        messages    @(re-frame/subscribe [:group-messages])
        joined?     @(re-frame/subscribe [:group-joined?])
        loading?    @(re-frame/subscribe [:group-loading?])
        error       @(re-frame/subscribe [:group-error])
        user-input  @(re-frame/subscribe [:group-user-input])]

    [:div.group-chat-panel
     [:h3 "Group Chat"]
     (when loading? [:p "Loading..."])
     (when error [:p {:style {:color "red"}} (str "Error: " error)])

     [:div
      [:label "Your Email: "]
      [:input {:type      "text"
               :value     user-email
               :on-change #(re-frame/dispatch
                            [:update-group-user-email (-> % .-target .-value)])}]]

     (if (not joined?)
       [:button {:on-click #(re-frame/dispatch [:join-group])}
        "Join Group"]
       [:div
        [:button {:on-click #(re-frame/dispatch [:leave-group])}
         "Leave Group"]
        [:button {:on-click #(re-frame/dispatch [:show-group])}
         "Show Group Messages"]])

     [:ul
      (for [m messages]
        ^{:key (str (:sender m) "-" (:message m))}
        [:li (str (:sender m) ": " (:message m))])]

     (when joined?
       [:div
        [:label "Message: "]
        [:input {:type      "text"
                 :value     user-input
                 :on-change #(re-frame/dispatch
                              [:update-group-user-input (-> % .-target .-value)])}]
        [:button {:on-click #(re-frame/dispatch [:send-group-message])}
         "Send to Group"]])]))
