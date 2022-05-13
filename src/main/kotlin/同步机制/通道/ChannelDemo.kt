package 同步机制.通道

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel

fun main(): Unit = runBlocking {
    val channel = Channel<String>(3)
    repeat(3){co->

        launch {
            repeat(10){
                num->
                yield()
                channel.send(num.toString())
                println("$co 发送了$num")
                //让出线程
                yield()

            }
        }
    }

    launch {
      while(true){
          val receive = channel.receive()
          println("接收了$receive")

      }
    }
}