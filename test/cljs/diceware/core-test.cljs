(ns diceware.core-test
  (:require [cljs.test :as t]
            [diceware.core :as dc]))

(enable-console-print!)

(t/deftest view-candidate-passwords-test
           (t/testing "No passwords requested"
                      (do (swap! dc/app-state update-in [:options :as-is :count] (fn [_old] 0))
                          (t/is (= [:ul (range 0)] (dc/view-candidate-passwords)))))
           (t/testing "One password requested"
                      (do (swap! dc/app-state update-in [:options :as-is :count] (fn [_old] 1))
                          (t/is (= [:ul (seq [[:li]])] (dc/view-candidate-passwords)))))
           (t/testing "Many passwords requested"
                      (do (swap! dc/app-state update-in [:options :as-is :count] (fn [_old] 5))
                          (t/is (= [:ul (repeat 5 [:li])] (dc/view-candidate-passwords)))))
           )

