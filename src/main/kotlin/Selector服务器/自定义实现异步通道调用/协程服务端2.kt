package Selector服务器.自定义实现异步通道调用

import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.net.InetSocketAddress
import java.nio.ByteBuffer
import java.nio.channels.AsynchronousChannelGroup
import java.nio.channels.AsynchronousServerSocketChannel
import java.nio.channels.AsynchronousSocketChannel
import java.nio.channels.CompletionHandler
import java.util.concurrent.Executors
import java.util.function.Consumer
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine


class MyServer(private val port: Int) {
    fun start() {
        val socket = MyAsyncServerSocketChannel(port)
        socket.start()
        runBlocking {
            while (true) {
                val client = connect(socket)
                launch {
                    while (true) {
                        val message = readFromConnect(client)
                        println("${Thread.currentThread().name}来自客户端${client.remoteAddress} 的消息 $message")
                        if (message == "bye\n") {
                            println("准备断开连接")
                            writeIntoConnect(client, "bye, too\n")
                            break
                        } else {
                            writeIntoConnect(client, "我已经收到$message \n")
                        }
                    }
                    println("连接已断开${client.remoteAddress}")
                }

            }
        }
    }
    // asynchronousChannelGroup.awaitTermination(0, TimeUnit.DAYS)
}


fun main() {
    MyServer(3000).start()
}

fun writeIntoConnect(client: MyAsyncSocketChannel, echoMessage: String) {
    val byteBuffer1: ByteBuffer = ByteBuffer.allocateDirect(2048)
    byteBuffer1.put(echoMessage.toByteArray())
    byteBuffer1.flip()
    client.write(byteBuffer1)
}

suspend fun readFromConnect(client: MyAsyncSocketChannel) = suspendCoroutine<String> { c ->
    client.read { byteBuffer ->
        //此时里面已经有数据了 是之前写入的 现在要转换为读取模式
        //此操作把limit放到当前位置 来保证不会多读
        byteBuffer.flip()
        val byteArray = ByteArray(byteBuffer.limit())
        byteBuffer.get(byteArray)
        byteBuffer.clear()
        val message = String(byteArray)
        c.resume(message)
    }
}

suspend fun connect(socket: MyAsyncServerSocketChannel) = suspendCoroutine<MyAsyncSocketChannel> { c ->
    socket.accept {
        c.resume(it)
    }

}



