package com.example.myapplication

import android.os.Handler
import android.util.Log
import android.widget.TextView
import java.io.IOException

class Thread3(private var msg: String, private var handler: Handler, private var tv_result: TextView): Runnable {
    override fun run() {
        try {
            if (MyData.isConnected) {
                MyData.output.write(msg)
                MyData.output.flush()
                Log.d("DEBUG", "Message has been sent: $msg\n")
                handler.post(Runnable {
                    tv_result.append("Message has been sent: $msg\n")
                })
            }
        } catch (e: IOException) {
            Log.d("DEBUG", "Error $e\n")
            handler.post(Runnable {
                tv_result.append("Error: $e\n")
            })
        }
    }
}