package com.example.team11.ui.placesList

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.RecyclerView
import com.example.team11.Place
import com.example.team11.R
import com.example.team11.ui.place.PlaceActivity
import com.example.team11.viewmodels.FavoritesFragmentViewModel

/*
 * List adapter viser informasjon på de forskjellige cardsViews.
 * @param myDataset er arraylist med badeplasser
 * @param context er kotexten til activity der cardViews skal visses
 */

class ListAdapter(private val myDataset: List<Place>, val context: Context,
                  val viewModel: ViewModel, val favorite: Boolean) : RecyclerView.Adapter<ListAdapter.MyViewHolder>() {

    class MyViewHolder(itemView: CardView) : RecyclerView.ViewHolder(itemView){

        var itemName: TextView
        var itemTempAir: TextView
        var itemTempWater: TextView

        init {
            itemName = itemView.findViewById(R.id.textName)
            itemTempAir = itemView.findViewById(R.id.textTempAir)
            itemTempWater = itemView.findViewById(R.id.textTempWater)
        }

    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): MyViewHolder {
        val v = LayoutInflater.from(viewGroup.context).inflate(R.layout.cardview, viewGroup, false) as CardView
        return MyViewHolder(v)
    }


    // TODO("Celsius burde komme fra string resource ")
    override fun onBindViewHolder(holder: MyViewHolder, position: Int){
        holder.itemName.text = myDataset[position].name
        holder.itemTempWater.text = myDataset[position].tempWater.toString() + "°C"
        holder.itemTempAir.text = myDataset[position].tempAir.toString() + "°C"


       holder.itemView.setOnClickListener{
           if(favorite){
               val favoritePlacesViewModel = viewModel as FavoritesFragmentViewModel
               favoritePlacesViewModel.changeCurrentPlace(myDataset[position])
           }else{
               val placesListActivityViewModel = viewModel as PlacesListFragmentViewModel
               placesListActivityViewModel.changeCurrentPlace(myDataset[position])
           }

           Log.d("in holder", "come here when your click on cards")
           val intent = Intent(context, PlaceActivity::class.java)
           context.startActivity(intent)
        }

    }
    override fun getItemCount() = myDataset.size
}
