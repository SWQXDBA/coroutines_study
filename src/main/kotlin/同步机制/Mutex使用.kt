package 同步机制

import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex

fun main(): Unit = runBlocking {
    val mutex = Mutex()
    launch {

        mutex.lock()
        println("锁定了1")
        delay(2000)
        mutex.unlock()
        println("解锁了1")

    }
    launch {
        mutex.lock()
        println("锁定了2")
        mutex.unlock()
        println("解锁了2")
    }

}