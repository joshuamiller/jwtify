(ns jwtify.core
  (:require [cljs-lambda.macros :refer-macros [defgateway]]
            [cljs.core.async :refer [<!]]
            [bidi.bidi :refer [match-route]]
            [jwtify.jwt :as jwt])
  (:require-macros [cljs.core.async.macros :refer [go]]))

(def routes
  ["/"
    {"sign"   {:post jwt/sign}
     "unsign" {:post jwt/unsign}}])

(defn jsonify
  [s]
  (->> (clj->js s)
       (.stringify js/JSON)))

(defn parse
  [s]
  (-> (.parse js/JSON s)
      (js->clj :keywordize-keys true)))

(defgateway jwtify
  [{:keys [path method body] :as event} ctx]
  (go
    (let [{handler :handler} (match-route routes path :request-method method)
          params             (when body (parse body))
          result             (<! (handler params))]
      (-> result
          (update :body jsonify)
          (assoc-in [:headers :content-type] "application/json")))))
