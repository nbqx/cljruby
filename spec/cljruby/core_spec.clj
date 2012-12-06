(ns cljruby.core-spec
  (:use [speclj.core]
        [cljruby.core]))

(describe "simple-run"
          (it "should exec ruby script"
              (should= "ok" (simple-run (slurp "spec/fixtures/ok.rb")))))

(describe "call-method"
          (with script (slurp "spec/fixtures/xxx.rb"))

          (it "should be 'hello, test'"
              (should= "hello, test" (call-method @script "func" "test")))

          (it "should be RubyHash"
              (should= org.jruby.RubyHash (class (call-method @script "get_hash"))))
          
          (it "should be 8"
              (should= 8 (call-method @script "func2" 2 4))))

(describe "call-module-method"
          (with script (slurp "spec/fixtures/xxx.rb"))
          
          (it "should be \"ok\""
              (should= "ok" (to-clj-obj (call-module-method @script "Plugin" "init"))))

          (it "should be 3"
              (should= 3 (to-clj-obj (call-module-method @script "Plugin" "add_some" 1 2)))))

(describe "call-class-method"
          (with script (slurp "spec/fixtures/xxx.rb"))

          (it "should be \"ok\""
              (should= "ok" (to-clj-obj (call-class-method @script "MyClass" "ok"))))

          (it "should be [\"test\",\"test\",\"test\",\"test\"]"
              (should= ["test","test","test","test"] (to-clj-obj (call-class-method @script "MyClass" "exec" 4)))))

(describe "to-ruby-obj"
          (it "should be RubyBoolean"
              (should= org.jruby.RubyBoolean (class (to-ruby-obj true))))
          
          (it "should be RubyString"
              (should= org.jruby.RubyString (class (to-ruby-obj "テスト"))))

          (it "should be RUbySymbol"
              (should= org.jruby.RubySymbol (class (to-ruby-obj :key))))

          (it "should be RubyFixnum"
              (should= org.jruby.RubyFixnum (class (to-ruby-obj 10))))

          (it "should be RubyBignum"
              (should= org.jruby.RubyBignum (class (to-ruby-obj 9223372036854775808))))

          (it "should be RubyFloat"
              (should= org.jruby.RubyFloat (class (to-ruby-obj 3.14))))

          (it "should be RubyArray"
              (should= org.jruby.RubyArray (class (to-ruby-obj [1 2 3]))))
          
          (it "should be RubyHash"
              (should= org.jruby.RubyHash (class (to-ruby-obj {:a "test" :b 10 :c 3.14})))))

(describe "to-ruby-array and to-array"
          (with my-vec [1 "text" :kwd 3.5])
          (with my-list '(1 "text" :kwd 3.5))

          (it "should be RubyArray, passed Vector"
              (should= org.jruby.RubyArray (class (to-ruby-array @my-vec))))

          (it "should be RubyArray, passed List"
              (should= org.jruby.RubyArray (class (to-ruby-array @my-list))))

          (with my-ruby-array (to-ruby-array @my-vec))

          (it "should be 1 in the 1st item of @my-ruby-array"
              (should= 1 (.get @my-ruby-array 0)))

          (it "should be 'text' in the 2nd item of @my-ruby-array"
              (should= "text" (.get @my-ruby-array 1)))

          (it "should be RubySymbol class in the 3rd item of @my-ruby-array"
              (should= org.jruby.RubySymbol (class (.get @my-ruby-array 2))))

          (it "should be 3.5 in the 4th item of @my-ruby-array"
              (should= 3.5 (.get @my-ruby-array 3)))

          (it "should be [1 \"text\" :kwd 3.5]"
              (should= [1 "text" :kwd 3.5] (to-vector @my-ruby-array))))

(describe "to-ruby-hash and to-hashmap"
          (with my-hashmap {:a 1 :b "text" :c 5.5})

          (it "should be RubyHash class"
              (should= org.jruby.RubyHash (class (to-ruby-hash @my-hashmap))))

          (with my-ruby-hashmap (to-ruby-hash @my-hashmap))
          
          (it "should be 1, accessing a RubySymbol"
              (should= 1 (.get @my-ruby-hashmap (to-ruby-obj :a))))

          (it "should be {:a 1 :b \"text\" :c 5.5}"
              (should= {:a 1 :b "text" :c 5.5} (to-hashmap @my-ruby-hashmap))))

(describe "to-clj-obj"
          (it "should be false"
              (should= false (to-clj-obj (to-ruby-obj false))))
          
          (it "should be 1"
              (should= 1 (to-clj-obj (to-ruby-obj 1))))

          (it "should be Long"
              (should= Long (class (to-clj-obj (to-ruby-obj 1)))))

          (it "should be \"テスト\""
              (should= "テスト" (to-clj-obj (to-ruby-obj "テスト"))))

          (it "should be :key"
              (should= :key (to-clj-obj (to-ruby-obj :key))))

          (it "should be clojure.lang.BigInt"
              (should= clojure.lang.BigInt (class (to-clj-obj (to-ruby-obj 9223372036854775808)))))
          
          (it "should be 9223372036854775808"
              (should= 9223372036854775808 (to-clj-obj (to-ruby-obj 9223372036854775808))))

          (it "should be Double"
              (should= Double (class (to-clj-obj (to-ruby-obj 3.14)))))
          
          (it "should be 3.14"
              (should= 3.14 (to-clj-obj (to-ruby-obj 3.14))))

          (it "should be [1 \"test\" :a 3.14]"
              (should= [1 "test" :a 3.14] (to-clj-obj (to-ruby-obj [1 "test" :a 3.14]))))

          (it "should be {:a 1 :b \"test\" :c 3.14}"
              (should= {:a 1 :b "test" :c 3.14} (to-clj-obj (to-ruby-obj {:a 1 :b "test" :c 3.14}))))

          (it "should be {:a [1 [2 [3]]], :c 100.45, :b \"this is test\"}"
              (should= {:a [1 [2 [3]]], :c 100.45, :b "this is test"} (to-clj-obj (to-ruby-obj {:a [1 [2 [3]]], :c 100.45, :b "this is test"})))))

(run-specs)