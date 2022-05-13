package 深入理解kotlin协程

import kotlin.concurrent.thread
import kotlin.coroutines.*

suspend fun fun1(): Int {
    println("in coroutine")
    return 6
}

//Continuation是协程结束后的回调
fun main() {
    val sus: suspend () -> Int = ::fun1

    sus.startCoroutine(object : Continuation<Int> {
        override val context: CoroutineContext = EmptyCoroutineContext
        override fun resumeWith(result: Result<Int>) {

            println("startCoroutine end: $result ")
        }
    })



    ::fun1.createCoroutine(object : Continuation<Int> {
        override val context: CoroutineContext = EmptyCoroutineContext
        override fun resumeWith(result: Result<Int>) {
            println("coroutine end $result")
        }
    }).resume(Unit)


    val continuation = suspend {
        println("in coroutine")
        5
    }.createCoroutine(object : Continuation<Int> {
        override val context: CoroutineContext = EmptyCoroutineContext
        override fun resumeWith(result: Result<Int>) {
            println("coroutine end $result")
        }
    })
    continuation.resume(Unit)

}

//回忆一下
suspend fun suspendFunc() = suspendCoroutine<Unit> {
        continuation ->
    thread {
        continuation.resume(Unit)
    }
}