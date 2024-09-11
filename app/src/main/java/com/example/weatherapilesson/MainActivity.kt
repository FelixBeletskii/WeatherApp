package com.example.weatherapilesson

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.weatherapilesson.databinding.ActivityMainBinding
import org.json.JSONObject

const val API_KEY = "758cdee76fb245999d960954240509"
// ОБЯЗАТЕЛЬНО!! ДОБАВИТЬ РАЗРЕШЕНИЕ ИНТЕРНЕТА И МЕСТОПОЛОЖЕНИЯ И ЗАВИСИМОСТЬ В ГРАДЛФАЙЛ

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)



    }

   /* private fun getResult(name: String) {            // для отправки запроса и получения результата используем функцию с параметром города
        val url =
            "https://api.weatherapi.com/v1/current.json" +  //  указываем ЮРЛ - то, куда пойдет запрос чтобы получить ответ
                    "?key=$API_KEY&q=$name&aqi=no"   // разбирая юрл можно понять, где и куда засовывам необходимые данные
        // где апи ключ, а где имя города как в данном примере
        val queue =
            Volley.newRequestQueue(this)  // через библиотеку воллей создаем очередь запросов
        val stringRequest = StringRequest(Request.Method.GET, url,  // создаем строковый запрос,
            // на вход тип работы с запросом( в данном случае получение), созданный юрл,
            { response -> //слушатель ответа
                val obj = JSONObject(response)
                val temp = obj.getJSONObject("current")
                Log.v("Mylog", "response: ${temp.getString("temp_c")}")

            },                                                      //и слушатель ошибки
            {
                Log.v("Mylog", "error: $it")
            }

        )
        queue.add(stringRequest) // добавляем запрос в очередь
    }  */
}