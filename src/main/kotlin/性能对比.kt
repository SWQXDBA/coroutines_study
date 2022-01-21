import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.lang.Thread.sleep
import kotlin.concurrent.thread

fun main()  {
    useThread()

}

fun useCoroutine() = runBlocking {
    repeat(100_000) { // 启动大量的协程
        launch {
            delay(5000L)
            print(".")
        }
    }
}

fun useThread() = runBlocking {
    repeat(100_000) { // 启动大量的协程
        thread {
            sleep(5000L)
            print(".")
        }
    }
}
