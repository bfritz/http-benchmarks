package com.bfritz.rpi

import org.scalatra.ScalatraServlet

class HelloWorldScalatraServlet extends ScalatraServlet {
  get("/") {
    "Hello World from Scalatra!\n"
  }
}
