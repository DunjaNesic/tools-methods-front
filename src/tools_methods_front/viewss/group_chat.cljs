(ns tools-methods-front.viewss.group-chat
  (:require
   [reagent.core :as reagent]
   [re-frame.core :as re-frame]))

(defn group-chat-panel []
  ;;bukv ne kapiram razliku izmedju :: i :, oba rade
  (let [messages    @(re-frame/subscribe [:group-messages])
        joined?     @(re-frame/subscribe [:group-joined?])
        error       @(re-frame/subscribe [:group-error])
        user-input  @(re-frame/subscribe [:group-user-input])]

    (reagent.core/after-render
     (fn []
       (re-frame/dispatch [:join-group])
       (re-frame/dispatch [:show-group])))

    (js/setInterval
     #(re-frame/dispatch [:show-group (js/Date.now)])
     5000)


    [:div.group-chat-panel
     [:h2 "Group chat, most likely a toxic one"]
     (when error [:p {:style {:color "red"}} (str "Error: " error)])

     (if (not joined?)
       [:button.btn.light-btn {:on-click #(re-frame/dispatch [:join-group])}
        "Join Group"]
      ;;  [:div
        ;; [:button {:on-click #(re-frame/dispatch [:leave-group])}
        ;;  "Leave Group"]
        ;; [:button.btn.light-btn {:on-click #(re-frame/dispatch [:show-group])}
        ;;  "Load Recent Group Messages"]]
       )
     [:div.group-wrapper
      [:ul
       (for [[idx m] (map-indexed vector messages)]
         ^{:key (str idx "-" (:sender m))}
         [:li (str (:sender m) ": " (:message m))])]

      (when joined?
        [:div.lbll
         [:input {:type      "text"
                  :placeholder "Message..."
                  :value     user-input
                  :on-change #(re-frame/dispatch
                               [:update-group-user-input (-> % .-target .-value)])}]
         [:button.btn.light-btn {:on-click #(re-frame/dispatch [:send-group-message])}
          "Send"]])]
     [:p "Still not satisfied with the answers you received? Try reaching out to our healthcare chatbot, which probably doesn’t have the right answer but can try to help you anyway :)"]]))
