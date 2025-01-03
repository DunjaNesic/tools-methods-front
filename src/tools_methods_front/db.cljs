(ns tools-methods-front.db)

;; (def default-db
;;   {;; 1-to-1 chat
;;    :one-to-one {:sender   "aaa@gmail.com"
;;                 :receiver "bbb@gmail.com"
;;                 :messages [{:message "How are you?", :sender "aaa@gmail.com", :timestamp 1735910604777, :timestamp-str "2025-01-03 14:23:24"}]
;;                 :user-input "dunja"
;;                 :loading?  false
;;                 :error     nil}

;;    ;; group chat
;;    :group-chat {:user-email "aaa@gmail.com"
;;                 :messages   []
;;                 :user-input ""
;;                 :joined?    false
;;                 :loading?   false
;;                 :error      nil}})

(def default-db
  {;; Symptom checker
   :symptoms            []
   :checker-loading?    false
   :checker-error       nil
   :checker-result      nil

   ;; Personalized treatment
   :medical-conditions  []
   :lifestyle           nil
   :genetic-markers     []
   :treatment-loading?  false
   :treatment-error     nil
   :treatment-result    nil})

