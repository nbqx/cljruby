(ns cljruby.ruby-obj
  (use [cljruby.config :only [*scripting-container* get-simple-runtime]]))

(defn get-ruby-boolean [^Boolean b]
  (org.jruby.RubyBoolean/newBoolean (get-simple-runtime *scripting-container*) b))

(defn get-ruby-string [^String s]
  (org.jruby.RubyString/newString (get-simple-runtime *scripting-container*) s))

(defn get-ruby-symbol [^clojure.lang.Keyword s]
  "(get-ruby-symbol :key)"
  (org.jruby.RubySymbol/newSymbol (get-simple-runtime *scripting-container*) (name s)))

(defn get-ruby-fixnum [^Long v]
  (org.jruby.RubyFixnum/newFixnum (get-simple-runtime *scripting-container*) v))

(defmulti get-ruby-bignum class)
(defmethod get-ruby-bignum clojure.lang.BigInt [v]
  (get-ruby-bignum (java.math.BigInteger. (.toString v))))
(defmethod get-ruby-bignum java.math.BigInteger [v]
  (org.jruby.RubyBignum/newBignum (get-simple-runtime *scripting-container*) v))

(defn get-ruby-float [^Double v]
  (org.jruby.RubyFloat/newFloat (get-simple-runtime *scripting-container*) v))

(defn get-ruby-module [^String source ^String module-name]
  (let [receiver (.runScriptlet *scripting-container* source)
        runtime (get-simple-runtime *scripting-container*)]
    (.fastGetModule runtime module-name)))

(defmulti get-ruby-class (fn [x y] [(class x) (class y)]))
(defmethod get-ruby-class [String String] [source klazz-name]
  (let [receiver (.runScriptlet *scripting-container* source)
        runtime (get-simple-runtime *scripting-container*)]
    (.fastGetClass runtime klazz-name)))
(defmethod get-ruby-class [org.jruby.RubyModule String] [module klazz-name]
  (.fastGetClass module klazz-name))

