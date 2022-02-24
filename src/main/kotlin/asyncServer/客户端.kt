package asyncServer

import kotlinx.coroutines.runBlocking
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.Socket
import java.util.*
import kotlin.concurrent.thread

class MyClint(val port: Int) {
    fun start() = runBlocking {
        val clientSocket = Socket("127.0.0.1", port)


        val scanner = Scanner(System.`in`)

        println("已与服务器建立连接")
        val outputStream = clientSocket.getOutputStream()
        val bufferedReader = BufferedReader(InputStreamReader(clientSocket.getInputStream()))

        thread {        //readLine接收到的没有换行符\n
            while(true){
                val message = bufferedReader.readLine()
                if(message!=null){
                    println("来自服务端消息：${message}")
                    if (message == "bye, too") {
                        clientSocket.close()
                        println("连接已断开")
                    }
                }
            }
        }
        println("获取输出流成功")
        while (scanner.hasNext()) {
            val bufferedWriter = BufferedWriter(OutputStreamWriter(outputStream))
            val nextLine = scanner.nextLine()
            bufferedWriter.write(nextLine + "\n")
            bufferedWriter.flush()
            println("发送了")
        }
    }
}

fun main() {
    MyClint(3000).start()
}