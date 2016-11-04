(set-env!
  :source-paths #{"src"}
  :dependencies '[[org.clojure/clojure "1.8.0"]
                  [nightlight "1.1.0" :scope "test"]
                  [ring "1.4.0"]
                  [hiccup "1.0.5"]
                  [compojure "1.5.1"]
                  [com.h2database/h2 "1.3.170"]
;                 [funcool/clojure.jdbc "0.9.0"]
                  [org.clojure/java.jdbc "0.6.2-alpha3"]
                  [org.postgresql/postgresql "9.4.1207"]])

(require '[nightlight.boot :refer [nightlight]])

(deftask build []
  (comp
    (aot :namespace '#{clojure-warframe.core})
    (pom :project 'clojure-warframe
         :version "1.0.0")
    (uber)
    (jar :main 'clojure-warframe.core)
    (target)))

(deftask run []
  (comp
    (wait)
    (nightlight :port 4000)))

(require '[clojure-warframe.core :as cw])

(deftask dev []
  (with-pass-thru fileset
    (cw/-main)))

