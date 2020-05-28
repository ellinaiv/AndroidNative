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
import com.example.team11.Color
import com.example.team11.database.entity.Place
import com.example.team11.R
import com.example.team11.database.entity.WeatherForecastDb
import com.example.team11.ui.place.PlaceActivity
import com.example.team11.ui.favorites.FavoritesFragmentViewModel

/**
 * List adapter viser informasjon på de forskjellige cardsViews.
 * @param myDataset er arraylist med badeplasser
 * @param forecasts er listen med vearmeldingen for alle stedene
 * @param context er contexten til activity der cardViews skal visses
 * @param listViewModel er viewModelen til det fragmentet som eier adapteren
 */

class ListAdapter(private val places: List<Place>,
                  private val forecasts: List<WeatherForecastDb>,
                  val context: Context,
                  private val listViewModel: ListViewModel) : RecyclerView.Adapter<ListAdapter.MyViewHolder>() {

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

    override fun onBindViewHolder(holder: MyViewHolder, position: Int){
        holder.itemName.text = places[position].name
        val forecastList = forecasts.filter { it.placeId == places[position].id}
        if(forecastList.isNotEmpty()) {
            val forecast = forecastList[0]
            holder.itemTempAir.text =
                context.getString(R.string.tempC, forecast.tempAir)
            holder.imageTempAir.setImageDrawable(
                context.getDrawable(
                    context.resources.getIdentifier(
                        forecast.symbol, "drawable", this.context.packageName)))
        }

        when(listViewModel.colorWave(places[position])){
            Color.GRAY -> holder.imageWater.setImageDrawable(context.getDrawable(R.drawable.ic_nodatawave))
            Color.RED -> {
                holder.imageWater.setImageDrawable(context.getDrawable(R.drawable.water_red))
                holder.itemTempWater.text = context.getString(R.string.tempC, places[position].tempWater)
            }
            Color.BLUE -> {
                holder.imageWater.setImageDrawable(context.getDrawable(R.drawable.water_blue))
                holder.itemTempWater.text = context.getString(R.string.tempC, places[position].tempWater)
            }
        }


       holder.itemView.setOnClickListener{
           listViewModel.changeCurrentPlace(places[position])
           val intent = Intent(context, PlaceActivity::class.java)
           context.startActivity(intent)
        }

    }
    override fun getItemCount() = places.size
}
