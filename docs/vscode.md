# VS Code

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

