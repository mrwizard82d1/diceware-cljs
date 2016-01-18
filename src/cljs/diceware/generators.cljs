(ns diceware.generators
  (:require [clojure.string :as str]
            [goog.string :as gs]))

(defmulti generate (fn [kind _ _ _] kind))

(defn random-words [count selector words]
  (repeatedly count (partial selector words)))

(defmethod generate :passphrase
  [_ count words selector]
  (repeatedly count
              (fn []
                (str/join " " (random-words 5 selector words)))))

(def non-alpha-characters
  (vec (remove #(or (gs/isAlpha %) (gs/isEmptyOrWhitespace %))
               (map js/String.fromCharCode (range 32 127)))))

(defn random-phrase [count selector words separators-f]
  (str/join "" (interleave (random-words count selector words)
                           (separators-f))))

(defn random-non-alpha []
  "Generate a sequence of randomly selected non-alphabetic characters"
  (repeatedly (partial rand-nth non-alpha-characters)))

(defn random-digits []
  "Generate a sequence of randomly selected digits."
  (repeatedly #(rand-int 10)))

(defn generate-outlook-password [words selector]
  (let [candidate (random-phrase 4 selector words random-non-alpha)]
    (if (> (count candidate) 16)
      (.substring candidate 0 16)
      candidate)))

(defmethod generate :password-outlook
  [_ count words selector]
  (repeatedly count (partial generate-outlook-password words selector)))

(defn generate-windows-password [words selector]
  (let [candidate (random-phrase 6 selector words random-non-alpha)]
    (if (> (count candidate) 32)
      (.substring candidate 0 32)
      candidate)))

(defmethod generate :password-windows
  [_ count words selector]
  (repeatedly count (partial generate-windows-password words selector)))

(defn generate-password [words selector]
  (let [candidate (str/join "" [(random-phrase 4 selector words random-non-alpha) (selector words)])]
    candidate))

(defmethod generate :password
  [_ count words selector]
  (repeatedly count (partial generate-password words selector)))

(defn parse-pin-password [pin-password]
  (re-find #"([A-Za-z]{2})[A-Za-z]*(\d)([A-Za-z]{2})[A-Za-z]*" pin-password))

(defn is-pin-password? [candidate]
  (parse-pin-password candidate))

(defn generate-pin [words selector]
  (let [candidates (filter is-pin-password?
                           (repeatedly #(str/join "" [(selector words) (first (random-digits)) (selector words)])))]
    (first candidates)))

(defmethod generate :pin
  [_ count words selector]
  (repeatedly count (partial generate-pin words selector)))

(defn chars->pin-numbers [cs]
  (str/join "" (for [c (str/lower-case cs)]
                 (cond
                   (gs/contains "abc" c) 2
                   (gs/contains "def" c) 3
                   (gs/contains "ghi" c) 4
                   (gs/contains "jkl" c) 5
                   (gs/contains "mno" c) 6
                   (gs/contains "pqrs" c) 7
                   (gs/contains "tuv" c) 8
                   (gs/contains "wxyz" c) 9))))

(defn pin-password->numeric-pin [pin-password]
  (if-let [[_ first-word-prefix digit-separator second-word-prefix] (parse-pin-password pin-password)
           ]
    (str/join "" [(chars->pin-numbers first-word-prefix)
                  digit-separator
                  (chars->pin-numbers second-word-prefix)])))

