package com.example.happyplaces.activities

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.happyplaces.database.DatabaseHandler
import com.example.happyplaces.databinding.ActivityMainBinding
import com.example.happyplaces.models.HappyPlaceModel
import com.happyplaces.adapters.HappyPlacesAdapter
import com.happyplaces.utils.SwipeToDeleteCallback
import pl.kitek.rvswipetodelete.SwipeToEditCallback

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.fabHappyPlace.setOnClickListener{
            startActivityForResult(Intent(this, AddHappyPlaceActivity::class.java), ADD_PLACE_ACTIVITY_REQUEST_CODE)
        }

        getHappyPlacesListFromLocalDB()
    }

    private fun setupHappyPlacesRecyclerView(happyPlaceList: ArrayList<HappyPlaceModel>){
        binding.rvHappyPlacesList.layoutManager = LinearLayoutManager(this)

        val placesAdapter = HappyPlacesAdapter(this, happyPlaceList)
        binding.rvHappyPlacesList.adapter = placesAdapter

        placesAdapter.setOnClickListener(object: HappyPlacesAdapter.OnClickListener{
            override fun onClick(position: Int, model: HappyPlaceModel) {
                val intent = Intent(this@MainActivity, HappyPlaceDetailsActivity::class.java)
                intent.putExtra(EXTRA_PLACE_DETAILS, model)
                startActivity(intent)
            }
        })

        val editSwipeHandler = object: SwipeToEditCallback(this){
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val adapter = binding.rvHappyPlacesList.adapter as HappyPlacesAdapter
                adapter.notifyEditItem(this@MainActivity, viewHolder.position, ADD_PLACE_ACTIVITY_REQUEST_CODE)
            }
        }

        val editItemTouchHelper = ItemTouchHelper(editSwipeHandler)
        editItemTouchHelper.attachToRecyclerView(binding.rvHappyPlacesList)


        val deleteSwipeHandler = object: SwipeToDeleteCallback(this){
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val adapter = binding.rvHappyPlacesList.adapter as HappyPlacesAdapter
                adapter.removeAt(viewHolder.position)

                getHappyPlacesListFromLocalDB()
            }
        }

        val deleteItemTouchHelper = ItemTouchHelper(deleteSwipeHandler)
        deleteItemTouchHelper.attachToRecyclerView(binding.rvHappyPlacesList)
    }

    private fun getHappyPlacesListFromLocalDB(){
        val dbHandler = DatabaseHandler(this)
        val getHappyPlaceList = dbHandler.getHappyPlaceList()

        if(getHappyPlaceList.size > 0){
            binding.rvHappyPlacesList.visibility = View.VISIBLE
            binding.tvNoRecordsAvailable.visibility = View.INVISIBLE
            setupHappyPlacesRecyclerView(getHappyPlaceList)
        }
        else{
            binding.rvHappyPlacesList.visibility = View.INVISIBLE
            binding.tvNoRecordsAvailable.visibility = View.VISIBLE
        }
    }

    companion object{
        val ADD_PLACE_ACTIVITY_REQUEST_CODE = 1
        val EXTRA_PLACE_DETAILS = "extra_place_details"
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == ADD_PLACE_ACTIVITY_REQUEST_CODE){
            if(resultCode == Activity.RESULT_OK){
                getHappyPlacesListFromLocalDB()
            }
        }
    }
}