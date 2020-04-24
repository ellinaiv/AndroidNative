package com.example.team11.userInterface

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.RecyclerView
import com.example.team11.Place
import com.example.team11.R
import com.example.team11.Repository.PlaceRepository
import com.example.team11.viewmodels.FavoritePlacesActivityViewModel
import com.example.team11.viewmodels.PlacesListActivityViewModel

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

    // TODO("Celsius burde komme fra string resource ")
    override fun onBindViewHolder(holder: MyViewHolder, position: Int){
        holder.itemName.text = myDataset[position].name
        holder.itemTempWater.text = myDataset[position].temp.toString() + "°C"
        holder.itemTempAir.text = "no data"
        holder.itemTempAir.text = PlaceRepository.getInstance().getWeather(myDataset[position])


       holder.itemView.setOnClickListener{
           if(favorite){
               val favoritePlacesActivityViewModel = viewModel as FavoritePlacesActivityViewModel
               favoritePlacesActivityViewModel.changeCurrentPlace(myDataset[position])
           }else{
               val placesListActivityViewModel = viewModel as PlacesListActivityViewModel
               placesListActivityViewModel.changeCurrentPlace(myDataset[position])
           }
            val intent = Intent(context, PlaceActivity::class.java)
            context.startActivity(intent)
        }

    }
    override fun getItemCount() = myDataset.size
}
