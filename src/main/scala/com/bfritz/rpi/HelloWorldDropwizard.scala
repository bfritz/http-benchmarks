package com.bfritz.rpi

import com.yammer.dropwizard.Service
import com.yammer.dropwizard.config.{
  Bootstrap,
  Configuration,
  Environment}

object HelloWorldDropwizard extends Service[Configuration] {

  override def initialize(bootstrap: Bootstrap[Configuration]) {
    bootstrap.setName("hello-world")
  }

  override def run(config: Configuration, env: Environment) {
    env.addResource(new HelloWorldResource(config))
  }

  final def main(args: Array[String]) {
      run(args)
  }
}
