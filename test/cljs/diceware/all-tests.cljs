(ns diceware.all-tests
  (:require [cljs.test :as t]
            diceware.core-test
            diceware.generators-test
            ))

(set! *main-cli-fn* #(t/run-tests 'diceware.core-test
                                  'diceware.generators-test))

