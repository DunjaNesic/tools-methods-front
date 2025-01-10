(ns tools-methods-front.subs
  (:require
   [re-frame.core :as re-frame]))

;; 1-to-1 chat
(re-frame/reg-sub
 ::one-to-one-sender
 (fn [db _]
  ;;  (js/console.log "Fetching sender from DB:" (get-in db [:one-to-one :sender]))
   (get-in db [:one-to-one :sender])))

(re-frame/reg-sub
 ::one-to-one-receiver
 (fn [db _]
   (get-in db [:one-to-one :receiver])))

(re-frame/reg-sub
 ::one-to-one-messages
 (fn [db _]
   (get-in db [:one-to-one :messages])))

(re-frame/reg-sub
 ::one-to-one-loading?
 (fn [db _]
   (get-in db [:one-to-one :loading?])))

(re-frame/reg-sub
 ::one-to-one-error
 (fn [db _]
   (get-in db [:one-to-one :error])))

(re-frame/reg-sub
 ::one-to-one-user-input
 (fn [db _]
   (get-in db [:one-to-one :user-input])))

;; Group chat
(re-frame/reg-sub
 :group-user-email
 (fn [db _]
   (get-in db [:group-chat :user-email])))

(re-frame/reg-sub
 :group-messages
 (fn [db _]
   (get-in db [:group-chat :messages])))

(re-frame/reg-sub
 :group-joined?
 (fn [db _]
   (get-in db [:group-chat :joined?])))

(re-frame/reg-sub
 :group-loading?
 (fn [db _]
   (get-in db [:group-chat :loading?])))

(re-frame/reg-sub
 :group-error
 (fn [db _]
   (get-in db [:group-chat :error])))

(re-frame/reg-sub
 :group-user-input
 (fn [db _]
   (get-in db [:group-chat :user-input])))

;; Symptom checker

(re-frame/reg-sub
 ::symptoms
 (fn [db _]
   (:symptoms db)))

(re-frame/reg-sub
 ::checker-error
 (fn [db _]
   (:checker-error db)))

(re-frame/reg-sub
 ::checker-result
 (fn [db _]
   (:checker-result db)))

;; Personalized treatment
(re-frame/reg-sub
 ::medical-conditions
 (fn [db _]
   (:medical-conditions db)))

(re-frame/reg-sub
 ::lifestyle
 (fn [db _]
   (:lifestyle db)))

(re-frame/reg-sub
 ::genetic-markers
 (fn [db _]
   (:genetic-markers db)))

(re-frame/reg-sub
 ::treatment-error
 (fn [db _]
   (:treatment-error db)))

(re-frame/reg-sub
 ::treatment-result
 (fn [db _]
   (:treatment-result db)))

(re-frame/reg-sub
 ::question
 (fn [db _]
   (:question db)))

(re-frame/reg-sub
 ::answer
 (fn [db _]
   (:answer db)))

(re-frame/reg-sub
 ::answer-loading?
 (fn [db _]
   (:answer-loading? db)))

(re-frame/reg-sub
 ::answer-error
 (fn [db _]
   (:answer-error db)))

(re-frame/reg-sub
 ::logged-in?
 (fn [db _]
   (:logged-in? db)))

(re-frame/reg-sub
 ::user
 (fn [db _]
   (:user db)))

(re-frame/reg-sub
 ::user-id
 (fn [db _]
   (:user-id db)))


(re-frame/reg-sub
 ::login-error
 (fn [db _]
   (:login-error db)))

(re-frame/reg-sub
 ::role
 (fn [db _]
   (:user db)))

(re-frame/reg-sub
 ::registration-error
 (fn [db _]
   (:registration-error db)))

(re-frame/reg-sub
 ::registration-succ
 (fn [db _]
   (:registration-succ db)))

(re-frame/reg-sub
 ::cost
 (fn [db _]
   (:user db)))

(re-frame/reg-sub
 ::cost-message
 (fn [db _]
   (:user db)))

(re-frame/reg-sub
 ::specialists
 (fn [db _]
   (:specialists db)))

(re-frame/reg-sub
 ::symptom-history
 (fn [db _]
   (:symptom-history db)))

