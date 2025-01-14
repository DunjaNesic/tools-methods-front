(ns tools-methods-front.events
  (:require
   [clojure.string]
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
                  (fn [existing-messages]
                    (sort-by :timestamp
                             (concat existing-messages
                                     [{:sender (:user db)
                                       :message original-msg
                                       :timestamp (js/Date.now)}]))))
       (assoc-in [:one-to-one :loading?] false)
       (assoc-in [:one-to-one :error] nil))))


(re-frame/reg-event-db
 ::send-1to1-failure
 (fn [db [_ resp]]
   (-> db
       (assoc-in [:one-to-one :loading?] false)
       (assoc-in [:one-to-one :error] (get-in resp [:response :error])))))

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
       (assoc-in [:group-chat :error] nil))))

(re-frame/reg-event-db
 :show-group-failure
 (fn [db [_ resp]]
   (-> db
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
 (fn [{:keys [db]} [_ local-symptoms]]
   (let [user-id        (:user-id db)
         user-symptoms (if (string? local-symptoms)
                         (->> (clojure.string/split local-symptoms #",")
                              (map #(-> %
                                        clojure.string/trim
                                        (clojure.string/replace #"\s+" "_")))
                              (vec))
                         local-symptoms)
         request-data   {:method          :post
                         :uri             "http://localhost:3000/check-symptoms"
                         :params          {:symptoms user-symptoms
                                           :user-id user-id}
                         :format          (json-request-format)
                         :response-format (json-response-format {:keywords? true})
                         :on-success      [::check-symptoms-success]
                         :on-failure      [::check-symptoms-failure]}]
     {:db (-> db
              (assoc :checker-error nil)
              (assoc :checker-result nil))
      :http-xhrio request-data})))

(re-frame/reg-event-db
 ::check-symptoms-success
 (fn [db [_ response]]
   (-> db
       (assoc :checker-error nil)
       (assoc :checker-result response))))

(re-frame/reg-event-db
 ::check-symptoms-failure
 (fn [db [_ error]]
   (assoc db :checker-error (get-in error [:response :message]))))

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
       (assoc :treatment-error nil)
       (assoc :treatment-result response))))

(re-frame/reg-event-db
 ::recommend-treatment-failure
 (fn [db [_ error]]
   (assoc db :treatment-error (get-in error [:response :message]))))

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
       (assoc :answer response)
       (assoc :answer-error nil))))

(re-frame/reg-event-db
 ::chatbot-answer-failure
 (fn [db [_ error-response]]
   (-> db
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
              :user-id (:userr/id (:user response))
              :role (:userr/user_type (:user response))))))

(re-frame/reg-event-db
 ::login-failure
 (fn [db [_ error]]
   (assoc db :login-error (get-in error [:response :message]))))

(re-frame/reg-event-fx
 ::logout
 (fn [{:keys [db]} [_ email]]
   {:http-xhrio {:method          :post
                 :uri             "http://localhost:3000/logout"
                 :params          {:email email}
                 :format          (json-request-format)
                 :response-format (json-response-format {:keywords? true})
                 :on-success      [::logout-success]
                 :on-failure      [::logout-failure]}
    :db (assoc db :user nil
               :role ""
               :name ""
               :specialty "")}))

(re-frame/reg-event-db
 ::logout-success
 (fn [db [_ response]]
   (assoc db :logged-in? false)))

(re-frame/reg-event-db
 ::logout-failure
 (fn [db [_ error]]
   (println "Logout failure error:" error)
   db))

(re-frame/reg-event-fx
 ::register
 (fn [{:keys [db]} [_ name new-email new-pass role specialty]]
   {:http-xhrio {:method          :post
                 :uri             "http://localhost:3000/register"
                 :params          {:name name
                                   :email new-email
                                   :password new-pass
                                   :user_type role
                                   :specialty specialty}
                 :format          (json-request-format)
                 :response-format (json-response-format {:keywords? true})
                 :on-success      [::registration-success]
                 :on-failure      [::registration-failure]}
    :db (assoc db :login-error nil)}))


