(ns cljruby.ruby-obj-spec
  (:use [speclj.core]
        [cljruby.core]
        [cljruby.ruby-obj]))

(describe "clojure -> ruby"
          (it "should be RubyBoolean"
              (should= org.jruby.RubyBoolean (class (get-ruby-boolean true))))
          (it "should be RubyString"
              (should= org.jruby.RubyString (class (get-ruby-string "テスト"))))

          (it "should be RubySymbol"
              (should= org.jruby.RubySymbol (class (get-ruby-symbol :key))))

          (it "should be RubyFixnum"
              (should= org.jruby.RubyFixnum (class (get-ruby-fixnum 10))))

          (it "should be RubyBignum"
              (should= org.jruby.RubyBignum (class (get-ruby-bignum 9223372036854775808))))

          (it "should be RubyFloat"
              (should= org.jruby.RubyFloat (class (get-ruby-float 3.14)))))

(describe "get-ruby-module"
          (with script (slurp "spec/fixtures/xxx.rb"))

          (it "should be module name \"Plugin\""
              (should= "Plugin" (to-clj-obj (.name (get-ruby-module @script "Plugin"))))))

(describe "get-ruby-class"
          (with script (slurp "spec/fixtures/xxx.rb"))

          (it "should be class name \"MyClass\""
              (should= "MyClass" (to-clj-obj (.name (get-ruby-class @script "MyClass")))))

          (it "should be class name \"Plugin::NestedClass\""
              (should= "Plugin::NestedClass" (to-clj-obj (.name (get-ruby-class (get-ruby-module @script "Plugin") "NestedClass"))))))

(run-specs)