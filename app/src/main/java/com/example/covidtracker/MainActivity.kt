package com.example.covidtracker

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONException
import org.json.JSONObject

class MainActivity : AppCompatActivity() {

    private lateinit var stateSpinner: Spinner
    private lateinit var districtSpinner: Spinner
    private lateinit var activeCasesTextView: TextView
    private lateinit var confrm: TextView
    private lateinit var mgdoth: TextView
    private lateinit var deceased: TextView
    private lateinit var recover: TextView
    private lateinit var covidData: JSONObject

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        stateSpinner = findViewById(R.id.stateSpinner)
        districtSpinner = findViewById(R.id.districtSpinner)
        activeCasesTextView = findViewById(R.id.activeCasesTextView)
        confrm = findViewById(R.id.cnf)
        mgdoth = findViewById(R.id.mg)
        deceased = findViewById(R.id.de)
        recover = findViewById(R.id.re)
        fetchCovidData()
    }

    private fun fetchCovidData() {
        val requestQueue = Volley.newRequestQueue(this)
        val url = "https://data.covid19india.org/state_district_wise.json"

        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.GET,
            url,
            null,
            { response ->
                covidData = response
                populateStateSpinner()
            },
            { error ->
                error.printStackTrace()
                activeCasesTextView.text = "Error: ${error.toString()}"
            }
        )
        requestQueue.add(jsonObjectRequest)
    }

    private fun populateStateSpinner() {
        try {
            val stateNames = covidData.keys().asSequence().toList()

            val stateAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, stateNames)
            stateAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            stateSpinner.adapter = stateAdapter

            stateSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {

                override fun onItemSelected(parentView: AdapterView<*>, selectedItemView: View?, position: Int, id: Long) {
                    val selectedState = stateNames[position]
                    populateDistrictSpinner(selectedState)
                }

                override fun onNothingSelected(parentView: AdapterView<*>) {
                    // Do nothing here
                }
            }
        } catch (e: JSONException) {
            e.printStackTrace()
            activeCasesTextView.text = "Error: ${e.message}"
        }
    }

    private fun populateDistrictSpinner(selectedState: String) {
        try {
            val districtData = covidData.getJSONObject(selectedState).getJSONObject("districtData")
            val districtNames = districtData.keys().asSequence().toList()
            val districtAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, districtNames)
            districtAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            districtSpinner.adapter = districtAdapter

            districtSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parentView: AdapterView<*>, selectedItemView: View?, position: Int, id: Long) {
                    val selectedDistrict = districtNames[position]
                    displayActiveCases(selectedState, selectedDistrict)
                }

                override fun onNothingSelected(parentView: AdapterView<*>) {

                }
            }
        } catch (e: JSONException) {
            e.printStackTrace()
            activeCasesTextView.text = "Error: ${e.message}"
        }
    }

    private fun displayActiveCases(selectedState: String, selectedDistrict: String) {
        try {
            // Get the active cases for the selected district and state
            val activeCases = covidData.getJSONObject(selectedState).getJSONObject("districtData").getJSONObject(selectedDistrict).getString("active")
            val c = covidData.getJSONObject(selectedState).getJSONObject("districtData").getJSONObject(selectedDistrict).getString("confirmed")
            val mg = covidData.getJSONObject(selectedState).getJSONObject("districtData").getJSONObject(selectedDistrict).getString("migratedother")
            val d = covidData.getJSONObject(selectedState).getJSONObject("districtData").getJSONObject(selectedDistrict).getString("deceased")
            val r = covidData.getJSONObject(selectedState).getJSONObject("districtData").getJSONObject(selectedDistrict).getString("recovered")
            activeCasesTextView.text = "Active Cases : $activeCases"
            confrm.text = "Confirmed Cases : $c"
            mgdoth.text = "Migratedother Cases : $mg"
            deceased.text = "Deceased Cases : $d"
            recover.text = "Recovered Cases : $r"


        } catch (e: JSONException) {
            e.printStackTrace()
            activeCasesTextView.text = "Error: ${e.message}"
        }
    }
}
