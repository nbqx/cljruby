(ns cljruby.core
  (:use [cljruby.config]
        [cljruby.ruby-obj]))

(declare to-ruby-obj to-ruby-array to-ruby-hash to-clj-obj to-vector to-hashmap)

(defn simple-run [^String ruby-script]
  (.runScriptlet *scripting-container* ruby-script))

(defn call-method [^String source ^String method-name & args]
  (let [receiver (.runScriptlet *scripting-container* source)
        context (get-simple-context *scripting-container*)]
    (.callMethod *scripting-container* context method-name (to-array args))))

(defn call-module-method [^String source ^String module-name ^String method-name & args]
  (let [context (get-simple-context *scripting-container*)
        module (get-ruby-module source module-name)]
    (.callMethod module context method-name (into-array org.jruby.RubyBasicObject (map to-ruby-obj args)))))

(defn call-class-method [^String source ^String klazz-name ^String method-name & args]
  (let [context (get-simple-context *scripting-container*)
        klazz (get-ruby-class source klazz-name)]
    (.callMethod klazz context method-name (into-array org.jruby.RubyBasicObject (map to-ruby-obj args)))))

(defmulti to-ruby-obj class)
(defmethod to-ruby-obj Boolean [x] (get-ruby-boolean x))
(defmethod to-ruby-obj String [x] (get-ruby-string x))
(defmethod to-ruby-obj clojure.lang.Keyword [x] (get-ruby-symbol x))
(defmethod to-ruby-obj Long [x] (get-ruby-fixnum x))
(defmethod to-ruby-obj clojure.lang.BigInt [x] (get-ruby-bignum x))
(defmethod to-ruby-obj Double [x] (get-ruby-float x))
(defmethod to-ruby-obj clojure.lang.PersistentVector [x] (to-ruby-array x))
(defmethod to-ruby-obj clojure.lang.PersistentList [x] (to-ruby-array x))
(defmethod to-ruby-obj clojure.lang.PersistentArrayMap [x] (to-ruby-hash x))
(defmethod to-ruby-obj clojure.lang.PersistentHashMap [x] (to-ruby-hash x))
(defmethod to-ruby-obj :default [x] (throw (Exception. "非対応だよ")))

(defn to-ruby-array [col]
  (when (coll? col)
    (org.jruby.RubyArray/newArray (get-simple-runtime *scripting-container*) (map to-ruby-obj col))))

(defn to-ruby-hash [m]
  (let [k (keys m)
        v (vals m)
        mp (zipmap (map to-ruby-obj k) (map to-ruby-obj v))]
    (org.jruby.RubyHash/newHash (get-simple-runtime *scripting-container*)
                                (java.util.HashMap. mp)
                                (org.jruby.RubyNil/createNilClass (get-simple-runtime *scripting-container*)))))

(defmulti to-clj-obj class)
(defmethod to-clj-obj org.jruby.RubyBoolean [x] (org.jruby.javasupport.JavaEmbedUtils/rubyToJava x))
(defmethod to-clj-obj org.jruby.RubyString [x] (org.jruby.javasupport.JavaEmbedUtils/rubyToJava x))
(defmethod to-clj-obj org.jruby.RubySymbol [x] (keyword (.toString x)))
(defmethod to-clj-obj org.jruby.RubyFixnum [x] (org.jruby.javasupport.JavaEmbedUtils/rubyToJava x))
(defmethod to-clj-obj org.jruby.RubyBignum [x] (bigint (.toJava x java.math.BigInteger)))
(defmethod to-clj-obj org.jruby.RubyFloat [x] (.toJava x Double))
(defmethod to-clj-obj org.jruby.RubyArray [x] (to-vector x))
(defmethod to-clj-obj org.jruby.RubyHash [x] (to-hashmap x))
(defmethod to-clj-obj :default [x] x)

(defn to-vector [^org.jruby.RubyArray array]
  (let [a (.toArray array)]
    (vec (map to-clj-obj a))))

(defn to-hashmap [^org.jruby.RubyHash h]
  (let [k (map #(to-clj-obj %) (keys h))
        v (map #(to-clj-obj %) (vals h))]
    (zipmap k v)))

;; TODO: make lein cmd
;; (defn gem-install [^String & args]
;;   (let [gems (vec (concat ["-S" "gem" "install"] args))]
;;     (-> (get-simple-instance-config *scripting-container*)
;;         (org.jruby.Main.)
;;         (.run (into-array String gems))
;;         (.getStatus))))

;; (defn gem-uninstall [^String & args]
;;   (let [gems (vec (concat ["-S" "gem" "uninstall"] args))]
;;     (-> (get-simple-instance-config *scripting-container*)
;;         (org.jruby.Main.)
;;         (.run (into-array String gems))
;;         (.getStatus))))

;; (defn gem-list []
;;   (-> (get-simple-instance-config *scripting-container*)
;;       (org.jruby.Main.)
;;       (.run (into-array String ["-S" "gem" "list"]))
;;       (.getStatus)))