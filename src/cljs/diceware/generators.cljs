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

(defn generate-outlook-password [words selector]
  (let [candidate (str/join "" (interleave (repeatedly 4 (partial selector words))
                                           (repeatedly (partial rand-nth non-alpha-characters))))]
    (if (> (count candidate) 16)
      (.substring candidate 0 16)
      candidate)))

(defmethod generate :password-outlook
  [_ count words selector]
  (repeatedly count (partial generate-outlook-password words selector)))

(defn generate-windows-password [words selector]
  (let [candidate (str/join "" (interleave (repeatedly 6 (partial selector words))
                                           (repeatedly (partial rand-nth non-alpha-characters))))]
    (if (> (count candidate) 32)
      (.substring candidate 0 32)
      candidate)))

(defmethod generate :password-windows
  [_ count words selector]
  (repeatedly count (partial generate-windows-password words selector)))

(defn generate-password [words selector]
  (let [candidate (str/join "" (concat (interleave (repeatedly 4 (partial selector words))
                                                   (repeatedly (partial rand-nth non-alpha-characters)))
                                       (selector words)))]
    candidate))

(defmethod generate :password
  [_ count words selector]
  (repeatedly count (partial generate-password words selector)))

(defn generate-pin [words selector]
  (let [candidate (str/join "" [(selector words) (rand-int 10) (selector words)])]
    candidate))

(defmethod generate :pin
  [_ count words selector]
  (repeatedly count (partial generate-pin words selector)))

