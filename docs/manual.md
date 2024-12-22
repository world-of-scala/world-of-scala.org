# Manualy

If for any reaseon you want to setup the development environment manually, here are the steps:

## Install the npm dependencies:

```bash
pushd modules/client
npm install
pushd pushd scalablytyped
npm install
popd
popd
```

## Start the development servers:

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


