import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import java.util.concurrent.Callable
import java.util.concurrent.Executors

fun main() {
    fun2()
}

fun fun1() = runBlocking {
    var currentTimeMillis = System.currentTimeMillis()

    val deferred1 = async {
        println("ok")
        delay(2000L)
        1
    }

    val deferred2 = async {
        println("ok")
        delay(2000L)
        1
    }
    println(deferred1.await() + deferred2.await())
    var currentTimeMillis2 = System.currentTimeMillis()
    println(currentTimeMillis2 - currentTimeMillis)
}

fun fun2() = runBlocking {

    var currentTimeMillis = System.currentTimeMillis()
    val newFixedThreadPool = Executors.newFixedThreadPool(2)
    val invokeAll = newFixedThreadPool.invokeAll(listOf(Callable {
        println("ok1")
        Thread.sleep(2000)
        1
    }, Callable {
        println("ok1")
        Thread.sleep(2000)
        1
    }))
    invokeAll.forEach { println(it.get()) }
    var currentTimeMillis2 = System.currentTimeMillis()
    println(currentTimeMillis2 - currentTimeMillis)


}


