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
   (println "Updating sender to: " new-sender)
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
   (let [sender   (get-in db [:one-to-one :sender])
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
   (let [sender   (get-in db [:one-to-one :sender])
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
                  conj {:sender (get-in db [:one-to-one :sender])
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
   (let [sender   (get-in db [:one-to-one :sender])
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
   (let [user-email (get-in db [:group-chat :user-email])]
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
   (let [user-email (get-in db [:group-chat :user-email])
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
              conj {:sender (get-in db [:group-chat :user-email])
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
   (let [user-email (get-in db [:group-chat :user-email])]
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

