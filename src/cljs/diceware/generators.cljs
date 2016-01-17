(ns diceware.generators
  (:require [clojure.string :as str]
            [goog.string :as gs]))

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

(defn contains? [in-string candidate ]
  (not (= -1 (.indexOf in-string candidate ))))

(defn chars->pin-numbers [cs]
  (str/join "" (for [c (str/lower-case cs)]
                 (cond
                   (contains? "abc" c) 2
                   (contains? "def" c) 3
                   (contains? "ghi" c) 4
                   (contains? "jkl" c) 5
                   (contains? "mno" c) 6
                   (contains? "pqrs" c) 7
                   (contains? "tuv" c) 8
                   (contains? "wxyz" c) 9))))

(defn pin-password->numeric-pin [pin-password]
  (if-let [[_ first-word-prefix digit-separator second-word-prefix]
           (re-find #"([A-Za-z]{2})[A-Za-z]*(\d)([A-Za-z]{2})[A-Za-z]*" pin-password)]
    (str/join "" [(chars->pin-numbers first-word-prefix)
                  digit-separator
                  (chars->pin-numbers second-word-prefix)])))

(defn is-pin-password? [candidate]
  (not (empty? (re-find #"^[A-Za-z]{2}[A-Za-z]*\d[A-Za-z]{2}[A-Za-z]*" candidate))))
