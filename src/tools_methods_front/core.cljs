(ns tools-methods-front.core
  (:require
   [reagent.dom :as rdom]
   [re-frame.core :as re-frame]
   [tools-methods-front.events :as events]
   [tools-methods-front.viewss.views :as views]
   [tools-methods-front.config :as config]))

(defn dev-setup []
  (when config/debug?
    (println "dev mode")))

(defn ^:dev/after-load mount-root []
  (re-frame/clear-subscription-cache!)
  (let [root-el (.getElementById js/document "app")]
    (rdom/unmount-component-at-node root-el)
    (rdom/render [views/main-panel] root-el)))

(defn init []
  (re-frame/dispatch-sync [::events/initialize-db])
  (println "dunja je naj")
  (dev-setup)
  (mount-root))
