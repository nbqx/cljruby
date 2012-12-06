class MyClass
  class << self
    def ok
      "ok"
    end

    def exec a
      ["test"]*a
    end
  end
end

module Plugin
  extend self

  class NestedClass
    class << self
      def nested
        "nested"
      end
    end
  end

  def init
    "ok"
  end

  def add_some a,b
    a+b
  end
end

def func a
  "hello, #{a}"
end

def func2 a,b
  a * b
end

def get_hash
  {:name => 'test', "body" => 'this is text'}
end
