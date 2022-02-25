package Selector服务器.自定义实现异步通道调用

import java.net.InetSocketAddress
import java.nio.ByteBuffer
import java.nio.channels.SelectionKey
import java.nio.channels.Selector
import java.nio.channels.ServerSocketChannel
import java.nio.channels.SocketChannel
import java.util.*
import java.util.function.Consumer
import kotlin.concurrent.thread


class MyAsyncServerSocketChannel(private val port: Int) {

    var selector: Selector

    init {
        val serverSocketChannel = ServerSocketChannel.open()
        serverSocketChannel.bind(InetSocketAddress(port))
        serverSocketChannel.configureBlocking(false)
        selector = Selector.open()
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT)
        println("server started")


    }

    var acceptConsumer: Consumer<MyAsyncSocketChannel>? = null
    fun accept(consumer: Consumer<MyAsyncSocketChannel>) {
        acceptConsumer = consumer;
    }

    val acceptList = LinkedList<MyAsyncSocketChannel>()
    fun start() {
        thread { doStart() }
    }

    fun doStart() {

        val map = mutableMapOf<SocketChannel, MyAsyncSocketChannel>()
        while (true) {
            val count = selector.select()
            if (count >= 0) {
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
                                val selectionKey =
                                    socketChannel.register(selector, SelectionKey.OP_READ )
                                val myAsyncSocketChannel = MyAsyncSocketChannel(socketChannel, selectionKey)
                                map[socketChannel] = myAsyncSocketChannel
                                synchronized(this) {
                                    acceptList.add(myAsyncSocketChannel)
                                    acceptConsumer?.let {
                                        it.accept(acceptList.poll())
                                        acceptConsumer = null
                                    }
                                }
                            }
                        }
                        if (next.isValid && next.isReadable) {
                            val myAsyncSocketChannel = map[next.channel()]
                            myAsyncSocketChannel?.doRead()
                        }
                    }
                }
            }

        }
    }
}

class MyAsyncSocketChannel(val socketChannel: SocketChannel, val selectionKey: SelectionKey) {
    val remoteAddress by socketChannel::remoteAddress
    private var readConsumer: Consumer<ByteBuffer>? = null
    private val messages = LinkedList<ByteBuffer>()

    /**
     * 尝试读取
     */
    fun read(consumer: Consumer<ByteBuffer>) {
        readConsumer = consumer
    }
    fun write(byteBuffer: ByteBuffer) {
        selectionKey.attach(byteBuffer)
        doWrite()
    }
    fun doRead() {
        val buffer = ByteBuffer.allocate(1024)
        socketChannel.read(buffer)
        messages.add(buffer)
        readConsumer?.let { rc ->
            messages.forEach { rc.accept(it) }
            messages.clear()
            readConsumer = null
        }
    }
    fun doWrite() {
        val attachment = selectionKey.attachment()
        attachment?.let {
            selectionKey.attach(null)
            socketChannel.write(it as ByteBuffer)
        }

    }


}