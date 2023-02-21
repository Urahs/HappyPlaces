package com.happyplaces.adapters

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.happyplaces.activities.AddHappyPlaceActivity
import com.example.happyplaces.activities.MainActivity
import com.example.happyplaces.database.DatabaseHandler
import com.example.happyplaces.databinding.ItemHappyPlaceBinding
import com.example.happyplaces.models.HappyPlaceModel


open class HappyPlacesAdapter(private var context: Context, private var list: ArrayList<HappyPlaceModel>) : RecyclerView.Adapter<HappyPlacesAdapter.ViewHolder>() {

    private var onClickListener: OnClickListener? = null

    fun setOnClickListener(onClickListener: OnClickListener){
        this.onClickListener = onClickListener
    }

    class ViewHolder(binding: ItemHappyPlaceBinding): RecyclerView.ViewHolder(binding.root){
        val tvTitle = binding.tvTitle
        val ivPlaceImage = binding.ivPlaceImage
        val tvDescription = binding.tvDescription
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemHappyPlaceBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val model = list[position]

        holder.ivPlaceImage.setImageURI(Uri.parse(model.image))
        holder.tvTitle.text = model.title
        holder.tvDescription.text = model.description

        holder.itemView.setOnClickListener{
            onClickListener!!.onClick(position, model)
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun removeAt(position: Int){
        val dbHandler = DatabaseHandler(context)
        val isDeleted = dbHandler.deleteHappyPlace(list[position])
        if(isDeleted > 0){
            list.removeAt(position)
            notifyItemRemoved(position)
        }
    }

    fun notifyEditItem(activity: Activity, position: Int, requestCode: Int){
        val intent = Intent(context, AddHappyPlaceActivity::class.java)
        intent.putExtra(MainActivity.EXTRA_PLACE_DETAILS, list[position])
        activity.startActivityForResult(intent, requestCode)
        notifyItemChanged(position)
    }

    interface OnClickListener{
        fun onClick(position: Int, model: HappyPlaceModel)
    }
}