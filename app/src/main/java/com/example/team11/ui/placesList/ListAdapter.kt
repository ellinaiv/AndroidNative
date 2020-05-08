package com.example.team11.ui.placesList

import android.content.Context
import android.content.Intent
import android.media.Image
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.RecyclerView
import com.example.team11.Place
import com.example.team11.R
import com.example.team11.ui.place.PlaceActivity
import com.example.team11.ui.favorites.FavoritesFragmentViewModel

/*
 * List adapter viser informasjon på de forskjellige cardsViews.
 * @param myDataset er arraylist med badeplasser
 * @param context er kotexten til activity der cardViews skal visses
 */

class ListAdapter(private val myDataSet: List<Place>, val context: Context,
                  private val viewModel: ViewModel, private val favorite: Boolean) : RecyclerView.Adapter<ListAdapter.MyViewHolder>() {

    class MyViewHolder(itemView: CardView) : RecyclerView.ViewHolder(itemView){

        val itemName: TextView = itemView.findViewById(R.id.textName)
        val itemTempAir: TextView = itemView.findViewById(R.id.textTempAir)
        val itemTempWater: TextView = itemView.findViewById(R.id.textTempWater)
        val imageWater: ImageView = itemView.findViewById(R.id.imageWater)

    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): MyViewHolder {
        val v = LayoutInflater.from(viewGroup.context).inflate(R.layout.cardview, viewGroup, false) as CardView
        return MyViewHolder(v)
    }


    // TODO("Celsius burde komme fra string resource ")
    override fun onBindViewHolder(holder: MyViewHolder, position: Int){
        holder.itemName.text = myDataSet[position].name
        holder.itemTempWater.text = context.getString(R.string.tempC, myDataSet[position].tempWater)
        holder.itemTempAir.text = context.getString(R.string.tempC, myDataSet[position].tempAir)

        if(favorite){
            val favoritePlacesViewModel = viewModel as FavoritesFragmentViewModel
            when(favoritePlacesViewModel.redWave(myDataSet[position])){
                true -> holder.imageWater.setImageDrawable(context.getDrawable(R.drawable.water_red))
                false ->holder.imageWater.setImageDrawable(context.getDrawable(R.drawable.water_blue))
            }

        }else{
            val placesListActivityViewModel = viewModel as PlacesListFragmentViewModel
            when(placesListActivityViewModel.redWave(myDataSet[position])){
                true -> holder.imageWater.setImageDrawable(context.getDrawable(R.drawable.water_red))
                false ->holder.imageWater.setImageDrawable(context.getDrawable(R.drawable.water_blue))
            }
        }


       holder.itemView.setOnClickListener{
           if(favorite){
               val favoritePlacesViewModel = viewModel as FavoritesFragmentViewModel
               favoritePlacesViewModel.changeCurrentPlace(myDataSet[position])
           }else{
               val placesListActivityViewModel = viewModel as PlacesListFragmentViewModel
               placesListActivityViewModel.changeCurrentPlace(myDataSet[position])
           }
           val intent = Intent(context, PlaceActivity::class.java)
           context.startActivity(intent)
        }

    }
    override fun getItemCount() = myDataSet.size
}
