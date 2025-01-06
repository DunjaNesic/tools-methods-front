(ns tools-methods-front.viewss.root-component
  (:require
   [tools-methods-front.viewss.views :as views]
   [tools-methods-front.viewss.login :as login]
   [tools-methods-front.subs :as subs]
   [re-frame.core :as re-frame]))

(defn root-component []
  (let [logged-in? (re-frame/subscribe [::subs/logged-in?])]
    (fn []
      (if @logged-in?
        [views/main-panel]
        [login/login-panel]))))
