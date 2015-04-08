package app.infrastructure

import unfiltered.netty.Server

trait AppServer {

  def server: Server

  def start = server.start()

  def stop = server.stop()

}
