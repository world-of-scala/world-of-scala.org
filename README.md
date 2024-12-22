# Welcome World Of Scala project!

This project intention is the study of the Scala programming language and its ecosystem.

This is a full stack Scala project, using Scala.js for the client and ZIO for the server.



## Libraries && Tooling

### Libraries
* Backend:
  * ZIO [https://zio.dev/](https://zio.dev/)
  * Tapir [https://tapir.softwaremill.com/en/latest/](https://tapir.softwaremill.com/en/latest/)
  * ZIO-Quill [ZIO-Quill](https://zio.dev/zio-quill/)
  * Flyway [https://flywaydb.org/](https://flywaydb.org/)
* Frontend:
  * Laminar [https://laminar.dev/](https://laminar.dev/)
  * Three.js [https://threejs.org/](https://threejs.org/)
  * Chart.js [https://www.chartjs.org/](https://www.chartjs.org/)


### Tooling

* Backend
 * sbt [https://www.scala-sbt.org/](https://www.scala-sbt.org/)
 * docker [https://www.docker.com/](https://www.docker.com/)

* Frontend
 * npm [https://www.npmjs.com/](https://www.npmjs.com/)
 * vite [https://vitejs.dev/](https://vitejs.dev/)
 * ScalablyTyped [https://scalablytyped.org/](https://scalablytyped.org/)


## Pre-requisites

- JDK
- sbt
- Node.js

Decent vesions of JDK, sbt and Node.js are required.

## Getting started

To get started, run the following command:

```bash
./scripts/fullstackRun.sh
```

http://localhost:8080/public/index.html


## Development

Development is done in two parts: the server and the client.

* The server is a ZIO application that serves the client.

* The client is a Scala.js application that is served by the server.

  * The client is built using the `fastLinkJS` command.
  * Vite is used to serve the client in development mode, with hot reloading.



### VS Code

This project [is configured to work](.vscode/tasks.json) with Visual Studio Code.

* metals is needed for Scala support.
* The Scala (Metals) extension is recommended.

To open the project in VS Code, run the following command:

```bash
code .
```

With a little luck, you will be prompted to install the recommended extensions, if not already installed.

The developpement environment should setup itself.

* npm install
* runDemo
  * sbt ~client/fastLinkJS
  * vite serve
  * runServer with reStart on file change



### Manualy

If for any reaseon you want to setup the development environment manually, here are the steps:

#### Install the npm dependencies:

```bash
pushd modules/client
npm install
pushd pushd scalablytyped
npm install
popd
popd
```

#### Start the development servers:

* Backend Scala:  In a terminal, run the following command:
```bash
MOD=dev sbt server/run
```
* ScalaJS: In another terminal, run the following command:
```bash
MOD=dev sbt ~client/fastLinkJS
```
* Vite: In another terminal, run the following command:
```bash
cd modules/client
npm run dev
```


