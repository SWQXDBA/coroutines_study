package 模拟实现async与await

import com.sun.xml.internal.ws.encoding.ContentTypeImpl
import kotlinx.coroutines.*
import java.lang.Thread.sleep
import kotlin.concurrent.thread
import kotlin.coroutines.Continuation
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.coroutines.startCoroutine
import kotlin.coroutines.suspendCoroutine


fun myAsync(context: EmptyCoroutineContext = EmptyCoroutineContext, block :suspend ()->Unit){
    block.startCoroutine(Continuation(context){})
}
suspend fun<T> myAwait(block : ()->T) = suspendCoroutine<T> {
    it.resumeWith(Result.success(block()))
}
fun main(){
    myAsync{
        var value = myAwait<Int> lab@{
            var value = 300
            thread {
                value = 2
            }
            sleep(200)
            return@lab value
        }
        println(value)
    }
}