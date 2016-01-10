(require '[cljs.build.api :as b])

(b/watch (b/inputs "src/cljs" "test/cljs")
  {:main 'diceware.all-tests
   :target :nodejs
   :output-to "out/all_tests.js"
   :output-dir "out"
   :verbose true})
