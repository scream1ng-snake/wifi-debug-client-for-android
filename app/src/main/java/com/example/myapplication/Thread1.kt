package com.example.myapplication

import android.os.Handler
import android.util.Log
import android.widget.TextView
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.Socket


class Thread1(private var handler: Handler, private  var tv_result: TextView): Runnable {
    override fun run() {
        try {
            MyData.socket = Socket(MyData.IP_ADRESS, MyData.PORT)
            MyData.input = BufferedReader(InputStreamReader(MyData.socket.getInputStream()))
            MyData.output = PrintWriter(MyData.socket.getOutputStream())
            MyData.isConnected = true
            Log.d("DEBUG", "Socket connected to ${MyData.IP_ADRESS} ")
            handler.post(Runnable {
                tv_result.append("Socket connected to ${MyData.IP_ADRESS} \n")
            })

            Thread(Thread2(handler, tv_result)).start()
        }catch (e: IOException) {
            Log.d("DEBUG", "Error $e\n")
            MyData.isConnected = false
            handler.post(Runnable {
                tv_result.append("Error: ${e} \n")
            })

        }
    }
}