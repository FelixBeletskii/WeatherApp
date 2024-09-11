package com.example.weatherapilesson.fragment

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.fragment.app.activityViewModels
import com.airbnb.lottie.LottieAnimationView
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.weatherapilesson.DataModel
import com.example.weatherapilesson.DialogManger
import com.example.weatherapilesson.R
import com.example.weatherapilesson.ViewPagerAdapter
import com.example.weatherapilesson.WeatherModel
import com.example.weatherapilesson.databinding.FragmentMainBinding
import com.example.weatherapilesson.isPermissionGranted
import com.example.weatherapilesson.show
import com.example.weatherapilesson.showToTime
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.material.tabs.TabLayoutMediator
import com.squareup.picasso.Picasso
import org.json.JSONObject

const val API_KEY = "758cdee76fb245999d960954240509"

class MainFragment : Fragment() {
    private lateinit var fLocationClient: FusedLocationProviderClient
    val model: DataModel by activityViewModels()
    private lateinit var pLauncher: ActivityResultLauncher<String>
    private lateinit var binding: FragmentMainBinding
    val Fragments = listOf(HoursFragment.newInstance(), DaysFragment.newInstance())


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMainBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
        showLoading()


    }

    override fun onResume() {
        super.onResume()
        checkLocation()
        checkPermission()
    }

    private fun permissionListener() {
        pLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            Log.v("MyLog", "Permission is $it")
        }

    }

    private fun makeAlert() {
        DialogManger.locationsSettingDialog(requireContext(), object : DialogManger.Listener {
            override fun onClick(name: String?) {
                startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))

            }
        }
        )
    }

    private fun checkLocation() {
        if (isLocationEnabled()) {
            getLocation()
        } else {
            makeAlert()
        }
    }


    private fun checkPermission() {
        if (!isPermissionGranted(android.Manifest.permission.ACCESS_FINE_LOCATION)) {
            permissionListener()
            pLauncher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
        } else Log.v(
            "MyLog",
            "Permission if ${isPermissionGranted(android.Manifest.permission.ACCESS_FINE_LOCATION)}"
        )

    }

    private fun isLocationEnabled(): Boolean {
        val lm = activity?.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return lm.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }

    private fun getLocation() {
        val ct = CancellationTokenSource()
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        fLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, ct.token)
            .addOnCompleteListener {
                Log.v("MyLog", "Got location: $it")
                requestWeatherData(
                    "${it.result.latitude},${it.result.longitude}"
                )
            }

    }

    private fun init() = with(binding) {
        fLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
        val adapter = ViewPagerAdapter(activity as AppCompatActivity, Fragments)
        viewPager.adapter = adapter
        binding.ivSync.setOnClickListener {
            showLoading()
            checkLocation()
            binding.tab.selectTab(tab.getTabAt(0))
        }
        binding.ivSearch.setOnClickListener {
            DialogManger.searchByName(requireContext(), object : DialogManger.Listener {
                override fun onClick(name: String?) {
                    name?.let { it1 -> requestWeatherData(it1) }
                    showLoading()
                }
            }
            )
        }
        TabLayoutMediator(tab, viewPager) { tab, pos ->
            when (pos) {
                0 -> {
                    tab.text = "Hours"
                    tab.setIcon(R.drawable.ic_watch)
                }

                1 -> {
                    tab.text = "Days"
                    tab.setIcon(R.drawable.ic_calendar)


                }
            }
        }.attach()


    }

    private fun updateCurrentCard() = with(binding) {
        model.liveDataCurrent.observe(viewLifecycleOwner) {
            val maxMinTemp = "${it.maxTemp}Cº/${it.minTemp}"
            tvData.text = it.time
            tvCity.text = it.city
            tvMaxMin.text = if (it.currentTemp.isEmpty()) ""
            else {
                maxMinTemp + "ºC"
            }
            tvCurrentTemp.text = it.currentTemp.ifEmpty {
                maxMinTemp
            } + "ºC"


            tvCondition.text = it.condition
            Picasso.get().load("https:" + it.imageUrl).into(imWeather)
        }
    }

    private fun requestWeatherData(city: String) {
        val url = "https://api.weatherapi.com/v1/forecast.json?key=" +
                API_KEY +
                "&q=" +
                city +
                "&days=" +
                "10" +
                "&aqi=no&alerts=no"
        val queue = Volley.newRequestQueue(context)
        val request = StringRequest(
            Request.Method.GET,
            url,
            { result ->
                parseWeatherData(result)
            },
            { error ->
                Toast.makeText(requireContext(), "City not found!", Toast.LENGTH_SHORT).show()
            }
        )
        queue.add(request)
    }

    private fun parseWeatherData(result: String) {
        val mainObject = JSONObject(result)
        val list = parseDays(mainObject)
        parseCurrentData(mainObject, list[0])
        updateCurrentCard()
    }

    private fun parseDays(mainObject: JSONObject): List<WeatherModel> {
        val list = ArrayList<WeatherModel>()
        val daysArray = mainObject.getJSONObject("forecast")
            .getJSONArray("forecastday")
        val name = mainObject.getJSONObject("location").getString("name")
        for (i in 0 until daysArray.length()) {
            val day = daysArray[i] as JSONObject
            val item = WeatherModel(
                name,
                day.getString("date"),
                day.getJSONObject("day").getJSONObject("condition")
                    .getString("text"),
                "",
                day.getJSONObject("day").getString("mintemp_c").toFloat().toInt().toString(),
                day.getJSONObject("day").getString("maxtemp_c").toFloat().toInt().toString(),
                day.getJSONObject("day").getJSONObject("condition")
                    .getString("icon"),
                day.getJSONArray("hour").toString()
            )
            list.add(item)
        }
        model.liveDataList.value = list
        return list
    }

    private fun parseCurrentData(mainObject: JSONObject, weatherItem: WeatherModel) {
        val item = WeatherModel(
            mainObject.getJSONObject("location").getString("name") +
                    ", " +
                    mainObject.getJSONObject("location").getString("country"),
            mainObject.getJSONObject("current").getString("last_updated"),
            mainObject.getJSONObject("current")
                .getJSONObject("condition").getString("text"),
            mainObject.getJSONObject("current").getString("temp_c").toFloat().toInt().toString(),
            weatherItem.maxTemp,
            weatherItem.minTemp,
            mainObject.getJSONObject("current")
                .getJSONObject("condition").getString("icon"),
            weatherItem.hours
        )
        model.liveDataCurrent.value = item
    }

    private fun showLoading() {
        val view = LayoutInflater.from(context).inflate(R.layout.loading_view, binding.root, false)
        binding.root.addView(view)
        val lottie = view.findViewById<LottieAnimationView>(R.id.lottieAnimationView)
        lottie.playAnimation()
        view.showToTime()

    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            MainFragment()
    }
}


