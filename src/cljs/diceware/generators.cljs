(ns diceware.generators
  (:require [clojure.string :as str]))

(defmulti generate (fn [kind _ _ _] kind))

(defmethod generate :passphrase
  [_ count words selector]
  (repeatedly count
              (fn []
                (str/join " " (repeatedly 5 (partial selector words))))))

