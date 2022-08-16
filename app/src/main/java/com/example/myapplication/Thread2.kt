package com.example.myapplication

import android.os.Handler
import android.util.Log
import android.widget.TextView
import java.io.IOException

class Thread2(private var handler: Handler, private var tv_result: TextView): Runnable {
    override fun run() {
        while (true) {
            try {
                val msg: String = MyData.input.readLine() // <- тут остановился
                if (msg.isNotEmpty()) {
                    Log.d("DEBUG", "have a message $msg\n")
                    handler.post(Runnable {
                        tv_result.append("have a message: $msg \n")
                    })
                } else {
                    handler.post(Runnable {
                        tv_result.append("connection failed: Retrying... \n")
                    })
                    MyData.socket.close()
                    Thread.sleep(500)
                    Thread(Thread1(handler, tv_result)).start()
                }
            }catch (e: IOException) {
                Log.d("DEBUG", "Error $e\n")
                handler.post(Runnable {
                    tv_result.append("Error: $e\n")
                })
            }
        }
    }
}