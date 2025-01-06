(ns tools-methods-front.db)

(def default-db
  {;; 1-to-1 chat
   :one-to-one {:sender   ""
                :receiver "bbb@gmail.com"
                :messages []
                :user-input "dunja"
                :loading?  false
                :error     nil}

   ;; Group chat
   :group-chat {:user-email ""
                :messages   []
                :user-input ""
                :joined?    false
                :loading?   false
                :error      nil}

   ;; Symptom checker
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
   :treatment-result    nil

   ;;Chatbot
   :question nil
   :answer nil
   :answer-loading? false
   :answer-error nil

   ;;Login
   :logged-in? false
   :user nil
   :login-error nil})

