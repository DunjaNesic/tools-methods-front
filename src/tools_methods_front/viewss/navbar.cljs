(ns tools-methods-front.viewss.navbar
  (:require
   [re-frame.core :as re-frame]
   [tools-methods-front.subs :as subs]
   [tools-methods-front.events :as events]))

(defn nav []
  (let [email (re-frame/subscribe [::subs/user])]
    [:div.nav
     [:span.logo "LOGO??"]
     [:button.btn.light-btn
      {:on-click #(re-frame/dispatch [::events/logout @email])}
      "Logout"]]))

