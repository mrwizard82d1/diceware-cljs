(ns diceware.generators-test
  (:require [cljs.test :as t]
            [clojure.string :as str]
            [diceware.generators :as dg]))

(def five-words ["vetus" "mensae" "filia" "Europae" "bracteae"])

(def sequential-selector
  (let [counter (atom 0)]
    (fn [words]
      (let [word (nth words (mod @counter (count words)))]
        (swap! counter inc)
        word))))

(t/deftest generate-passphrase
           (t/is (= 0 (count (dg/generate :passphrase 0 ["a"] (fn [ws] (nth ws 0))))))
           (let [passphrases (dg/generate :passphrase 1 five-words sequential-selector)]
             (t/is (= 1 (count passphrases)))
             (let [pieces (str/split (nth passphrases 0) " ")]
               (t/is (= 5 (count pieces)))
               (t/is (= five-words pieces)))
             (t/is (= 3 (count (dg/generate :passphrase 3 five-words sequential-selector))))))

(t/deftest generate-xp-password
           (t/is (= 0 (count (dg/generate :password-xp 0 ["a"] (fn [ws] (nth ws 0))))))
           (let [passwords (dg/generate :password-xp 1 five-words sequential-selector)]
             (t/is (= 1 (count passwords)))
             (let [pieces (str/split (nth passwords 0) #"[^A-Za-z]")]
               (t/is (= 3 (count pieces)))
               (t/is (= ["vetus" "mensae" "fil"] pieces)))
             (t/is (= 3 (count (dg/generate :password-xp 3 five-words sequential-selector))))))


