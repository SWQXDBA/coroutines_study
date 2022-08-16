package 深入理解kotlin协程

import kotlinx.coroutines.*
import sun.rmi.server.Dispatcher
import java.rmi.Remote
import java.rmi.server.RemoteCall
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

open class MyDispatcher: CoroutineDispatcher() {
    companion object : MyDispatcher()
    val service: ExecutorService = Executors.newFixedThreadPool(10)
    override fun dispatch(context: CoroutineContext, block: Runnable) {
        service.submit(block)
    }


}

fun main() {
    CoroutineScope(EmptyCoroutineContext).launch (MyDispatcher){
        repeat(20){
            println(Thread.currentThread().name)
            delay(100)
        }

    }
}