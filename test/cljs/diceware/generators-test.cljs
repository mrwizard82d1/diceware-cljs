(ns diceware.generators-test
  (:require [cljs.test :as t]
            [clojure.string :as str]
            [diceware.generators :as dg]))

(t/deftest generate-passphrase
           (t/is (= 0 (count (dg/generate :passphrase 0 ["a"] (fn [ws] (nth ws 0))))))
           (let [single-passphrases (dg/generate :passphrase 1
                                                ["vetus" "mensae" "filia" "Europae" "bracteae"]
                                                (let [counter (atom 0)]
                                                  (fn [words]
                                                    (let [n @counter
                                                          word (nth words (mod n (count words)))]
                                                      (swap! counter inc)
                                                      word))))]
             (t/is (= 1 (count single-passphrases)))
             (let [pieces (str/split (nth single-passphrases 0) " ")]
               (t/is (= 5 (count pieces)))
               (t/is (= ["vetus" "mensae" "filia" "Europae" "bracteae"] pieces)))))
