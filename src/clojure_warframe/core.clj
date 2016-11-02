(ns clojure-warframe.core
  (:require [clojure.string :as str]
            [compojure.core :as c]
            [ring.adapter.jetty :as j]
            [hiccup.core :as h]
            ;[clojure.java.jdbc :as sql]
            [jdbc.core :as jdbc])
  (:gen-class))

(def file-name "items.txt")

(defn read-items []
  (let [items (str/split-lines (slurp file-name))
        items (map (fn [item] 
                     (str/split item #"|"))
                items)
        header (first items)
        items (rest items)
        items (map (fn [item]
                     (zipmap header item))
                items)]
    items))

(def db
  {:classname "org.h2.Driver"
   :subprotocol "h2:file"
   :subname "db/warframe"})

(with-open [conn (jdbc/connection db)]
  (jdbc/execute conn "CREATE TABLE IF NOT EXISTS item_list (id IDENTITY, item_name VARCHAR, category VARCHAR, void_relic VARCHAR);")
  (jdbc/execute conn "CREATE TABLE IF NOT EXISTS users (id IDENTITY, name VARCHAR, password VARCHAR);")
  (jdbc/execute conn "CREATE TABLE IF NOT EXISTS user_list (id IDENTITY, item_name VARCHAR, category VARCHAR, void_relic VARCHAR, quantity INT, user_id INT);"))
  

;(sql/with-connection db 
 ; (sql/create-table :items
  ;  [:id "bigint primary key auto_increment"]
   ; [:item_name "varchar"]
    ;[:category "varchar"]
    ;[:void_relic "varchar"]})
(with-open [conn (jdbc/connection db)]
  (let [items (read-items)
        items {:item_name (get items "item_name") 
               :category (get items "category")
               :void_relic (get items "void_relic")}]
    (jdbc/execute conn "INSERT INTO item_list VALUES(NULL, ?, ?, ?)" :item_name, :category, :void_relic)))
    

                

(defn -main [& args]
 (j/run-jetty app {:port 3000}))
