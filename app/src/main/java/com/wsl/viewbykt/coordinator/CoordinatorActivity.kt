package com.wsl.viewbykt.coordinator

import android.os.Build.VERSION
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.wsl.viewbykt.R
import kotlinx.android.synthetic.main.activity_coordinator.*
import java.util.*

class CoordinatorActivity : AppCompatActivity() {

    private lateinit var textToSpeech: TextToSpeech

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_coordinator)
        recyclerView.layoutManager = LinearLayoutManager(applicationContext)
        recyclerView.adapter = MyAdapter()
        Log.e("TAG", "====语音初始化结果==")
        textToSpeech = TextToSpeech(this, { var1 ->
            var var1 = var1
            val var2 = StringBuilder()
            var2.append("语音初始化结果=====")
            var2.append(var1)
            val var4 = System.err
            val var3 = StringBuilder()
            var3.append("SystemTTS====onInit====")
            var3.append(var1)
            var4.println(var3.toString())
            if (var1 == 0) {
                var1 = this@CoordinatorActivity.textToSpeech.setLanguage(Locale.CHINA)
                if (VERSION.SDK_INT > 19) {
//                    textToSpeech.setOnUtteranceProgressListener(this@SystemTTS)
                } else {
//                    textToSpeech.setOnUtteranceCompletedListener(this@SystemTTS)
                }
                if (var1 == -1 || var1 == -2) {
//                    this@SystemTTS.isSuccess = false
                }
            }
        }, "com.iflytek.vflynote")

        view.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                val var3 = Bundle()
                var3.putFloat("volume", 1.0f)
                var var1 = "主演：欧阳震华 黄宗泽 苗侨伟 宣萱 abc"
                textToSpeech.speak(var1, 1, var3, var1)
            }
        })
    }

    inner class MyAdapter : RecyclerView.Adapter<MyHolder>(){
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHolder {
            return MyHolder(LayoutInflater.from(applicationContext).inflate(R.layout.adapter_item,null))
        }

        override fun getItemCount(): Int {
            return 20
        }

        override fun onBindViewHolder(holder: MyHolder, position: Int) {
            holder.tv.text = "Item = " +position
        }
    }

    inner class MyHolder(view: View) : RecyclerView.ViewHolder(view){
        val tv: TextView = view.findViewById(R.id.txt_tv)
    }
}