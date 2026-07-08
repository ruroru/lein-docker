(ns leiningen.docker
  (:require [leiningen.core.main :as main]
            [clojure.string :as str]))

(defn- run-cmd [& args]
  (main/info "Running:" (str/join " " args))
  (let [pb (doto (ProcessBuilder. ^"[Ljava.lang.String;" (into-array String args))
             (.inheritIO))
        process (.start pb)
        exit-code (.waitFor process)]
    (when-not (zero? exit-code)
      (main/abort "Command failed with exit code:" exit-code))))

(defn- get-repo [project]
  (let [repo (get-in project [:docker :repo])]
    (if (str/blank? repo)
      (main/abort "No Docker repository specified! Please add `:docker {:repo \"your/repo\"}` to your project.clj.")
      repo)))

(defn- build-image [repo version]
  (let [tag (str repo ":" version)]
    (main/info "Building Docker image:" tag)
    (run-cmd "docker" "build" "-t" tag ".")))

(defn- push-image [repo version]
  (let [tag (str repo ":" version)]
    (main/info "Pushing Docker image:" tag)
    (run-cmd "docker" "push" tag)))

(defn docker
  "Build and push Docker images for your project.

Reads `:docker {:repo \"your/repo\"}` from project.clj and tags the image with
the project's :version.

Subtasks:
  image build   Build the Docker image.
  image push    Push the Docker image to the repository.
  image all     Build and then push the Docker image."
  [project & args]
  (let [version (:version project)
        repo (get-repo project)]
    (case (vec args)
      ["image" "build"] (build-image repo version)
      ["image" "push"]  (push-image repo version)
      ["image" "all"]   (do (build-image repo version)
                            (push-image repo version))
      (main/abort (str "Invalid command: " (str/join " " args)
                       "\nPlease use: 'lein docker image build', 'lein docker image push', or 'lein docker image all'")))))
