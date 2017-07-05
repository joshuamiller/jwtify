(require
  '[cljs.repl :as repl]
  '[cljs.repl.node :as node])

(repl/repl* (node/repl-env)
  {:output-dir "target/jwtify"
   :optimizations :none
   :cache-analysis true
   :source-map true})
