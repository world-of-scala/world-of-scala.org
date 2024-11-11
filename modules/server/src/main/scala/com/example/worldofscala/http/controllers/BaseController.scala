package com.example.worldofscala.http.controllers

import sttp.tapir.server.ServerEndpoint
import zio.Task

trait BaseController {

  val routes: List[ServerEndpoint[Any, Task]]

}
