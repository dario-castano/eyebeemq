(ns dario-castano.connection.mqconnection
  (:gen-class)
  (:require [taoensso.timbre :as log])
  (:import [com.ibm.msg.client.jms JmsConnectionFactory] 
           [com.ibm.msg.client.jms JmsFactoryFactory]
           [com.ibm.msg.client.wmq WMQConstants]
           [javax.jms JMSException]))

(defn create-jms-connfactory []
  (try 
    (-> (JmsFactoryFactory/getInstance (WMQConstants/WMQ_PROVIDER))
        (.createConnectionFactory))
    (catch JMSException jmsex ((log/error (str (. jmsex getMessage)))))))

(defn set-manual-connection [cf config]
  (doto cf
        (.setStringProperty (WMQConstants/WMQ_CONNECTION_NAME_LIST) (config :connection-string))
        (.setStringProperty (WMQConstants/WMQ_CHANNEL) (config :channel))))

(defn set-ccdt-url [cf config]
  (.setStringProperty cf (WMQConstants/WMQ_CCDTURL) (config :ccdt-url)))

(defn set-jms-properties [cf config] 
  (try 
    (doto cf
          (if (nil? (config :ccdt-url))
           (set-manual-connection cf config)
           (set-ccdt-url cf config))
          (.setIntProperty (WMQConstants/WMQ_CONNECTION_MODE) (WMQConstants/WMQ_CM_CLIENT))
          (.setStringProperty (WMQConstants/WMQ_QUEUE_MANAGER) (config :queue-manager))
          (.setStringProperty (WMQConstants/WMQ_APPLICATIONNAME) (config :app-name))
          (.setBooleanProperty (WMQConstants/USER_AUTHENTICATION_MQCSP) true)
          (.setStringProperty (WMQConstants/USERID) (config :app-user))
          (.setStringProperty (WMQConstants/PASSWORD) (config :app-password))
          (when (or (nil? (config :cipher-suite)) (empty? (config :cipher-suite)))
            (.setStringProperty (WMQConstants/WMQ_SSL_CIPHER_SUITE) (config :cipher-suite)))
          )
    (catch JMSException jmsex ((log/error (str (. jmsex getMessage)))))))



;; HERE I'M TESTING THAT THINGS GONNA BE OK
(comment
  (def config {:connection-string "localhost(1414),localhost(1416)"
               :channel "DEV.APP.SVRCONN"
               :queue-manager "QM1"
               :app-name "TESTAPP"
               :app-user "testusr"
               :app-password "passw0rd"})
  (def cf (create-jms-connfactory)))