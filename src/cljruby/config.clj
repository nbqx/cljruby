(ns cljruby.config)

(defn set-ruby-env [& {:keys [version gem-home gem-path path] :or {version org.jruby.CompatVersion/RUBY1_9
                                                                    gem-home "vendor/gems"
                                                                    gem-path "vendor/gems"
                                                                    path (System/getenv "PATH")}}]
  (let [container (org.jruby.embed.ScriptingContainer.)]
    (doto container
      (.setCompatVersion version)
      (.setEnvironment (merge (into {} (System/getenv)) {"GEM_HOME" gem-home
                                                         "GEM_PATH" gem-path
                                                         "PATH" path})))))

(def ^:dynamic *scripting-container* (set-ruby-env))

(defn get-simple-runtime [^org.jruby.embed.ScriptingContainer c]
  (.. c getProvider getRuntime))

(defn get-simple-context [^org.jruby.embed.ScriptingContainer c]
  (.getCurrentContext (get-simple-runtime c)))

(defn get-simple-instance-config [^org.jruby.embed.ScriptingContainer c]
  (.. c getProvider getRubyInstanceConfig))