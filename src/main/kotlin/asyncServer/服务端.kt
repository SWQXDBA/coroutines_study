package asyncServer

import kotlinx.coroutines.*
import java.net.InetSocketAddress
import java.nio.ByteBuffer
import java.nio.channels.AsynchronousChannelGroup
import java.nio.channels.AsynchronousServerSocketChannel
import java.nio.channels.AsynchronousSocketChannel
import java.nio.channels.CompletionHandler
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.ThreadPoolExecutor
import kotlin.concurrent.thread

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

class Handler<V : AsynchronousSocketChannel, A>(private val server: AsynchronousServerSocketChannel) : CompletionHandler<V, A> {

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


    fun start() = runBlocking {
        val socket =
            AsynchronousServerSocketChannel.open(AsynchronousChannelGroup.withThreadPool(Executors.newSingleThreadExecutor()))
                .bind(InetSocketAddress(port))
        socket.accept(null, Handler(socket))
    }

}

fun main()  {
    MyServer(5000).start()
    while (true){
        Thread.sleep(Long.MAX_VALUE)
    }


/*   runBlocking {
        launch {
            println(Thread.currentThread().name)

            awaitCancellation()
        }
    }*/
}

/*
suspend fun connect(serverSocket: ServerSocket) = suspendCoroutine<Socket> {

    COROUTINE_SUSPENDED

}*/
