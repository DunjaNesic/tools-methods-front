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
 ::checker-loading?
 (fn [db _]
   (:checker-loading? db)))

(re-frame/reg-sub
 ::checker-error
 (fn [db _]
   (:checker-error db)))

(re-frame/reg-sub
 ::checker-result
 (fn [db _]
   (:checker-result db)))

(re-frame/reg-sub
 ::local-symptom
 (fn [db _]
   (:local-symptom db)))

