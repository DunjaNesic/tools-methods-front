(ns tools-methods-front.viewss.chatbot
  (:require [re-frame.core :as re-frame]
            [tools-methods-front.subs :as subs]
            [tools-methods-front.events :as events]
            [reagent.core :as reagent]))

(defn chatbot-panel []
  (let [question (re-frame/subscribe [::subs/question])
        answer (re-frame/subscribe [::subs/answer])
        answer-loading? (re-frame/subscribe [::subs/answer-loading?])
        answer-error (re-frame/subscribe [::subs/answer-error])
        user-input (reagent/atom "")]
    (fn []
      ;; (when @answer
      ;;   (js/console.log "Answer received:" @answer))
      [:div.chatbot-container
       [:h2 "Healthcare Chatbot"]

       [:div.input-section
        [:label {:for "question"} "Enter your question:"]
        [:input {:type        "text"
                 :id          "question"
                 :value       @user-input
                 :placeholder "Ask your question here..."
                 :on-change   #(reset! user-input (-> % .-target .-value))}]
        [:button {:on-click #(do
                               (re-frame/dispatch [::events/set-question @user-input])
                               (re-frame/dispatch [::events/get-answer]))}
         "Submit"]]

       (when @answer-loading?
         [:div.loading-bot "Loading..."])

       (when @answer-error
         [:div.error-message
          [:p {:style {:color "red"}} @answer-error]])

       (when @answer
         (js/console.log "Answer received:" @answer)
         [:div.answer
          [:h3 "Chatbot Response:"]
          [:p (:answer @answer)]])])))
