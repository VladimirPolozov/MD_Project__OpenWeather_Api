package com.example.md_project__openweather_api

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val cityInput: EditText = findViewById(R.id.city_input)
        val searchButton: Button = findViewById(R.id.search_button)
        val cityNameTextView: TextView = findViewById(R.id.city_name)

        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar)
        val rView: RecyclerView = findViewById(R.id.rView)
        val adapter = Adapter(DiffCallback())

        setSupportActionBar(toolbar)
        rView.adapter = adapter
        rView.layoutManager = LinearLayoutManager(this)

        searchButton.setOnClickListener {
            val cityName = cityInput.text.toString().trim()

            if (cityName.isNotEmpty()) {
                cityNameTextView.text = cityName
                val mService: RetrofitServices = Common.retrofitService
                val appId = "b5d6ced05ea56639a3c663c5b724214b"

                try {
                    mService.getWeatherListByCity(cityName, appId)
                        .enqueue(object : Callback<WeatherWrapper> {
                            override fun onResponse(
                                call: Call<WeatherWrapper>,
                                response: Response<WeatherWrapper>
                            ) {
                                if (response.isSuccessful) {
                                    val forecast = response.body()
                                    adapter.submitList(forecast!!.list)
                                } else {
                                    Toast.makeText(this@MainActivity, "City not found", Toast.LENGTH_SHORT).show()
                                }
                            }
                            override fun onFailure(call: Call<WeatherWrapper>, t: Throwable) {
                                Toast.makeText(this@MainActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                            }
                        })
                } catch (e: Exception) {
                    Toast.makeText(this@MainActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "City field is empty", Toast.LENGTH_SHORT).show()
            }
        }
    }
}