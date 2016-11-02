(set-env!
  :source-paths #{"src"}
  :dependencies '[[org.clojure/clojure "1.8.0"]
                  [nightlight "1.1.0" :scope "test"]
                  [ring "1.4.0"]
                  [hiccup "1.0.5"]
                  [compojure "1.5.1"]])

(require '[nightlight.boot :refer [nightlight]])

(deftask build []
  (comp
    (aot :namespace '#{clojure-purchases.core})
    (pom :project 'clojure-purchases
         :version "1.0.0")
    (uber)
    (jar :main 'clojure-purchases.core)
    (target)))

(deftask run []
  (comp
    (wait)
    (nightlight :port 4000)))

(require '[clojure-warframe.core :as cw])

(deftask dev []
  (with-pass-thru fileset
    (cw/-main)))

