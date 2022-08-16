package com.example.myapplication

import java.io.BufferedReader
import java.io.PrintWriter
import java.net.Socket

object MyData {
    lateinit var socket: Socket
    lateinit var input: BufferedReader
    lateinit var output: PrintWriter
    var IP_ADRESS: String = ""
    var PORT: Int = 0
    var isConnected: Boolean = false
}