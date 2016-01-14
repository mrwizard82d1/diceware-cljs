(ns diceware.generators
  (:require [clojure.string :as str]))

(defmulti generate (fn [kind _ _ _] kind))

(defmethod generate :passphrase
  [_ count words selector]
  (repeatedly count
              (fn []
                (str/join " " (repeatedly 5 (partial selector words))))))

(def non-alpha-characters
  (vec (map js/String.fromCharCode (concat (range 33 64)    ;; skip space (ASCII 32)
                                           (range 91 97) (range 123 126)))))

(defn generate-xp-password [words selector]
  (let [candidate (str/join "" (interleave (repeatedly 4 (partial selector words))
                                           (repeatedly (partial rand-nth non-alpha-characters))))]
    (if (> (count candidate) 16)
      (.substring candidate 0 16)
      candidate)))

(defmethod generate :password-xp
  [_ count words selector]
  (repeatedly count (partial generate-xp-password words selector)))

