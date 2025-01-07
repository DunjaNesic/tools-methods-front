(ns tools-methods-front.events
  (:require
   [re-frame.core :as re-frame]
   [day8.re-frame.http-fx]
   [tools-methods-front.db :as db]
   [ajax.core :refer [json-request-format json-response-format]]))

(re-frame/reg-event-db
 ::initialize-db
 (fn [_ _]
   (println "Getting stuff from db")
   db/default-db))

;;1 na 1 cet:

(re-frame/reg-event-db
 ::update-1to1-sender
 (fn [db [_ new-sender]]
   (assoc-in db [:one-to-one :sender] new-sender)))

(re-frame/reg-event-db
 ::update-1to1-receiver
 (fn [db [_ new-receiver]]
   (assoc-in db [:one-to-one :receiver] new-receiver)))

(re-frame/reg-event-db
 ::update-1to1-user-input
 (fn [db [_ val]]
   (assoc-in db [:one-to-one :user-input] val)))

(re-frame/reg-event-fx
 ::start-1to1
 (fn [{:keys [db]} [_]]
   (let [sender   (:user db)
         receiver (get-in db [:one-to-one :receiver])]
     {:db (assoc-in db [:one-to-one :loading?] true)
      :http-xhrio
      {:method          :post
       :uri             "http://localhost:3000/chat"
       :params          {:action   "start"
                         :sender   sender
                         :receiver receiver}
       :format          (json-request-format)
       :response-format (json-response-format {:keywords? true})
       :on-success      [::start-1to1-success]
       :on-failure      [::start-1to1-failure]}})))

(re-frame/reg-event-db
 ::start-1to1-success
 (fn [db [_ resp]]
   (println (:message resp))
   (-> db
       (assoc-in [:one-to-one :loading?] false))))


(re-frame/reg-event-db
 ::start-1to1-failure
 (fn [db [_ resp]]
   (-> db
       (assoc-in [:one-to-one :loading?] true)
       (assoc-in [:one-to-one :error]
                 (str "aaaaaFailed to start 1-to-1 chat: " (pr-str resp))))))

(re-frame/reg-event-fx
 ::send-1to1-message
 (fn [{:keys [db]} [_]]
   (let [sender   (:user db)
         receiver (get-in db [:one-to-one :receiver])
         msg      (get-in db [:one-to-one :user-input])]
     {:db (-> db
              (assoc-in [:one-to-one :loading?] true)
              (assoc-in [:one-to-one :user-input] ""))
      :http-xhrio
      {:method          :post
       :uri             "http://localhost:3000/chat"
       :params          {:action   "send"
                         :sender   sender
                         :receiver receiver
                         :message  msg}
       :format          (json-request-format)
       :response-format (json-response-format {:keywords? true})
       :on-success      [::send-1to1-success msg]
       :on-failure      [::send-1to1-failure]}})))

(re-frame/reg-event-db
 ::send-1to1-success
 (fn [db [_ original-msg response]]
   (-> db
       (update-in [:one-to-one :messages]
                  conj {:sender (:user db)
                        :message original-msg})
       (assoc-in [:one-to-one :loading?] false)
       (assoc-in [:one-to-one :error] nil))))

(re-frame/reg-event-db
 ::send-1to1-failure
 (fn [db [_ resp]]
   (-> db
       (assoc-in [:one-to-one :loading?] false)
       (assoc-in [:one-to-one :error]
                 (str "Failed to send message: " (pr-str resp))))))

(re-frame/reg-event-fx
 ::show-1to1-messages
 (fn [{:keys [db]} [_]]
   (let [sender   (:user db)
         receiver (get-in db [:one-to-one :receiver])]
     {:db (assoc-in db [:one-to-one :loading?] true)
      :http-xhrio
      {:method          :post
       :uri             "http://localhost:3000/chat"
       :params          {:action   "show-messages"
                         :sender   sender
                         :receiver receiver}
       :format          (json-request-format)
       :response-format (json-response-format {:keywords? true})
       :on-success      [::show-1to1-success]
       :on-failure      [::show-1to1-failure]}})))

