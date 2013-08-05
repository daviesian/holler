(defproject holler "0.1.0-SNAPSHOT"
  :description "An introduction to clojure and overtone"
  :url "http://overtone.github.io/"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :plugins [[lein-cljsbuild "0.3.2"]]
  :dependencies [[org.clojure/clojure "1.5.1"]
				 [overtone "0.8.1" :exclusions [org.clojure/clojure]]
				 [ring/ring-core "1.2.0"]
				 [ring/ring-jetty-adapter "1.2.0"]
				 [compojure "1.1.5"]
				 [hiccup "1.0.4"]
				 [org.clojure/data.json "0.2.2"]
				 [jayq "2.4.0"]]	
  :cljsbuild
	{:builds
	 [{:source-paths ["src-cljs"],
	   :builds nil,
	   :compiler
	   {:pretty-print true,
		:output-to "resources/public/cljs/holler.js",
		:optimizations :simple}}]})
