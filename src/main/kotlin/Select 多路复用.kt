import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.produce
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.selects.select


@ExperimentalCoroutinesApi
fun main()= runBlocking {
    val channel1 = Channel<Int>()
    val channel2 = Channel<Int>()
    val channel3 = produce<Int> {
        repeat(5){
            i->
            send(i*6)
        }
    }


    launch {
        repeat(5){
            i->
            channel1.send(i)
        }
    }
    launch {
        repeat(10){
                i->
            channel2.send(i+5)
        }
    }
    while (true){
        val select = select<Int> {
            channel1.onReceive {

                return@onReceive it
            }
            channel2.onReceive {

                return@onReceive it
            }

            channel3.onReceive {

                return@onReceive it
            }
        }
        println(select)
    }


}