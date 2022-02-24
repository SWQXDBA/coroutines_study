package asyncServer

import kotlinx.coroutines.runBlocking
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.Socket
import java.util.*

class MyClint(val port: Int) {
    fun start() = runBlocking {
        val clientSocket = Socket("127.0.0.1", port)


        val scanner = Scanner(System.`in`)

        println("已与服务器建立连接")
        val outputStream = clientSocket.getOutputStream()

        println("获取输出流成功")
        while (scanner.hasNext()) {
            val bufferedWriter = BufferedWriter(OutputStreamWriter(outputStream))
            val nextLine = scanner.nextLine()
            bufferedWriter.write(nextLine + "\n")
            bufferedWriter.flush()

            println("发送了")

/*
            val bufferedReader = BufferedReader(InputStreamReader(clientSocket.getInputStream()))


            //readLine接收到的没有换行符\n
            val message = bufferedReader.readLine() ?: break

            println("来自服务端消息：${message}")
            if(message=="bye, too"){
                clientSocket.close()
                println("连接已断开")
                break
            }*/
        }
    }
}

fun main() {
    MyClint(3000).start()
}