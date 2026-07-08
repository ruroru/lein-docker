(defproject org.clojars.jj/lein-docker "1.0.0-SNAPSHOT"
  :description "A Leiningen plugin for building and pushing Docker images."
  :url "https://github.com/ruroru/lein-docker"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url  "https://www.eclipse.org/legal/epl-2.0/"}
  :eval-in-leiningen true
  :repl-options {:init-ns leiningen.docker}

  :dependencies [[clj-commons/clj-yaml "1.0.27"]
                 [leiningen-core "2.13.0"]]

  :deploy-repositories [["clojars" {:url      "https://repo.clojars.org"
                                    :username :env/clojars_user
                                    :password :env/clojars_pass}]]

  :strict-check {:filter ["leiningen.core"]}

  :plugins [[org.clojars.jj/bump "1.0.4"]
            [org.clojars.jj/strict-check "1.1.0"]
            [org.clojars.jj/lein-git-tag "1.0.1"]
            [org.clojars.jj/bump-md "1.1.0"]]
  )
