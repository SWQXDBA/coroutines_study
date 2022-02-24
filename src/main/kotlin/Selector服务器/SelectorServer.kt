package Selector服务器

import kotlinx.coroutines.*
import java.net.InetSocketAddress
import java.net.SocketAddress
import java.nio.ByteBuffer
import java.nio.channels.SelectionKey
import java.nio.channels.Selector
import java.nio.channels.ServerSocketChannel
import java.nio.channels.SocketChannel
import java.text.SimpleDateFormat
import java.util.*
import javax.sound.sampled.Port

 class SelectorServer(private val port:Int) {
    private val dateFormat =  SimpleDateFormat("yyyy-MM-dd HH:mm:ss" )
     lateinit var selector:Selector
    fun read(key:SelectionKey): String? {
        try {
            val socketChannel = key.channel() as SocketChannel
            val byteBuffer = ByteBuffer.allocate(4096)
            socketChannel.read(byteBuffer)

            if (byteBuffer.limit()==0) {
                return null
            }
            val message = String(byteBuffer.array(),0,byteBuffer.limit())
            println("read: $message")
            byteBuffer.flip()
            return message
        }catch (e:Exception){
            key.channel().close()
        }

        return null
    }
    fun start(){
        val serverSocketChannel = ServerSocketChannel.open()
        serverSocketChannel.bind(InetSocketAddress(port))
        serverSocketChannel.configureBlocking(false)
        selector = Selector.open()
        serverSocketChannel.register(selector,SelectionKey.OP_ACCEPT)
        println("server started")
        while(true){
            val count = selector.select()
            println(count)
            if(count>=0){
                val selectedKeys = selector.selectedKeys()
                val iterator = selectedKeys.iterator()
                while(iterator.hasNext()){
                    val next = iterator.next()
                    if(next.isValid){
                        if(next.isAcceptable){
                            val channel = next.channel() as ServerSocketChannel
                            val socketChannel = channel.accept()
                            if(socketChannel!=null){
                                socketChannel.configureBlocking(false)
                                socketChannel.register(selector,SelectionKey.OP_READ)
                            }

                        }
                        if(next.isReadable){
                            val message = read(next)
                            if(message!=null){
                                val format = dateFormat.format(Date(System.currentTimeMillis()))
                                println("[$format]: $message")
                            }

                        }
                    }

                }
            }

        }

    }
}

fun main() = runBlocking{
    SelectorServer(3000).start()
}