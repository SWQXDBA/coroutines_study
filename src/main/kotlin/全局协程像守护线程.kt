import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

fun main() {
    runBlocking{
        func()
    }
}
suspend fun func() {
    //在 GlobalScope 中启动的活动协程并不会使进程保活。它们就像守护线程
    GlobalScope.launch {
        repeat(1000) { i ->
            println("I'm sleeping $i ...")
            delay(500L)
        }
    }
    delay(1300L) // 在延迟后退出
}