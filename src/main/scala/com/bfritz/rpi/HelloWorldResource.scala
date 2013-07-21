package com.bfritz.rpi

//import com.yammer.metrics.annotation.Timed

import com.yammer.dropwizard.config.Configuration

import javax.ws.rs.{GET, Path, Produces}
import javax.ws.rs.core.MediaType

@Path("/")
@Produces(Array(MediaType.TEXT_PLAIN))
class HelloWorldResource(config: Configuration) {

  @GET
  //@Timed
  def hello(): String = {
    "Hello World from Dropwizard!\n"
  }
}
