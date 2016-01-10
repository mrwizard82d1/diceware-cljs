(ns diceware.core-test
  (:require [cljs.test :as t]
            [diceware.core :as dc]))

(enable-console-print!)

(t/deftest view-candidate-passwords-test
           (t/is (= [:ul (repeat 5 [:li])] (dc/view-candidate-passwords)))
           )

(set! *main-cli-fn* #(t/run-tests))
