(ns holler
  (:use [jayq.core :only [$ ajax on]]
        [jayq.util :only [log]]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; This is a clojurescript file.
;; It gets compiled to js by lein-cljsbuild
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

;; When we click a link to a note, make
;; an AJAX request to play that note.

(defn note-link-click [e]
  (let [n (-> ($ (.-target e))
              (.data "note"))]
    (log (str "Playing " n))
    (ajax {:url (str "/play/" n)
           :method :post})))


;; When we press a key, make an AJAX
;; request to play a note corresponding
;; to the key-code.

(defn key-down [e]
  (let [n (.-keyCode e)]
    (log e)
    (ajax {:url (str "/play/" n)
           :method :post})))


;; When the document is fully loaded, attach event handlers.

($ (fn []
     (log "Hello. The document is ready")
     (on ($ "body") "mousedown" ".note-link" nil note-link-click)
     (on ($ "body") "keydown" nil nil key-down)))
