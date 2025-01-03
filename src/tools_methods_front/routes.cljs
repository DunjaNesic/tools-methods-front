(ns tools-methods-front.routes)

(def routes
  [["/"
    {:name ::calculator
     :view symptoms-view/symptoms-page}]

   ["/history"
    {:name ::history
     :view chat-view/chat-page}]])