(re-frame/reg-event-db
 ::registration-success
 (fn [db [_ response]]
   (assoc db :registration-succ (:message response))))

(re-frame/reg-event-db
 ::registration-failure
 (fn [db [_ error]]
   (assoc db :registration-error (get-in error [:response :message]))))

(re-frame/reg-event-db
 ::clear-registration-succ
 (fn [db _]
   (assoc db :registration-succ nil)))

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
       (assoc :cost-error nil))))

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

(re-frame/reg-event-fx
 ::load-patients
 (fn [_ _]
   {:http-xhrio {:method          :get
                 :uri             "http://localhost:3000/patients"
                 :response-format (json-response-format {:keywords? true})
                 :on-success      [::patients-loaded]
                 :on-failure      [::patients-failed-load]}}))

(re-frame/reg-event-db
 ::patients-loaded
 (fn [db [_ response]]
   (assoc db :patients (:patients response))))

(re-frame/reg-event-db
 ::patients-failed-load
 (fn [db [_ error]]
   (assoc db :specialists-error error)))

(re-frame/reg-event-fx
 ::load-specialists
 (fn [_ _]
   {:http-xhrio {:method          :get
                 :uri             "http://localhost:3000/specialists"
                 :response-format (json-response-format {:keywords? true})
                 :on-success      [::specialists-loaded]
                 :on-failure      [::specialists-failed-load]}}))

(re-frame/reg-event-db
 ::specialists-loaded
 (fn [db [_ response]]
   (assoc db :specialists (:specialists response))))

(re-frame/reg-event-db
 ::specialists-failed-load
 (fn [db [_ error]]
   (assoc db :specialists-error error)))

(re-frame/reg-event-fx
 ::select-specialty
 (fn [_ [_ selected-specialty]]
   {:http-xhrio {:method          :get
                 :uri             (str "http://localhost:3000/specialist?specialty=" selected-specialty)
                 :response-format (json-response-format {:keywords? true})
                 :on-success      [::specialists-loaded]
                 :on-failure      [::specialists-failed-load]}}))

(re-frame/reg-event-fx
 ::get-history
 (fn [{:keys [db]} [_]]
   (let [user-id  (:user-id db)]
     {:http-xhrio {:method          :post
                   :params          {:user-id user-id}
                   :uri             "http://localhost:3000/history"
                   :format          (json-request-format)
                   :response-format (json-response-format {:keywords? true})
                   :on-success      [::history-success]
                   :on-failure      [::history-failed]}})))

(re-frame/reg-event-db
 ::history-success
 (fn [db [_ response]]
   (assoc db :symptom-history (:history response))))

(re-frame/reg-event-db
 ::history-failed
 (fn [db [_ error]]
   (js/console.log "Error history response:" error)))

(re-frame/reg-event-fx
 ::fetch-1to1-messages
 (fn [{:keys [db]} [_ receiver last-checked-timestamp]]
   (js/console.log "Request Params:" {:action "fetch-new-messages"
                                      :receiver receiver
                                      :last-checked-timestamp (str last-checked-timestamp)})
   {:db (assoc db :one-to-one-loading? true :one-to-one-error nil)
    :http-xhrio {:method          :post
                 :uri             "http://localhost:3000/chat"
                 :params          {:action "fetch-new-messages"
                                   :receiver receiver
                                   :last-checked-timestamp (str last-checked-timestamp)}
                 :timeout         10000
                 :format          (json-request-format)
                 :response-format (json-response-format {:keywords? true})
                 :on-success      [::fetch-1to1-messages-success]
                 :on-failure      [::fetch-1to1-messages-failure]}}))

(re-frame/reg-event-db
 ::fetch-1to1-messages-success
 (fn [db [_ response]]
   (update db :one-to-one
           (fn [one-to-one]
             (update one-to-one :messages
                     (fn [existing-messages]
                       (sort-by :timestamp
                                (distinct (concat existing-messages (:messages response))))))))))


(re-frame/reg-event-db
 ::fetch-1to1-messages-failure
 (fn [db [_ error]]
   (-> db
       (assoc :one-to-one-loading? false
              :one-to-one-error error)))) 
