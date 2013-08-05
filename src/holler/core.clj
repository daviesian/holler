(ns holler.core
  (:use [overtone.live]
        [clojure.pprint]
        [compojure.core]
        [compojure.route]
        [hiccup.core]
        [hiccup.page]
        [ring.adapter.jetty]
        [ring.util.response :only [redirect]]))

;; Define an overtone synth

(defsynth my-synth [freq 440 amp 1]
  (let [env (env-gen (perc 0.05 0.4) :action FREE)]
    (out 0 (pan2 (* env amp (sin-osc freq))))))


;; Test the synth

(my-synth 220 1)


;; It would be nice if we could use note
;; numbers rather than frequencies.

(defn play-note [n amp]
  (my-synth (midi->hz n) amp))


;; Test out our new function

(play-note 65 1)


;; Define one octave of a pentatonic scale as intervals

(def pentatonic-octave [2 3 2 2 3])


;; Now repeat that forever

(def pentatonic-intervals (flatten (repeat pentatonic-octave)))


;; Create an infinite seq of pentatonic notes

(def pentatonic-seq (reductions + 0 pentatonic-intervals))


;; ... but let's just take the first 3 octaves

(def pentatonic-scale (take 15 pentatonic-seq))


;; A function to get a random element of a collection

(defn get-random-element [coll]
  (let [i (rand-int (count coll))]
    (nth coll i)))


;; A function to produce an infinite seq of elements
;; randomly selected from a collection

(defn infinite-random-seq [coll]
  (repeatedly (fn [] (get-random-element coll))))


;; A recursive (ish) function to play a seq of notes.

(def stop? (atom false))

(defn play-notes [notes]
  (let [[n & ns] notes]
    (play-note (+ 45 n) 0.4)
    (when-not @stop?
      (Thread/sleep 200)
      (future (play-notes ns)))))


;; Let's test it.

(play-notes (infinite-random-seq pentatonic-scale))


;; But it goes on forever! Let's stop it.

(reset! stop? true)


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Now we'll build a web-based GUI
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;


;; A function to render our main page.

(defn get-home-page []
  (html5
   [:head
    [:title "Hello"]
    (include-css "/main.css")
    (include-css "http://netdna.bootstrapcdn.com/bootstrap/3.0.0-rc1/css/bootstrap.min.css")
    (include-js "http://code.jquery.com/jquery-1.10.1.min.js")
    (include-js "/cljs/holler.js")]
   [:body
    [:div.col-lg-12
     [:h1 "Here is a header"]
     [:a.btn.btn-primary {:href "/start"} "Start Seq"] " "
     [:a.btn.btn-primary {:href "/stop"} "Stop Seq"]
     [:hr]
     [:div "Press keys to play notes, or click the buttons below."]
     [:hr]
     (for [i (range 0 15)]
       (let [n (+ 45 (nth pentatonic-scale i))]
         (html [:a.btn.btn-primary.note-link {:href "#" :data-note n} "Note " n] " ")))]]))


;; The request handler for our web app.

(defroutes handler
  (GET "/" [] (#'get-home-page))
  (POST "/play/:note" [note]
       (play-note (Integer/parseInt note) 1)
       (redirect "/"))
  (GET "/start" []
       (reset! stop? false)
       (future (play-notes (infinite-random-seq pentatonic-scale)))
       (redirect "/"))
  (GET "/stop" []
       (reset! stop? true)
       (redirect "/"))
  (files "/" {:root "resources/public"}))


;; Start a web server

(future (run-jetty #'handler {:port 5050}))


;; Now visit http://localhost:5050/ and play!
