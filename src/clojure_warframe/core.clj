(ns clojure-warframe.core

  (:require [clojure.string :as str]
            [compojure.core :as c]
            [compojure.handler :as handler]
            [ring.adapter.jetty :as j]
            [ring.util.request :refer [body-string]]
            [hiccup.core :as h]
            [clojure.java.jdbc :as sql]
            [compojure.route :as route]
            [ring.util.response :refer [resource-response response]]
            [ring.middleware.resource :as r]
            [ring.middleware.file :as f])
  (:gen-class))

(def file-name "items.txt")

(defn read-items []
  (let [items (str/split-lines (slurp file-name))
        items (map (fn [line]
                     (str/split line #"\|"))
                items)
        header (first items)
        items (rest items)
        items (map (fn [line]
                     (zipmap header line))
                items)]
    items))

(def db-spec
  {:classname "org.h2.Driver"
   :subprotocol "h2:file"
   :subname "db/warframe"})

(def the-db
  {:classname "org.postgresql.Driver"
   :subprotocol "postgresql"
   :subname "//localhost:5432/warframe"
   :user "michaelplott"
   :password "dubose"})

(defn create-ext-tables []
   (sql/db-do-commands the-db
      (sql/create-table-ddl
        :item_list [[:id "SERIAL"]
                    [:item_name "TEXT"]
                    [:category "TEXT"]
                    [:void_relic "TEXT"]])))

(defn insert-ext []
    (let [items (read-items)]
      (map (fn [item]
             (sql/insert! the-db
               :item_list
               {:item_name (get item "item_name")
                :category (get item "category")
                :void_relic (get item "void_relic")}))
        items)))

(defn create-tables []
   (sql/db-do-commands db-spec
      (sql/create-table-ddl
        :item_list [[:id "IDENTITY"]
                    [:item_name "VARCHAR"]
                    [:category "VARCHAR"]
                    [:void_relic "VARCHAR"]]))
   (let [items (read-items)]
        (map (fn [item]
               (sql/insert! db-spec
                 :item_list
                 {:item_name (get item "item_name")
                  :category (get item "category")
                  :void_relic (get item "void_relic")}))
          items)))

(defn test-import []
  (let [test-data
        (sql/query db-spec ["SELECT * FROM item_list"])]
    (println test-data)
    test-data))

;;(defn handler [request]
  ;;(case (:uri request)
    ;;"/" (response/redirect "/index.html")))

(c/defroutes app
  (c/GET "/" [] (resource-response "index.html" {:root "resources/public"}))
  (route/resources "/")
  (route/not-found "Page not found"))

;;(j/run-jetty (wrap-resource app "public") {:port 3000})
(defn -main [& args]
  (j/run-jetty (r/wrap-resource app "public") {:port 3000}))








