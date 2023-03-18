(ns dario-castano.eyebeemq
  (:require [dario-castano.connection.mqconnection :as mqconnection]))
  
(defn connect [config] 
  (mqconnection/startmq config))
