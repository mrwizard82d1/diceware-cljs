(ns diceware.core
  (:require [reagent.core :as reagent]
            [cljsjs.react])
  (:use [clojure.string :only [split-lines]]))

(enable-console-print!)

;; Require the node filesystem module to read the diceware word file.
(def fs (js/require "fs"))

(defn read-diceware-words-file []
  (let [text (.readFileSync fs "./app/diceware8k.txt" {:encoding "utf-8"})]
    (clojure.string/split-lines text)))

(def generators-view {:passphrase {:name "Passphrase"}
                      :password-xp {:name "Password (XP)"}
                      :password-win7 {:name "Password (Windows 7)"}
                      :password {:name "Password"}
                      :pin {:name "PIN (5-digit)"}})

(def app-state
  (reagent/atom
    ;; The "as-is" and "to-be" values begin as **copies** of each other (not shared).
    {:options {:as-is {:generator :passphrase
                       :count 5}
               :to-be {:generator :passphrase
                       :count 5}}
     :results {:candidates []}}))

(defn get-how-many [which]
  (get-in @app-state [:options which :count]))

(defn copy-options-to-be->as-is! []
 (swap! app-state update-in [:options :as-is] merge (get-in @app-state [:options :to-be])))

(defn view-generator-selector [generator-id]
  (let [generator-id-text (name generator-id)]
    [:div
     [:input {:type "radio" :name generator-id-text :id generator-id-text :value generator-id-text}]
     [:label {:for generator-id-text :class "radio-label"} (get-in generators-view [generator-id :name])]]))

(defn view-generators []
  (map #(with-meta (view-generator-selector %) {:key %}) (keys generators-view)))

(defn view-how-many []
  [:div {:class-name "how-many vertical-isolation"}
    [:label {:for "count" :class "text-label"} "How Many?"]
    [:input {:type      "number" :name "count" :id "count" :value (get-how-many :to-be) :min 1 :max 17
             :on-change (fn [e] (swap! app-state update-in [:options :to-be :count]
                                       (fn [_] (-> e .-target .-value js/parseInt))))}]])

(defn options []
  [:section {:id "options" :class-name "col-4"}
   [:h2 "Options"]
   [:form
    (view-generators)
    (view-how-many)
    [:input {:type "submit" :class-name "span-width vertical-isolation" :value "Go!" :id "go"
             :on-click
                   (fn [ev]
                     (.preventDefault ev)
                     (copy-options-to-be->as-is!)
                     (let [source-words (read-diceware-words-file)]
                       (println (nth source-words 0))
                       (swap! app-state update-in [:results :candidates]
                              (fn [_old] (vec (repeatedly (get-how-many :as-is)
                                                          (fn [] (rand-nth source-words))))))))}]]])

(defn view-candidate-passwords []
  [:ul
   (let [candidate-passwords (get-in @app-state [:results :candidates])]
     (if (not (empty? candidate-passwords))
       (map #(vector :li %) candidate-passwords)
       (map-indexed (fn [i _] ^{:key i} [:li]) (range (get-how-many :as-is)))))])

(defn results []
  [:section {:id "results" :class-name "col-8"}
   [:section {:class-name "generated"}
    [:h2 "Generated"]
    (view-candidate-passwords)]
   [:section {:class-name "practice"}
    [:h2 "Practice"]
    [:textarea {:rows (get-how-many :as-is)
                :cols 55}]]])

(defn main-page
  []
  [:header
   [:h1 "Password Generator"]
   [:section {:class-name "row"}
    (options)
    (results)
    ]])

(defn mount-root
  []
  (reagent/render [main-page] (.getElementById js/document "app")))

(defn init!
  []
  (mount-root))
