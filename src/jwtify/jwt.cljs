(ns jwtify.jwt
  (:require [cljs.core.async :refer [chan put!]]))

(def jwt (js/require "jsonwebtoken"))

(defn sign
  [{:keys [payload secret]}]
  (let [ch   (chan)
        cb   (fn [err token]
               (if token
                 (put! ch {:status 200
                           :body {:token token}})
                 (put! ch {:status 422
                           :body (js->clj err)})))
        opts #js {}]
    (.sign jwt (clj->js payload) secret opts cb)
    ch))

(defn unsign
  [{:keys [token secret]}]
  (let [ch   (chan)
        cb   (fn [err payload]
               (if payload
                 (put! ch {:status 200
                           :body (js->clj payload)})
                 (put! ch {:status 422
                           :body (js->clj err)})))
        opts #js {}]
    (.verify jwt token secret opts cb)
    ch))
