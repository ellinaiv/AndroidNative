package com.example.team11.ui.placesList

import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.provider.Settings.Global.getString
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.RecyclerView
import com.example.team11.database.entity.Place
import com.example.team11.R
import com.example.team11.database.entity.WeatherForecastDb
import com.example.team11.ui.place.PlaceActivity
import com.example.team11.ui.favorites.FavoritesFragmentViewModel

/*
 * List adapter viser informasjon på de forskjellige cardsViews.
 * @param myDataset er arraylist med badeplasser
 * @param context er kotexten til activity der cardViews skal visses
 */

class ListAdapter(private val places: List<Place>, private val forecasts: List<WeatherForecastDb>, val context: Context,
                  private val viewModel: ViewModel, private val favorite: Boolean) : RecyclerView.Adapter<ListAdapter.MyViewHolder>() {

    class MyViewHolder(itemView: CardView) : RecyclerView.ViewHolder(itemView){

        val itemName: TextView = itemView.findViewById(R.id.textName)
        val itemTempAir: TextView = itemView.findViewById(R.id.textTempAir)
        val itemTempWater: TextView = itemView.findViewById(R.id.textTempWater)
        val imageWater: ImageView = itemView.findViewById(R.id.imageWater)
        val imageTempAir: ImageView = itemView.findViewById(R.id.imageTempAir)

    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): MyViewHolder {
        val v = LayoutInflater.from(viewGroup.context).inflate(R.layout.cardview, viewGroup, false) as CardView
        return MyViewHolder(v)
    }


    // TODO("Celsius burde komme fra string resource ")
    override fun onBindViewHolder(holder: MyViewHolder, position: Int){
        holder.itemName.text = places[position].name
        Log.d("tagStørrelse", places.size.toString())
        Log.d("tagStørrelse", forecasts.size.toString())
        if(places.size == forecasts.size) {
            if(places[position].tempWater != Int.MAX_VALUE){
                holder.itemTempWater.text = context.getString(R.string.tempC, places[position].tempWater)
                Log.d("tagTemp", places[position].id.toString())
                Log.d("tagTemp", forecasts[position].placeId.toString())
            }
            if(forecasts[position].tempAir != Int.MAX_VALUE) {
                holder.itemTempAir.text =
                    context.getString(R.string.tempC, forecasts[position].tempAir)
                holder.imageTempAir.setImageDrawable(
                    context.getDrawable(
                        context.resources.getIdentifier(
                            forecasts[position].symbol, "drawable", this.context.packageName)))
            }
        }

        //TODO("Må disse være ulike egentlig?")
        if(favorite && places[position].tempWater != Int.MAX_VALUE){
            val favoritePlacesViewModel = viewModel as FavoritesFragmentViewModel
            when(favoritePlacesViewModel.redWave(places[position])){
                true -> holder.imageWater.setImageDrawable(context.getDrawable(R.drawable.water_red))
                false ->holder.imageWater.setImageDrawable(context.getDrawable(R.drawable.water_blue))
            }

        }else if(places[position].tempWater != Int.MAX_VALUE){
            val placesListActivityViewModel = viewModel as PlacesListFragmentViewModel
            when(placesListActivityViewModel.redWave(places[position])){
                true -> holder.imageWater.setImageDrawable(context.getDrawable(R.drawable.water_red))
                false ->holder.imageWater.setImageDrawable(context.getDrawable(R.drawable.water_blue))
            }
        }


       holder.itemView.setOnClickListener{
           if(favorite){
               val favoritePlacesViewModel = viewModel as FavoritesFragmentViewModel
               favoritePlacesViewModel.changeCurrentPlace(places[position])
           }else{
               val placesListActivityViewModel = viewModel as PlacesListFragmentViewModel
               placesListActivityViewModel.changeCurrentPlace(places[position])
           }
           val intent = Intent(context, PlaceActivity::class.java)
           context.startActivity(intent)
        }

    }
    override fun getItemCount() = places.size
}
