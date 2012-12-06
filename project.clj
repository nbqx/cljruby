(defproject cljruby "0.1.0-SNAPSHOT"
  :description "using jruby script from clojure"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.4.0"]
                 [org.jruby/jruby-complete "1.7.0"]]
  :profiles {:dev {:dependencies [[speclj "2.3.4"]] :plugins [[speclj "2.3.4"]]}}
  :test-paths ["spec/"])
