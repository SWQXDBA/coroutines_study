package asyncServer
import java.nio.channels.AsynchronousSocketChannel

class AsyncSocket {
    val socketChannel =  AsynchronousSocketChannel.open()

}