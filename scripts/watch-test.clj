(require '[cljs.build.api :as b])

(b/watch (b/inputs "src/cljs" "test/cljs")
  {:main 'diceware.core-test
   :target :nodejs
   :output-to "out/diceware_test.js"
   :output-dir "out"
   :verbose true})
