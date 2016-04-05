package app

import io.netty.handler.codec.http.HttpResponse
import unfiltered.netty.ReceivedMessage
import unfiltered.response.ResponseString

package object infrastructure {

  type FrameworkResponse = unfiltered.response.ResponseFunction[HttpResponse]
  type FrameworkRequest = unfiltered.request.HttpRequest[ReceivedMessage]


  implicit class FrameworkRequestOps(req: FrameworkRequest) {
    def body: String = unfiltered.request.Body.string(req)
  }

  def responseString(message:String):FrameworkResponse = ResponseString(message)

}

