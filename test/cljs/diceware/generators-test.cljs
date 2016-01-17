(ns diceware.generators-test
  (:require [cljs.test :as t]
            [clojure.string :as str]
            [diceware.generators :as dg]))

;; This module generates passwords. In particular, the algorithm for Outlook and Windows passwords are based on the
;; maximum lengths described in the article on historical password lengths:
;;
;; http://security.stackexchange.com/questions/22721/password-length-limits-in-history-of-operating-systems-and-popular-web-sites
;;

(def five-words ["vetus" "mensae" "filia" "Europae" "bracteae"])

(defn make-sequential-selector []
  (let [counter (atom 0)]
    (fn [words]
      (let [word (nth words (mod @counter (count words)))]
        (swap! counter inc)
        word))))

(t/deftest generate-passphrase
           (t/is (= 0 (count (dg/generate :passphrase 0 ["a"] (fn [ws] (nth ws 0))))))
           (let [sequential-selector (make-sequential-selector)
                 passphrases (dg/generate :passphrase 1 five-words sequential-selector)]
             (t/is (= 1 (count passphrases)))
             (let [pieces (str/split (nth passphrases 0) " ")]
               (t/is (= 5 (count pieces)))
               (t/is (= five-words pieces)))
             (t/is (= 3 (count (dg/generate :passphrase 3 five-words sequential-selector))))))

(t/deftest generate-outlook-password
           (t/is (= 0 (count (dg/generate :password-outlook 0 ["a"] (fn [ws] (nth ws 0))))))
           (let [sequential-selector (make-sequential-selector)
                 passwords (dg/generate :password-outlook 1 five-words sequential-selector)]
             (t/is (= 1 (count passwords)))
             (t/is (not (re-find #"[^A-Za-z]$" (nth passwords 0))))
             (let [pieces (str/split (nth passwords 0) #"[^A-Za-z]")]
               (t/is (= 3 (count pieces)))
               (t/is (= ["vetus" "mensae" "fil"] pieces)))
             (t/is (= 3 (count (dg/generate :password-outlook 3 five-words sequential-selector))))))

(t/deftest generate-windows-password
           (t/is (= 0 (count (dg/generate :password-windows 0 ["a"] (fn [ws] (nth ws 0))))))
           (let [sequential-selector (make-sequential-selector)
                 passwords (dg/generate :password-windows 1 five-words sequential-selector)]
             (t/is (= 1 (count passwords)))
             (t/is (not (re-find #"[^A-Za-z]$" (nth passwords 0))))
             (let [pieces (str/split (nth passwords 0) #"[^A-Za-z]")]
               (t/is (= 5 (count pieces)))
               ;; Because the maximum size of a windows password is 32 characters, I truncate the last piece.
               (t/is (= ["vetus" "mensae" "filia" "Europae" "bract"] pieces)))
             (t/is (= 3 (count (dg/generate :password-windows 3 five-words sequential-selector))))))

(t/deftest generate-password
           (t/is (= 0 (count (dg/generate :password 0 ["a"] (fn [ws] (nth ws 0))))))
           (let [sequential-selector (make-sequential-selector)
                 passwords (dg/generate :password 1 five-words sequential-selector)]
             (t/is (= 1 (count passwords)))
             (t/is (not (re-find #"[^A-Za-z]$" (nth passwords 0))))
             (let [pieces (str/split (nth passwords 0) #"[^A-Za-z]")]
               (t/is (= 5 (count pieces)))
               ;; The expected order is "unexpected" because of an implementation detail of `dg/generate :password`.
               (t/is (= ["mensae" "filia" "Europae" "bracteae" "vetus"] pieces)))
             (t/is (= 3 (count (dg/generate :password 3 five-words sequential-selector))))))

(t/deftest generate-pin
           (t/is (= 0 (count (dg/generate :pin 0 ["a"] (fn [ws] (nth ws 0))))))
           (let [sequential-selector (make-sequential-selector)
                 pins (dg/generate :pin 1 five-words sequential-selector)]
             (t/is (= 1 (count pins)))
             (let [matches (vec (re-seq #"^([A-Za-z]+)(\d)([A-Za-z]+)$" (nth pins 0)))]
               (t/is (= 1 (count matches)))
               (t/is (= "vetus" (get-in matches [0 1])))    ;; first group
               (t/is (= "mensae" (get-in matches [0 3]))))  ;; third group
             (t/is (= 3 (count (dg/generate :pin 3 five-words sequential-selector))))))

(t/deftest pin-password->pin-numeric
           (t/is (nil? (dg/pin-password->numeric-pin "")))
           (t/are [x y] (= x (dg/pin-password->numeric-pin y))
                  "78373" "strentes3sextus"
                  "54168" "lIquo1nugatorius"
                  "22423" "capsi4aFflatus")
           (t/are [c] (nil? (dg/pin-password->numeric-pin c))
                  "1Afer"
                  "a5agenda"
                  "civitas9c"
                  "solidus[quoque"))

(t/deftest is-pin-password?
           (t/is (not (dg/is-pin-password? "")))
           (t/are [c] (dg/is-pin-password? c)
                  "assideo2orior"
                  "Fulgeo7chordus"
                  "utilis1hErnia")
           (t/are [c] (not (dg/is-pin-password? c))
                  "8merx"
                  "n1nostri"
                  "molis4m"
                  "silentis>implacabilis"))
