package com.bfritz.rpi

import io.netty.bootstrap.ServerBootstrap
import io.netty.buffer.Unpooled
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.{
  ChannelFuture,
  ChannelFutureListener,
  ChannelHandlerContext,
  ChannelInboundHandlerAdapter,
  ChannelInitializer}
import io.netty.channel.socket.SocketChannel
import io.netty.channel.socket.nio.NioServerSocketChannel
import io.netty.handler.codec.http.{
  DefaultFullHttpResponse,
  HttpRequest,
  HttpServerCodec}
import io.netty.handler.codec.http.HttpHeaders._
import io.netty.handler.codec.http.HttpHeaders.Names._
import io.netty.handler.codec.http.HttpResponseStatus.{CONTINUE,OK}
import io.netty.handler.codec.http.HttpVersion.HTTP_1_1
//import io.netty.handler.logging.{LoggingHandler, LogLevel}
import io.netty.util.CharsetUtil.US_ASCII

import java.net.InetSocketAddress

object HelloWorldNetty extends App {
  val port = Option(System.getenv("PORT")).map(_.toInt).getOrElse(8080)
  startServer(port).sync.channel.closeFuture.sync

  def startServer(port: Int): ChannelFuture = {
    val bossGroup, workerGroup = new NioEventLoopGroup

    new ServerBootstrap()
      .group(bossGroup, workerGroup)
      .channel(classOf[NioServerSocketChannel])
      //.handler(new LoggingHandler(LogLevel.INFO))
      .childHandler(new HttpHelloWorldInitializer)
      .bind(new InetSocketAddress(port))
  }

  class HttpHelloWorldInitializer extends ChannelInitializer[SocketChannel] {
    override def initChannel(ch: SocketChannel) {
      ch.pipeline()
        .addLast("codec", new HttpServerCodec)
        .addLast("handler", new HelloWorldHandler)
    }
  }

  class HelloWorldHandler extends ChannelInboundHandlerAdapter {
    val Hello = Unpooled.unreleasableBuffer(Unpooled.copiedBuffer(
      "Hello World from Netty!\n", US_ASCII))

    override def channelReadComplete(ctx: ChannelHandlerContext) {
      ctx.flush
    }

    override def exceptionCaught(ctx: ChannelHandlerContext, cause: Throwable) {
      cause.printStackTrace()
      ctx.close
    }

    override def channelRead(ctx: ChannelHandlerContext, msg: Object) {
      msg match {
        case req: HttpRequest => {
          if (is100ContinueExpected(req)) {
              ctx.write(new DefaultFullHttpResponse(HTTP_1_1, CONTINUE))
          }
          val keepAlive = isKeepAlive(req)
          val response = new DefaultFullHttpResponse(HTTP_1_1, OK, Hello.duplicate)
          response.headers.set(CONTENT_TYPE, "text/plain")
          response.headers.set(CONTENT_LENGTH, response.content.readableBytes)
          if (!keepAlive) {
            ctx.write(response).addListener(ChannelFutureListener.CLOSE)
          } else {
            response.headers().set(CONNECTION, Values.KEEP_ALIVE)
            ctx.write(response)
          }
        }
        case _ => Nil
      }
    }
  }
}
