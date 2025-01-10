(ns tools-methods-front.viewss.chatbot
  (:require [re-frame.core :as re-frame]
            [tools-methods-front.subs :as subs]
            [tools-methods-front.events :as events]
            [reagent.core :as reagent]))

(defn chatbot-panel []
  (let [answer (re-frame/subscribe [::subs/answer])
        answer-error (re-frame/subscribe [::subs/answer-error])
        user-input (reagent/atom "")
        ;; question (re-frame/subscribe [::subs/question])
        lil-robot "/images/chatbot.png"]

    (fn []
      ;; (when @answer
      ;;   (js/console.log "Answer received:" @answer))
      [:div.chatbot-container
       [:img.robot {:src lil-robot
                    :alt "Healthcare Chatbot"}]

       [:div.lbl
        [:input {:type        "text"
                 :id          "question"
                 :value       @user-input
                 :placeholder "Ask your question here..."
                 :on-change   #(reset! user-input (-> % .-target .-value))}]
        [:button.btn.light-btn {:on-click #(do
                                             (re-frame/dispatch [::events/set-question @user-input])
                                             (re-frame/dispatch [::events/get-answer]))}
         "Submit"]]

       (when @answer-error
         [:div.error-message
          [:p {:style {:color "red"}} @answer-error]])

       (when @answer
         [:div.answer
          [:p (:answer @answer)]])])))
