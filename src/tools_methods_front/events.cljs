(ns tools-methods-front.events
  (:require
   [re-frame.core :as re-frame]
   [tools-methods-front.db :as db]
   ))

(re-frame/reg-event-db
 ::initialize-db
 (fn [_ _]
   db/default-db))
