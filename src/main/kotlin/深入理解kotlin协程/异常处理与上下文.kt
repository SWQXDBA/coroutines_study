package 深入理解kotlin协程

import java.util.Comparator
import kotlin.coroutines.*

//一个context 代表异常处理器
class ExceptionInterceptor(val handler:(Throwable)->Unit): AbstractCoroutineContextElement(Key) {
    companion object Key :CoroutineContext.Key<ExceptionInterceptor>
    fun onError(err:Throwable){
        handler(err)
    }

}
//一个Continuation 代表协程结果的回调，在这里检查与处理异常
class ExceptionContinuation(override val context : CoroutineContext): Continuation<Any>{

    override fun resumeWith(result: Result<Any>) {
        result.onFailure {
            context[ExceptionInterceptor]?.onError(it)
        }

    }
}
fun startExceptionCoroutine(handler: (Throwable) -> Unit, block:suspend()->Unit){
    block.startCoroutine(ExceptionContinuation(ExceptionInterceptor(handler)))
}

fun main() {
    startExceptionCoroutine(handler = fun(t:Throwable){
        println("err: $t")
    }){

        println("ok")
        val k = 1/0;
        println("出错了!")
    }
}