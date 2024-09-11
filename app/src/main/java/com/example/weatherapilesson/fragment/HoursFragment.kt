package com.example.weatherapilesson.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.weatherapilesson.DataModel
import com.example.weatherapilesson.RecyclerViewAdapter
import com.example.weatherapilesson.WeatherModel
import com.example.weatherapilesson.databinding.FragmentHoursBinding
import org.json.JSONArray
import org.json.JSONObject

class HoursFragment : Fragment(){
    private lateinit var binding: FragmentHoursBinding
    private lateinit var adapter: RecyclerViewAdapter
    private val model: DataModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHoursBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRcView()

    }

    fun initRcView() = with(binding) {
        rcView.layoutManager = LinearLayoutManager(activity)
        adapter = RecyclerViewAdapter(null)
        rcView.adapter = adapter
        model.liveDataCurrent.observe(viewLifecycleOwner) {
            adapter.submitList(
            getHoursList(it))
            }
        }



    private fun getHoursList(item: WeatherModel): ArrayList<WeatherModel> {
        val list = ArrayList<WeatherModel>()
        val hoursArray = JSONArray(item.hours)
        for (i in 0 until hoursArray.length()) {
            val hour = hoursArray[i] as JSONObject
            val weatherModel = WeatherModel(
                "",
                hour.getString("time"),
                hour.getJSONObject("condition").getString("text"),
                hour.getString("temp_c"),
                "", "",
                hour.getJSONObject("condition").getString("icon"),
                ""
            )
            list.add(weatherModel)
        }
        return list
    }

    companion object {
        @JvmStatic
        fun newInstance() = HoursFragment()
    }


}