(re-frame/reg-event-db
 ::show-1to1-success
 (fn [db [_ response]]
   (-> db
       (assoc-in [:one-to-one :loading?] false)
       (assoc-in [:one-to-one :error] nil)
       (assoc-in [:one-to-one :messages] (:messages response)))))

(re-frame/reg-event-db
 ::show-1to1-failure
 (fn [db [_ resp]]
   (-> db
       (assoc-in [:one-to-one :loading?] false)
       (assoc-in [:one-to-one :error]
                 (str "Failed to show messages: " (pr-str resp))))))

;; Grupni cet:

(re-frame/reg-event-db
 :update-group-user-email
 (fn [db [_ val]]
   (assoc-in db [:group-chat :user-email] val)))

(re-frame/reg-event-db
 :update-group-user-input
 (fn [db [_ val]]
   (assoc-in db [:group-chat :user-input] val)))

(re-frame/reg-event-fx
 :join-group
 (fn [{:keys [db]} [_]]
   (let [user-email (:user db)]
     {:db (assoc-in db [:group-chat :loading?] true)
      :http-xhrio
      {:method          :post
       :uri             "http://localhost:3000/group-chat"
       :params          {:action "join"
                         :user-email user-email}
       :format          (json-request-format)
       :response-format (json-response-format {:keywords? true})
       :on-success      [:join-group-success]
       :on-failure      [:join-group-failure]}})))

(re-frame/reg-event-db
 :join-group-success
 (fn [db [_ resp]]
   (-> db
       (assoc-in [:group-chat :joined?] true)
       (assoc-in [:group-chat :loading?] false)
       (assoc-in [:group-chat :error] nil))))

(re-frame/reg-event-db
 :join-group-failure
 (fn [db [_ resp]]
   (-> db
       (assoc-in [:group-chat :loading?] true)
       (assoc-in [:group-chat :error] (str "Failed to join group: " (pr-str resp))))))

(re-frame/reg-event-fx
 :send-group-message
 (fn [{:keys [db]} [_]]
   (let [user-email (:user db)
         msg        (get-in db [:group-chat :user-input])]
     {:db (-> db
              (assoc-in [:group-chat :loading?] false)
              (assoc-in [:group-chat :user-input] ""))
      :http-xhrio
      {:method          :post
       :uri             "http://localhost:3000/group-chat"
       :params          {:action "send"
                         :user-email user-email
                         :message    msg}
       :format          (json-request-format)
       :response-format (json-response-format {:keywords? true})
       :on-success      [:send-group-success msg]
       :on-failure      [:send-group-failure]}})))

(re-frame/reg-event-db
 :send-group-success
 (fn [db [_ original-msg response]]
   (update-in db [:group-chat :messages]
              conj {:sender (:user db)
                    :message original-msg})))

(re-frame/reg-event-db
 :send-group-failure
 (fn [db [_ resp]]
   (-> db
       (assoc-in [:group-chat :loading?] false)
       (assoc-in [:group-chat :error] (str "Failed to send group msg: " (pr-str resp))))))

(re-frame/reg-event-fx
 :show-group
 (fn [{:keys [db]} [_]]
   {:db (assoc-in db [:group-chat :loading?] true)
    :http-xhrio
    {:method          :post
     :uri             "http://localhost:3000/group-chat"
     :params          {:action "show-group"}
     :format          (json-request-format)
     :response-format (json-response-format {:keywords? true})
     :on-success      [:show-group-success]
     :on-failure      [:show-group-failure]}}))

(re-frame/reg-event-db
 :show-group-success
 (fn [db [_ response]]
   (-> db
       (assoc-in [:group-chat :messages] (:messages response))
       (assoc-in [:group-chat :loading?] false)
       (assoc-in [:group-chat :error] nil))))

(re-frame/reg-event-db
 :show-group-failure
 (fn [db [_ resp]]
   (-> db
       (assoc-in [:group-chat :loading?] false)
       (assoc-in [:group-chat :error]
                 (str "Failed to show group messages: " (pr-str resp))))))

