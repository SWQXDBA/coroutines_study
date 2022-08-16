package 上下文与调度器

import kotlinx.coroutines.*
import kotlin.coroutines.EmptyCoroutineContext

fun main ():Unit = runBlocking{
    launch(Dispatchers.Unconfined) { // 非受限的——将和主线程一起工作
        println("Unconfined      : I'm working in thread ${Thread.currentThread().name}")
        delay(500)
        println("Unconfined      : After delay in thread ${Thread.currentThread().name}")
    }
    launch { // 父协程的上下文，主 runBlocking 协程
        println("main runBlocking: I'm working in thread ${Thread.currentThread().name}")
        delay(1000)
        delay(1000000)
        println("main runBlocking: After delay in thread ${Thread.currentThread().name}")
    }

}