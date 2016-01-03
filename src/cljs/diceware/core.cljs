(ns diceware.core
  (:require [reagent.core :as reagent]
            [cljsjs.react]))

(def app-state
  (reagent/atom
    {:options {:count 5}
     :results {:candidates []}}))

(enable-console-print!)

(defn get-how-many []
  (get-in @app-state [:options :count]))

(defn view-generators [])

(defn view-how-many []
  [:div {:class-name "how-many vertical-isolation"}
    [:label {:for "count"} "How Many?"]
    [:input {:type      "number" :name "count" :id "count" :value (get-how-many) :min 1 :max 17
             :on-change (fn [e] (swap! app-state update-in [:options :count] (fn [_] (-> e .-target .-value)))
                          #_(do
                              (println e)
                              (println (-> e .-target))
                              (println (-> e .-target .-value))))}]])

(defn options []
  [:section {:id "options" :class-name "col-4"}
   [:h2 "Options"]
   [:form
    (view-generators)
    (view-how-many)
    [:input {:type "submit" :class-name "span-width vertical-isolation" :value "Go!" :id "go"}]]])

(defn view-candidate-passwords []
  [:ul
   (let [candidate-passwords (get-in @app-state [:results :candidates])]
     (if (not (empty? candidate-passwords))
       (map #([:li %]) candidate-passwords)
       (map-indexed (fn [i _] ^{:key i} [:li]) (range (get-how-many)))))])

(defn results []
  [:section {:id "results" :class-name "col-8"}
   [:section {:class-name "generated"}
    [:h2 "Generated"]
    (view-candidate-passwords)]
   [:section {:class-name "practice"}
    [:h2 "Practice"]
    [:textarea {:rows (get-how-many)
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
