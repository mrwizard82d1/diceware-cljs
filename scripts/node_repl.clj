(require 'cljs.repl)
(require 'cljs.build.api)
(require 'cljs.repl.node)

;; Compile all scripts in the "src" directory and write the result to "out/diceware.js."
(cljs.build.api/build "src"
                      {:main 'word-source.core
                       :output-to "out/diceware.js"
                       :output-dir "out"
                       :target :nodejs
                       :verbose true})

;; Invoke the node.js REPL.
(cljs.repl/repl (cljs.repl.node/repl-env)
                :watch "src"
                :output-dir "out")
