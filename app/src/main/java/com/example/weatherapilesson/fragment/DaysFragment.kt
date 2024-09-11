package com.example.weatherapilesson.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.weatherapilesson.DataModel
import com.example.weatherapilesson.RecyclerViewAdapter
import com.example.weatherapilesson.WeatherModel
import com.example.weatherapilesson.databinding.FragmentDaysBinding


class DaysFragment : Fragment(), RecyclerViewAdapter.Listener {
    private lateinit var binding: FragmentDaysBinding
    private val model: DataModel by activityViewModels()
    private lateinit var adapter: RecyclerViewAdapter
    var currentPosition = 0


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDaysBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRcView()
    }

    fun initRcView() = with(binding) {
        rcView.layoutManager = LinearLayoutManager(activity)
        adapter = RecyclerViewAdapter(this@DaysFragment)
        rcView.adapter = adapter
        model.liveDataList.observe(viewLifecycleOwner) {
            adapter.submitList(it.subList(1,it.size))
        }
    }



 /*  private fun getDaysList(array: ArrayList<WeatherModel>): ArrayList<WeatherModel> {
        val list = ArrayList<WeatherModel>()
       val dayArray = JSONArray(array)
            for (i in 0 until dayArray.length()) {
                val day = dayArray[i] as JSONObject
                val weatherModel = WeatherModel(
                    "", "",
                    //  day.getString("date"),
                    day.getJSONObject("day").getJSONObject("condition").getString("text"),
                    "",
                    day.getJSONObject("day").getString("mintemp_c"),
                    day.getJSONObject("day").getString("maxtemp_c"),
                    day.getJSONObject("condition").getString("icon"),
                    ""

                )
                list.add(weatherModel)
            }

        return list*/



    companion object {
        @JvmStatic
        fun newInstance() =
            DaysFragment()
    }

    override fun onClick(item: WeatherModel) {
            model.liveDataCurrent.value = item
    }
}