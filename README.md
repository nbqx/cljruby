# cljruby

call ruby method(in module or class method) in jruby script from clojure

## Usage

jruby script

```ruby
module Plugin
  extend self

  class NestedClass
    class << self
      def nested
        "nested"
      end
    end
  end

  def add_some a,b
    a+b
  end
end
```

in clojure

```clojure
(use 'cljruby.core)
(def add_some (fn [x y] (to-clj-obj (call-module-method (slurp "/path/to/xxx.rb") "Plugin" "add_some" x y))))
(add_some 10 23) ;; => 33
```

```clojure
(use '[cljruby.core] 
     '[cljruby.ruby-obj :only [get-ruby-module]])
(to-clj-obj (call-class-method (get-ruby-module (slurp "/path/to/xxx.rb") "Plugin") "NestedClass" "nested")) ;; => "nested"
```

## TODO

* doing `lein gem install || uninstall || list`, using gems in jruby script