(re-frame/reg-event-fx
 :leave-group
 (fn [{:keys [db]} [_]]
   (let [user-email (:user db)]
     {:db (assoc-in db [:group-chat :loading?] true)
      :http-xhrio
      {:method          :post
       :uri             "http://localhost:3000/group-chat"
       :params          {:action "leave"
                         :user-email user-email}
       :format          (json-request-format)
       :response-format (json-response-format {:keywords? true})
       :on-success      [:leave-group-success]
       :on-failure      [:leave-group-failure]}})))

(re-frame/reg-event-db
 :leave-group-success
 (fn [db [_ resp]]
   (-> db
       (assoc-in [:group-chat :joined?] false)
       (assoc-in [:group-chat :loading?] false)
       (assoc-in [:group-chat :messages] [])
       (assoc-in [:group-chat :error] nil))))

(re-frame/reg-event-db
 :leave-group-failure
 (fn [db [_ resp]]
   (-> db
       (assoc-in [:group-chat :loading?] false)
       (assoc-in [:group-chat :error]
                 (str "Failed to leave group: " (pr-str resp))))))

;; Symptom checking

(re-frame/reg-event-db
 ::set-symptoms
 (fn [db [_ symptom-list]]
   (assoc db :symptoms symptom-list)))

(re-frame/reg-event-fx
 ::check-symptoms
 (fn [{:keys [db]} [_]]
   (let [user-symptoms (:symptoms db)]
     {:db (-> db
              (assoc :checker-loading? true)
              (assoc :checker-error nil)
              (assoc :checker-result nil))
      :http-xhrio
      {:method          :post
       :uri             "http://localhost:3000/check-symptoms"
       :params          {:symptoms user-symptoms}
       :format          (json-request-format)
       :response-format (json-response-format {:keywords? true})
       :on-success      [::check-symptoms-success]
       :on-failure      [::check-symptoms-failure]}})))

(re-frame/reg-event-db
 ::check-symptoms-success
 (fn [db [_ response]]
   (-> db
       (assoc :checker-loading? false)
       (assoc :checker-error nil)
       (assoc :checker-result response))))

(re-frame/reg-event-db
 ::check-symptoms-failure
 (fn [db [_ error-response]]
   (-> db
       (assoc :checker-loading? false)
       (assoc :checker-error
              (str "Error checking symptoms: " (pr-str error-response))))))

(re-frame/reg-event-db
 ::local-symptom-input
 (fn [db [_ val]]
   (assoc db :local-symptom val)))

(re-frame/reg-event-db
 ::add-symptom
 (fn [db _]
   (let [symp (get db :local-symptom)]
     (-> db
         (update :symptoms conj symp)
         (assoc :local-symptom "")))))

;;Personalized treatment

(re-frame/reg-event-db
 ::set-medical-conditions
 (fn [db [_ conditions]]
   (assoc db :medical-conditions conditions)))

(re-frame/reg-event-db
 ::set-lifestyle
 (fn [db [_ lifestyle]]
   (assoc db :lifestyle lifestyle)))

(re-frame/reg-event-db
 ::set-genetic-markers
 (fn [db [_ markers]]
   (assoc db :genetic-markers markers)))

(re-frame/reg-event-fx
 ::recommend-treatment
 (fn [{:keys [db]} [_]]
   (let [medical-conds  (:medical-conditions db)
         lifestyle       (:lifestyle db)
         genetic-markers (:genetic-markers db)]
     {:db (-> db
              (assoc :treatment-loading? true)
              (assoc :treatment-error nil)
              (assoc :treatment-result nil))
      :http-xhrio
      {:method          :post
       :uri             "http://localhost:3000/recommendations"
       :params          {:medical-conditions medical-conds
                         :lifestyle          lifestyle
                         :genetic-markers    genetic-markers}
       :format          (json-request-format)
       :response-format (json-response-format {:keywords? true})
       :on-success      [::recommend-treatment-success]
       :on-failure      [::recommend-treatment-failure]}})))

(re-frame/reg-event-db
 ::recommend-treatment-success
 (fn [db [_ response]]
   (-> db
       (assoc :treatment-loading? false)
       (assoc :treatment-error nil)
       (assoc :treatment-result response))))

