package Selector服务器.自定义实现异步通道调用


import kotlinx.coroutines.*
import java.net.InetSocketAddress
import java.net.Socket
import java.net.SocketAddress
import java.nio.ByteBuffer
import java.nio.channels.SelectionKey
import java.nio.channels.Selector
import java.nio.channels.ServerSocketChannel
import java.nio.channels.SocketChannel
import java.text.SimpleDateFormat
import java.util.*
import javax.sound.sampled.Port
import kotlin.concurrent.thread

class SelectorServer(private val port: Int) {
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    lateinit var selector: Selector
    fun write(key: SelectionKey) {
        val message = key.attachment() as String? ?: return
        val channel = key.channel() as SocketChannel
        val put = ByteBuffer.wrap((message + "\n").toByteArray())
        //  put.flip()
        channel.write(put)
        key.interestOps(SelectionKey.OP_READ)
        key.attach(null)
    }

    fun read(key: SelectionKey) {
        try {
            val socketChannel = key.channel() as SocketChannel
            val byteBuffer = ByteBuffer.allocate(4096)
            socketChannel.read(byteBuffer)

            if (byteBuffer.limit() == 0) {
                return
            }
            val message = String(byteBuffer.array(), 0, byteBuffer.limit())
            val format = dateFormat.format(Date(System.currentTimeMillis()))
            println("[$format ${socketChannel.remoteAddress}]: $message")
            val iterator = selector.selectedKeys().iterator()
            while (iterator.hasNext()) {
                val selectionKey = iterator.next()
                val channel = selectionKey.channel()
                if (channel is SocketChannel) {
                    selectionKey.attach(message)
                    selectionKey.interestOps(SelectionKey.OP_WRITE)
                }
            }

        } catch (e: Exception) {
            key.channel().close()
        }


    }

    fun start() {
        val serverSocketChannel = ServerSocketChannel.open()
        serverSocketChannel.bind(InetSocketAddress(port))
        serverSocketChannel.configureBlocking(false)
        selector = Selector.open()
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT)
        println("server started")
        while (true) {
            //如果每次select时 某个 SelectKey.interestOps 与上次不一致 则也认为更新了
                //所以可以在中途从read切换到write来进入到下一轮
            val count = selector.select()
            if (count > 0) {
                println("count" + count)
                val selectedKeys = selector.selectedKeys()
                val iterator = selectedKeys.iterator()
                while (iterator.hasNext()) {
                    val next = iterator.next()
                    if (next.isValid) {
                        if (next.isAcceptable) {
                            //从已就绪的集合中移除 但不会移除注册
                            iterator.remove()
                            val channel = next.channel() as ServerSocketChannel
                            val socketChannel = channel.accept()
                            if (socketChannel != null) {
                                socketChannel.configureBlocking(false)
                                socketChannel.register(selector, SelectionKey.OP_READ)
                            }
                        }
                        if (next.isReadable) {

                            //下次select前如果没切换 则无法select出来 会阻塞
                            thread {
                                Thread.sleep(500L)
                                println("to write")
                                //不会触发  只会影响select的检测
                                next.interestOps(SelectionKey.OP_WRITE)
                            }


                     /*       println("to write")
                            next.interestOps(SelectionKey.OP_WRITE)*/
                        }

                        //如果read时候连接中断了 进入next.isWritable判断的时候会报错
                        continue
                    }
                    if (next.isWritable) {
                        thread {
                            Thread.sleep(500L)
                            println("to write")
                            next.interestOps(SelectionKey.OP_READ)
                        }

                    }
                }


            }
        }

    }

}


fun main() = runBlocking {
    SelectorServer(3000).start()
}