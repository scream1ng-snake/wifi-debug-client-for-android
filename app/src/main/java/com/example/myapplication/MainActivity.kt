package com.example.myapplication

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.LinkProperties
import android.net.NetworkCapabilities
import android.net.wifi.WifiInfo
import android.net.wifi.WifiManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import android.text.format.Formatter
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import java.net.SocketException

class MainActivity : AppCompatActivity() {
    private val wifiManager: WifiManager get() = this.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
    private lateinit var user_field: EditText
    private lateinit var send_btn: Button
    private lateinit var connect_btn: Button
    private lateinit var close_btn: Button
    private lateinit var result_info: TextView
    private lateinit var port_field: EditText
    private lateinit var ip_field: EditText

    private lateinit var menuItem: MenuItem
    private lateinit var menuItem2: MenuItem

    private lateinit var handler: Handler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        user_field = findViewById(R.id.user_field)
        send_btn = findViewById(R.id.send_btn)
        result_info = findViewById(R.id.result_info)
        connect_btn = findViewById(R.id.connect_btn)
        close_btn = findViewById(R.id.close_btn)
        ip_field = findViewById(R.id.ip_field)
        port_field = findViewById(R.id.port_field)

        handler = Handler()


        connect_btn.setOnClickListener(View.OnClickListener() {
            if (ip_field.getText().toString().trim() == "" || port_field.getText().trim() == "") {
                Toast.makeText(this@MainActivity, "Введите порт и IP адрес", Toast.LENGTH_LONG).show()
            } else {
                MyData.IP_ADRESS = ip_field.getText().toString().trim()
                MyData.PORT = port_field.text.toString().toInt()
                Thread(Thread1(handler, result_info)).start()
                // result_info.append(MyData.message)
            }
        })

        send_btn.setOnClickListener(View.OnClickListener() {
            if (user_field.getText().toString().trim() == "") {
                Toast.makeText(this@MainActivity, R.string.no_user_input, Toast.LENGTH_LONG).show()
            } else {
                var field_text = user_field.getText().toString().trim()
                user_field.setText("")
                Thread(Thread3(field_text, handler, result_info)).start()
            }
        })

        close_btn.setOnClickListener(View.OnClickListener() {
            MyData.socket.close()
            result_info.append("Socket connection was closed \n")
        })
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)

        if (menu != null) {

            menuItem = menu.findItem(R.id.id_wifi_button)
            menuItem2 = menu.findItem(R.id.id_ip_button)

        }

        setWiFiIcon()

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == R.id.id_wifi_button) {
            if(!wifiManager.isWifiEnabled) {
                enableWiFi()
            } else {
                disableWiFi()
            }
        } else if (item.itemId == R.id.id_ip_button) {
            MyData.IP_ADRESS = getIPAddress()
            ip_field.setText(MyData.IP_ADRESS)
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == 0) {
            MyData.IP_ADRESS = getIPAddress()
            setWiFiIcon()
        }
    }

    private fun getIPAddress(): String {
        var ip: String = ""
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val connectivityManager: ConnectivityManager = this.applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                val networkInfo = connectivityManager.activeNetwork
                val capabilities = connectivityManager.getNetworkCapabilities(networkInfo)

                var gateway: LinkProperties? = connectivityManager.getLinkProperties(ConnectivityManager.TYPE_WIFI)

                if (capabilities?.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) == true) {
                    val wifiInfo = capabilities.transportInfo as WifiInfo
                    ip = wifiInfo.ipAddress.toString()
                    Log.d("DEBUG", "ssid ${ip}")
                }
            } else {
                val connectionInfo = wifiManager.connectionInfo
                ip = Formatter.formatIpAddress(connectionInfo.ipAddress)
                result_info.append("Connected IP is = $ip\n")
                Log.d("DEBUG", "ssid (deprecated) ${ip}")
            }
        } catch (ex: SocketException) {}
        return ip
    }

    private fun setWiFiIcon() {
        if (wifiManager.isWifiEnabled()) {
            menuItem.setIcon(R.drawable.ic_wifi_disable)
        } else {
            menuItem.setIcon(R.drawable.ic_wifi_enable)
        }
    }

    private fun enableWiFi() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val panelIntent = Intent(Settings.Panel.ACTION_WIFI)
            startActivityForResult(panelIntent, 0)
        } else {
            wifiManager.setWifiEnabled(true)
        }
        Toast.makeText(this@MainActivity, R.string.wifi_ena, Toast.LENGTH_LONG).show()


    }

    private fun disableWiFi() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val panelIntent = Intent(Settings.Panel.ACTION_WIFI)
            startActivityForResult(panelIntent, 0)
        } else {
            wifiManager.disconnect()
            wifiManager.setWifiEnabled(false)
        }
        Toast.makeText(this@MainActivity, R.string.wifi_dis, Toast.LENGTH_LONG).show()

    }

}