(re-frame/reg-event-db
 ::recommend-treatment-failure
 (fn [db [_ error-response]]
   (-> db
       (assoc :treatment-loading? false)
       (assoc :treatment-error
              (str "Error getting recommendations: " (pr-str error-response))))))


;;Healthcare chatbot

(re-frame/reg-event-db
 ::set-question
 (fn [db [_ user-input]]
   (assoc db :question user-input)))

(re-frame/reg-event-fx
 ::get-answer
 (fn [{:keys [db]} [_]]
   (let [question  (:question db)]
     {:db (-> db
              (assoc :answer-loading? true)
              (assoc :answer-error nil))
      :http-xhrio
      {:method          :post
       :uri             "http://localhost:3000/chatbot"
       :params          {:question question}
       :format          (json-request-format)
       :response-format (json-response-format {:keywords? true})
       :on-success      [::chatbot-answer-success]
       :on-failure      [::chatbot-answer-failure]}})))

(re-frame/reg-event-db
 ::chatbot-answer-success
 (fn [db [_ response]]
   (-> db
       (assoc :answer-loading? false)
       (assoc :answer response)
       (assoc :answer-error nil))))

(re-frame/reg-event-db
 ::chatbot-answer-failure
 (fn [db [_ error-response]]
   (-> db
       (assoc :answer-loading? false)
       (assoc :answer-error
              (str "Error getting an answer: " (pr-str error-response))))))

(re-frame/reg-event-fx
 ::login
 (fn [{:keys [db]} [_ email password]]
   {:http-xhrio {:method          :post
                 :uri             "http://localhost:3000/login"
                 :params          {:email email :password password}
                 :format          (json-request-format)
                 :response-format (json-response-format {:keywords? true})
                 :on-success      [::login-success]
                 :on-failure      [::login-failure]}
    :db (assoc db :login-error nil)}))

(re-frame/reg-event-db
 ::login-success
 (fn [db [_ response]]
   (-> db
       (assoc :logged-in? true
              :user (:userr/email (:user response))
              :role (:userr/user_type (:user response))))))

(re-frame/reg-event-db
 ::login-failure
 (fn [db [_ error]]
   (assoc db :login-error (:message error))))

(re-frame/reg-event-fx
 ::start-charging
 (fn [{:keys [db]} [_]]
   (let [sender   (:user db)
         receiver (get-in db [:one-to-one :receiver])]
     {:db (assoc-in db [:one-to-one :loading?] true)
      :http-xhrio
      {:method          :post
       :uri             "http://localhost:3000/chat"
       :params          {:action   "start-charging"
                         :sender   sender
                         :receiver receiver}
       :format          (json-request-format)
       :response-format (json-response-format {:keywords? true})
       :on-success      [::start-charging-success]
       :on-failure      [::start-charging-failure]}})))

(re-frame/reg-event-db
 ::start-charging-success
 (fn [db [_ response]]
   (-> db
       (assoc :cost-message (:message response)
              :cost-error nil
              :cost (:cost response)))))

(re-frame/reg-event-db
 ::start-charging-failure
 (fn [db [_ error]]
   (assoc db :cost-error (:message error))))

(re-frame/reg-event-fx
 ::stop-charging
 (fn [{:keys [db]} [_]]
   (let [sender   (:user db)
         receiver (get-in db [:one-to-one :receiver])]
     {:db (assoc-in db [:one-to-one :loading?] true)
      :http-xhrio
      {:method          :post
       :uri             "http://localhost:3000/chat"
       :params          {:action   "stop-charging"
                         :sender   sender
                         :receiver receiver}
       :format          (json-request-format)
       :response-format (json-response-format {:keywords? true})
       :on-success      [::stop-charging-success]
       :on-failure      [::stop-charging-failure]}})))

(re-frame/reg-event-db
 ::stop-charging-success
 (fn [db [_ response]]
   (-> db
       (assoc :cost-message (:message response)
              :cost-error nil
              :cost (:cost response)))))

(re-frame/reg-event-db
 ::stop-charging-failure
 (fn [db [_ error]]
   (assoc db :cost-error (:message error))))

