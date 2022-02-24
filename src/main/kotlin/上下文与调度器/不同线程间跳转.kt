package 上下文与调度器
import com.sun.activation.registries.LogSupport.log
import kotlinx.coroutines.*

fun main(){
    newSingleThreadContext("Ctx1").use { ctx1 ->
        newSingleThreadContext("Ctx2").use { ctx2 ->
            runBlocking(ctx1) {
                println("Started in ctx1   ${Thread.currentThread().name}")

                withContext(ctx2) {
                    println("Working in ctx2  ${Thread.currentThread().name}")
                }
                println("Back to ctx1  ${Thread.currentThread().name}")

            }
        }
    }
}

