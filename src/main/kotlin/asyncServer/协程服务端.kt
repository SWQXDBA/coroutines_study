package asyncServer

import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.net.InetSocketAddress
import java.nio.ByteBuffer
import java.nio.channels.AsynchronousChannelGroup
import java.nio.channels.AsynchronousServerSocketChannel
import java.nio.channels.AsynchronousSocketChannel
import java.nio.channels.CompletionHandler
import java.util.concurrent.Executors
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class MyHandler<A : Attachment> : CompletionHandler<Int, A> {
    override fun completed(result: Int, attachment: A) {
        val byteBuffer = attachment.byteBuffer
        if (attachment.readMode) {
            //此时里面已经有数据了 是之前写入的 现在要转换为读取模式
            //此操作把limit放到当前位置 来保证不会多读
            byteBuffer.flip()
            val byteArray = ByteArray(byteBuffer.limit())
            byteBuffer.get(byteArray)
            byteBuffer.clear()
            val message = String(byteArray)
            byteBuffer.put(message.toByteArray())
            println("${Thread.currentThread().name}来自客户端${attachment.client.remoteAddress} 消息  :$message")
            attachment.readMode = false
            //写入的时候 要从缓冲区把数据读走  所以要flip
            byteBuffer.flip()
            attachment.client.write(byteBuffer, attachment, this)
        } else {
            byteBuffer.clear()
            attachment.readMode = true
            attachment.client.read(byteBuffer, attachment, this)
        }
    }

    override fun failed(exc: Throwable?, attachment: A) {
        // TODO("Not yet implemented")
    }
}

open class Attachment(
    // val server: AsynchronousServerSocketChannel,
    val client: AsynchronousSocketChannel,
    val byteBuffer: ByteBuffer,
    var readMode: Boolean = true
)

class Handler<V : AsynchronousSocketChannel, A>(private val server: AsynchronousServerSocketChannel) :
    CompletionHandler<V, A> {

    private val byteBuffer: ByteBuffer = ByteBuffer.allocateDirect(2048)
    override fun completed(client: V, attachment: A) {
        server.accept(null, Handler(server))
        println("连接到服务端:${client.remoteAddress}")
        client.read(byteBuffer, Attachment(client, byteBuffer), MyHandler())
    }

    override fun failed(exc: Throwable?, attachment: A) {
        //   TODO("Not yet implemented")
    }

}

class MyServer(private val port: Int) {
    val asynchronousChannelGroup = AsynchronousChannelGroup.withThreadPool(Executors.newSingleThreadExecutor())
    fun start() {

        val socket =
            AsynchronousServerSocketChannel.open(asynchronousChannelGroup)
                .bind(InetSocketAddress(port))
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
                            writeIntoConnect(client, "我已经收到$message")
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
    MyServer(5000).start()
}

fun writeIntoConnect(client: AsynchronousSocketChannel, echoMessage: String) {
    val byteBuffer1: ByteBuffer = ByteBuffer.allocateDirect(2048)
    byteBuffer1.put(echoMessage.toByteArray())
    byteBuffer1.flip()
    client.write(byteBuffer1)
}

suspend fun readFromConnect(client: AsynchronousSocketChannel) = suspendCoroutine<String> {
    val byteBuffer1: ByteBuffer = ByteBuffer.allocateDirect(2048)
    client.read(byteBuffer1, Attachment(client, byteBuffer1), object : CompletionHandler<Int, Attachment> {
        override fun completed(result: Int?, attachment: Attachment?) {
            val byteBuffer = attachment!!.byteBuffer
            //此时里面已经有数据了 是之前写入的 现在要转换为读取模式
            //此操作把limit放到当前位置 来保证不会多读
            byteBuffer.flip()
            val byteArray = ByteArray(byteBuffer.limit())
            byteBuffer.get(byteArray)
            byteBuffer.clear()
            val message = String(byteArray)
            it.resume(message)
        }

        override fun failed(exc: Throwable?, attachment: Attachment?) {
            // TODO("Not yet implemented")
        }

    }
    )

}

suspend fun connect(socket: AsynchronousServerSocketChannel) = suspendCoroutine<AsynchronousSocketChannel> {
    socket.accept(null, object : CompletionHandler<AsynchronousSocketChannel, Any?> {
        override fun completed(client: AsynchronousSocketChannel?, attachment: Any?) {
            println("${Thread.currentThread().name} 连接到服务端:${client!!.remoteAddress}")
            it.resume(client)
        }

        override fun failed(exc: Throwable?, attachment: Any?) {
            // TODO("Not yet implemented")
        }
    })
}



