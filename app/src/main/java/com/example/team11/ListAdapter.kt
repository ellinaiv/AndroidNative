package com.example.team11

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView

/*
 * List adapter viser informasjon på de forskjellige cardsViews.
 * @param myDataset er arraylist med badeplasser
 * @param context er kotexten til activity der cardViews skal visses
 */

class ListAdapter(private val myDataset: ArrayList<Place>, val context: Context) : RecyclerView.Adapter<ListAdapter.MyViewHolder>() {

    class MyViewHolder(itemView: CardView) : RecyclerView.ViewHolder(itemView){

        var itemName: TextView
        var itemTempAir: TextView
        var itemTempWater: TextView


        init {
            itemName = itemView.findViewById(R.id.name)
            itemTempAir = itemView.findViewById(R.id.tempAir)
            itemTempWater = itemView.findViewById(R.id.tempWater)
        }

    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): MyViewHolder {
        val v = LayoutInflater.from(viewGroup.context).inflate(R.layout.cardview, viewGroup, false) as CardView
        return MyViewHolder(v)
    }


    /*
     * Vi har for lite data nå. Burde se på APIer og ta ut mer info om badeplassene
     */
    override fun onBindViewHolder(holder: MyViewHolder, position: Int){
        holder.itemName.text = myDataset[position].name
        holder.itemTempWater.text = myDataset[position].temp.toString() + "°C"
        holder.itemTempAir.text = "no data"

    }
    override fun getItemCount() = myDataset.size
}
