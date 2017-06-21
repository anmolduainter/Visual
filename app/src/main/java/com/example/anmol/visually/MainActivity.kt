package com.example.anmol.visually

import android.Manifest
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.net.Uri
import android.speech.RecognizerIntent
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast

import java.util.ArrayList
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private val REQ_CODE_SPEECH_INPUT = 100

    var text:String = ""

    var tx:TextView = null!!

    var phone:String=""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        tx= findViewById(R.id.textView) as TextView
        val btn = findViewById(R.id.button) as Button

        btn.setOnClickListener { promptSpeechInput() }
    }

    fun promptSpeechInput() {

        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT)
        } catch (a: ActivityNotFoundException) {
            Toast.makeText(applicationContext,
                    "Device not supported",
                    Toast.LENGTH_SHORT).show()
        }

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            REQ_CODE_SPEECH_INPUT -> {
                if (resultCode == Activity.RESULT_OK && null != data) {

                    val result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)

                    val text1:String=result[0]

                    tx.text = result[0]
                    if (text1.contains("call")) {
                        text = result[0].replace("call", "").trim({ it <= ' ' })
                        if (text.length == 10) {
                            call()
                        } else {
                            val resId = resources.getIdentifier("beep", "raw", packageName)
                            val mp = MediaPlayer.create(applicationContext, resId)
                            mp.start()
                        }
                    } else if (result[0].contains("sms")) {

                        var str:String=result[0].replace("sms","")

                        if(str.contains("to")){
                             phone=str.replace("to","")
                        }

                        else if (str.contains("body")){

                           var message:String=str.replace("body","")

                            sms(message)

                        }


                    }
                }
            }
        }
    }


    fun sms(message:String){

        val intent=Intent(Intent.ACTION_VIEW,Uri.parse("sms"+phone))
        intent.putExtra("sms_body",message)
        startActivity(intent)

    }


    fun call() {


        val uri = "tel:" + text
        val intent = Intent(Intent.ACTION_CALL)
        intent.data = Uri.parse(uri)
        if (ActivityCompat.checkSelfPermission(this@MainActivity, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            return
        }
        startActivity(intent)

    }

}
