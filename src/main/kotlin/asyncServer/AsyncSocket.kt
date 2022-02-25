package asyncServer
import com.sun.beans.WeakCache
import java.nio.channels.AsynchronousSocketChannel
import java.util.*

class AsyncSocket {
    val socketChannel =  AsynchronousSocketChannel.open()

}