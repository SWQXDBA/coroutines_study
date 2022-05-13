package 深入理解kotlin协程

import kotlinx.coroutines.delay
import kotlin.coroutines.Continuation
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.coroutines.startCoroutine

class ProducerScope<T> {
    suspend fun produce(value: T) {
        println("produce $value")
    }
}

fun <R, T> launchCoroutine(receiver: R, block: suspend R.() -> T) {
    block.startCoroutine(receiver, object : Continuation<T> {
        override val context: CoroutineContext = EmptyCoroutineContext
        override fun resumeWith(result: Result<T>) {
            println("coroutine end $result")
        }
    })
}

fun main() {

    launchCoroutine(ProducerScope<Int>()) {
        println("in coroutine 1")
        produce(666)
        delay(1000)
        produce(777)
        777
    }
    launchCoroutine(ProducerScope<Int>()) {
        println("in coroutine 2")
        produce(888)
        delay(1000)
        produce(999)
        999
    }
    Thread.sleep(10000)
}

