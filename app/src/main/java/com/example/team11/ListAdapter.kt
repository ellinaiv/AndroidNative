package com.example.team11

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import java.lang.Exception



class ListAdapter(private val myDataset: MutableList<Place>, val context: Context) : RecyclerView.Adapter<ListAdapter.MyViewHolder>() {

    class MyViewHolder(itemView: CardView) : RecyclerView.ViewHolder(itemView){

        var itemName: TextView
        var itemAge: TextView
        var itemLocation: TextView
        var itemImg: ImageView


        init {
            itemName = itemView.findViewById(R.id.tempWater)
            itemAge = itemView.findViewById(R.id.tempAir)
            itemLocation = itemView.findViewById(R.id.tempWater)
            itemImg = itemView.findViewById(R.id.imgSun)
        }

    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): MyViewHolder {
        val v = LayoutInflater.from(viewGroup.context).inflate(R.layout.cardview, viewGroup, false) as CardView
        return MyViewHolder(v)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int){

        holder.itemName.text = myDataset[position].name
        holder.itemLocation.text = myDataset[position].temp.toString()
        holder.itemAge.text = myDataset[position].id.toString()
        /*try {
            GlideApp.with(context)
                .load(myDataset[position].imgSrc)
                .into(holder.itemImg)
        }catch(e : Exception){
            Log.d("error", e.message.toString())
        }*/
    }
    override fun getItemCount() = myDataset.size
}
