(ns leiningen.docker-test
  (:require [clojure.test :refer :all]
            [clojure.string :as str]
            [leiningen.docker :as docker]
            [leiningen.core.main :as main]))

(def ^:private project {:version "1.2.3" :docker {:repo "me/app"}})

(defn- capture
  "Runs the docker task with every side effect mocked out: `run-cmd` records the
  docker argv instead of shelling out, `main/info` is silenced, and `main/abort`
  records its message and unwinds instead of exiting the JVM. Returns a map of
  :commands (vector of captured docker argv vectors) and :aborted (abort message
  string, or nil)."
  [project & args]
  (let [commands (atom [])
        aborted  (atom nil)]
    (with-redefs [leiningen.docker/run-cmd (fn [& cmd] (swap! commands conj (vec cmd)))
                  main/info  (fn [& _] nil)
                  main/abort (fn [& msg]
                               (reset! aborted (str/join " " (map str msg)))
                               (throw (ex-info "abort" {:abort true})))]
      (try
        (apply docker/docker project args)
        (catch clojure.lang.ExceptionInfo e
          (when-not (:abort (ex-data e)) (throw e)))))
    {:commands @commands :aborted @aborted}))

(deftest image-build-test
  (testing "image build tags with repo:version and builds from the current dir"
    (is (= {:commands [["docker" "build" "-t" "me/app:1.2.3" "."]]
            :aborted  nil}
           (capture project "image" "build")))))

(deftest image-push-test
  (testing "image push pushes the repo:version tag"
    (is (= {:commands [["docker" "push" "me/app:1.2.3"]]
            :aborted  nil}
           (capture project "image" "push")))))

(deftest image-all-test
  (testing "image all builds first, then pushes, in that order"
    (is (= {:commands [["docker" "build" "-t" "me/app:1.2.3" "."]
                       ["docker" "push" "me/app:1.2.3"]]
            :aborted  nil}
           (capture project "image" "all")))))

(deftest registry-qualified-repo-test
  (testing "a registry/namespace repo is passed through verbatim into the tag"
    (is (= [["docker" "push" "ghcr.io/me/app:9.9.9"]]
           (:commands (capture {:version "9.9.9" :docker {:repo "ghcr.io/me/app"}}
                               "image" "push"))))))

(deftest missing-repo-test
  (testing "aborts (and runs no docker command) when :docker :repo is absent"
    (let [{:keys [commands aborted]} (capture {:version "1.0.0"} "image" "build")]
      (is (empty? commands))
      (is (re-find #"No Docker repository" aborted)))))

(deftest blank-repo-test
  (testing "aborts when :docker :repo is blank"
    (let [{:keys [commands aborted]} (capture {:version "1.0.0" :docker {:repo "   "}}
                                              "image" "push")]
      (is (empty? commands))
      (is (re-find #"No Docker repository" aborted)))))

(deftest invalid-command-test
  (testing "aborts (and runs no docker command) on an unrecognized subtask"
    (let [{:keys [commands aborted]} (capture project "image" "frobnicate")]
      (is (empty? commands))
      (is (re-find #"Invalid command" aborted)))))

(deftest no-args-test
  (testing "aborts when no subtask is given"
    (let [{:keys [commands aborted]} (capture project)]
      (is (empty? commands))
      (is (re-find #"Invalid command" aborted)))))
