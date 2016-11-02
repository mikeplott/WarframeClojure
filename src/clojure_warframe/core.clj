(ns clojure-warframe.core
  (:require [clojure.string :as str]
            [compojure.core :as c]
            [ring.adapter.jetty :as j]
            [hiccup.core :as h])
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
                     (zipmap header line))
                items)]
    items))
                

(defn -main [& args]
  (println "Hello world!"))
