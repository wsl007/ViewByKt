package com.wsl.viewbykt.coordinator

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.wsl.viewbykt.R
import kotlinx.android.synthetic.main.activity_coordinator.*

class CoordinatorActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_coordinator)
        recyclerView.layoutManager = LinearLayoutManager(applicationContext)
        recyclerView.adapter = MyAdapter()
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