# Production

To build the project for production, run the following command:

* With ESModule
```bash
./scripts/fullstackBuild.sh
```

* With CommonJS
```bash
./scripts/fullstackBuild.sh -n
```


In production mode the client is minified and the Scala server exposes the client as a static resource (public/assets).
