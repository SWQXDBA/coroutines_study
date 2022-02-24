package 上下文与调度器

import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

fun main():Unit = runBlocking {

    launch (Dispatchers.Default+CoroutineName("这个携程的名字")){

        println(  this.coroutineContext[CoroutineName])
        println("I'm working in thread ${Thread.currentThread().name},${this.coroutineContext}")

    }
}

