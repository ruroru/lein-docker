# lein-docker

A [Leiningen](https://leiningen.org) plugin for building and pushing Docker
images for your project.

## Installation

Add the plugin to the `:plugins` vector of your `project.clj`:

```clojure
:plugins [[org.clojars.jj/lein-docker "1.0.0-SNAPSHOT"]]
```

## Usage

Configure the target repository under the `:docker` key in your `project.clj`:

```clojure
:docker {:repo "your-org/your-app"}
```

The image is tagged as `<repo>:<version>`, using the project's `:version`.

Then run:

```sh
lein docker image build   # docker build -t your-org/your-app:<version> .
lein docker image push    # docker push your-org/your-app:<version>
lein docker image all     # build, then push
```

Both subtasks require the `docker` CLI to be installed and available on your
`PATH`.

## License

Copyright © 2026

This program and the accompanying materials are made available under the
terms of the Eclipse Public License 2.0 which is available at
http://www.eclipse.org/legal/epl-2.0.

This Source Code may also be made available under the following Secondary
Licenses when the conditions for such availability set forth in the Eclipse
Public License, v. 2.0 are satisfied: GNU General Public License as published by
the Free Software Foundation, either version 2 of the License, or (at your
option) any later version, with the GNU Classpath Exception which is available
at https://www.gnu.org/software/classpath/license.html.
