import javafx.application.Application.launch
import kotlinx.coroutines.*
import java.lang.RuntimeException
import java.lang.Thread.sleep
import kotlin.coroutines.Continuation
import kotlin.coroutines.ContinuationInterceptor
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

object MyScope :CoroutineScope{
    override val coroutineContext: CoroutineContext = UseInterceptor
}
object UseInterceptor:ContinuationInterceptor {
    override val key: CoroutineContext.Key<UseInterceptor>
        get() = object : CoroutineContext.Key<UseInterceptor>{}

    override fun <T> interceptContinuation(continuation: Continuation<T>): Continuation<T> {
        println(Thread.currentThread().name+"")
        continuation.context.plus(this)
        println(this)
        val c2 = object :Continuation<T>{
            override val context: CoroutineContext
                get() = this@UseInterceptor

            override fun resumeWith(result: Result<T>) {
//                println("result")
                continuation.resumeWith(result)
            }
        }
        return c2
    }
}

fun main() {

    MyScope.launch {

       repeat(Int.MAX_VALUE){
           delay(1)
       }
    }
    sleep(3000)
}