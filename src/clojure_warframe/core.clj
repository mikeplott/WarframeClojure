(ns clojure-warframe.core
  (:require [clojure.string :as str]
            [compojure.core :as c]
            [ring.adapter.jetty :as j]
            [hiccup.core :as h]
            [clojure.java.jdbc :as sql])
            ;[jdbc.core :as jdbc])
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
  ;(sql/db-do-commands the-db
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

(defn -main [& args])



;(defn data
  ;(sql/with-db-connection
 ;   (let [items (read-items)])
  ;  (map (fn [item]
   ;        (sql/insert! the-db
    ;         :item_list 
     ;        {:item_name (get item "item_name") 
      ;        :category (get item "category") 
       ;       :void_relic (get item "void_relic")) 
     ; items))


;               ["INSERT INTO item_list VALUES(NULL, ?, ?, ?);"] 
;               (get item "item_name"),
;               (get item "category"),
;               (get item "void_relic"))


    ;(jdbc/execute conn "CREATE TABLE IF NOT EXISTS users (id IDENTITY, name VARCHAR, password VARCHAR);")
    ;(jdbc/execute conn "CREATE TABLE IF NOT EXISTS user_list (id IDENTITY, item_name VARCHAR, category VARCHAR, void_relic VARCHAR, quantity INT, user_id INT);")))
    
;(defn item-import []
 ; (let [items (read-items)]
  ;  (with-open [conn (sql/connection db)]
   ;   sql/execute conn ["INSERT INTO item_list VALUES(NULL, ?, ?, ?)"
    ;                     (get items "item_name"), 
     ;                    (get items "category"), 
      ;                   (get items "void_relic"))
    ;items))

;(defn test-import []
 ; (with-open [conn (jdbc/connection db)]
  ;  (jdbc/execute conn ["SELECT * FROM item_list"])))
     
;(defn test-import []
 ; (with-open [conn (jdbc/connection db)]
  ;  jdbc/query db ["SELECT * FROM item_list"]))    
      
  

;(sql/with-connection db 
 ; (sql/create-table :items
  ;  [:id "bigint primary key auto_increment"]
   ; [:item_name "varchar"]
    ;[:category "varchar"]
    ;[:void_relic "varchar"]})
;(with-open [conn (jdbc/connection db)]
;  (let [items (read-items)
 ;       items {:item_name (get items "item_name") 
 ;              :category (get items "category")
 ;              :void_relic (get items "void_relic")
 ;   (jdbc/execute conn "INSERT INTO item_list VALUES(NULL, ?, ?, ?)" :item_name, :category, :void_relic)))
    

                


 ;(j/run-jetty app {:port 3000}))
