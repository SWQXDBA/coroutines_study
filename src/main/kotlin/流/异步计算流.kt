package 流

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.flow.flow

fun simple(): Flow<Int> = flow { // 流构建器
    for (i in 1..3) {
        delay(100) // 假装我们在这里做了一些有用的事情
        println(666)
        emit(i) // 发送下一个值
    }
}

@InternalCoroutinesApi
fun main(): Unit = runBlocking{
    // 启动并发的协程以验证主线程并未阻塞
    launch {
        for (k in 1..3) {
            println("I'm not blocked $k")
            delay(100)
        }
    }
    // 收集这个流
//    simple().collect { value -> println(value) }
    println(simple().toList())
}


