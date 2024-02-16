package com.example.paises

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.example.paises.adapters.CountryAdapter

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var countryAdapter: CountryAdapter
    private lateinit var countries: List<Country>

    private var selectedContinent: String? = null
    private var selectedSortOption: String? = null

    private var pantallafav = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupRecyclerView()
        loadSimple()
    }

    private fun setupRecyclerView() {
        recyclerView = findViewById(R.id.recycler)
        recyclerView.layoutManager = LinearLayoutManager(this)
        countryAdapter = CountryAdapter(emptyList(), this)
        recyclerView.adapter = countryAdapter
    }

    private fun loadSimple() {
        val json: String = this.assets.open("countries.json").bufferedReader().use { it.readText() }
        val countriesData: Countries = Gson().fromJson(json, Countries::class.java)

        //Emmagatzemar estat favorito amb shared preferences
        val sharedPref = getSharedPreferences("favorites", Context.MODE_PRIVATE)
        for (country in countriesData.countries) {
            val isFavorite = sharedPref.getBoolean(country.nameEs, false)
            country.isFavorite = isFavorite
        }

        countries = countriesData.countries
        updateAdapterData()
    }

    private fun favorite() {
        pantallafav = !pantallafav

        val filteredList = if (pantallafav) {
            countries.filter { it.isFavorite && (selectedContinent.isNullOrEmpty() || it.continent_es == selectedContinent) }
        } else {
            countries.filter { selectedContinent.isNullOrEmpty() || it.continent_es == selectedContinent }
        }

        countryAdapter.setCountries(filteredList)
        countryAdapter.notifyDataSetChanged()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        //Amb el rquest code comprova que l'altreactivity acabi correctament
        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            // Verificar si ha habido cambios en el favorito
            val updatedCountry = data?.getSerializableExtra("updatedCountry") as? Country
            updatedCountry?.let {
                // Actualizar el estado de favorito en la lista y en el adaptador
                updateFavoriteState(it)
            }
        }
    }

    private fun updateFavoriteState(updatedCountry: Country) {
        // Actualitzat l'estat de favorito a la llista
        val index = countries.indexOfFirst { it.nameEs == updatedCountry.nameEs }
        if (index != -1) {
            countries[index].isFavorite = updatedCountry.isFavorite

            // Actualizar el adaptador
            updateAdapterData()
        }

        // También podrías actualizar el estado en SharedPreferences aquí si es necesario
        // ...
    }

    private fun updateAdapterData() {
        val filteredList = filterAndSortData()
        countryAdapter.setCountries(filteredList)
        countryAdapter.notifyDataSetChanged()
    }

    private fun filterAndSortData(): List<Country> {
        // Filtrar per continent seleccionat
        val filteredByContinent = if (selectedContinent.isNullOrEmpty()) {
            countries
        } else {
            countries.filter { it.continent_es == selectedContinent }
        }

        // Filtrar per favoritos
        val filteredList = if (pantallafav) {
            filteredByContinent.filter { it.isFavorite }
        } else {
            filteredByContinent
        }

        return when (selectedSortOption) {
            "Pais (Ascendente)" -> filteredList.sortedBy { it.nameEs }
            "Pais (Descendente)" -> filteredList.sortedByDescending { it.nameEs }
            "Capital (Ascendente)" -> filteredList.sortedBy { it.capital_es }
            "Capital (Descendente)" -> filteredList.sortedByDescending { it.capital_es }
            else -> filteredList
        }
    }

    private fun startGameActivity() {
        val intent = Intent(this, GameActivity::class.java)
        val currentFilteredList = countryAdapter.getCurrentFilteredList()

        // Intent del joc pasantli els filtres actuals
        intent.putExtra("currentFilteredList", ArrayList(currentFilteredList))
        intent.putExtra("selectedContinent", selectedContinent)
        intent.putExtra("selectedSortOption", selectedSortOption)

        startActivity(intent)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.actionbar, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.todos -> {
                selectedContinent = ""
                updateAdapterData()
                return true
            }

            R.id.favorito -> {
                favorite()
                return true
            }

            R.id.game -> {
                startGameActivity()
                return true
            }

            R.id.continent_europe, R.id.continent_africa, R.id.continent_north_america,
            R.id.continent_south_america, R.id.continent_asia, R.id.continent_oceania -> {
                selectedContinent = item.title.toString()
                updateAdapterData()
                return true
            }

            R.id.order_by_name_asc, R.id.order_by_name_desc,
            R.id.order_by_capital_asc, R.id.order_by_capital_desc -> {
                selectedSortOption = item.title.toString()
                updateAdapterData()
                return true
            }

            else -> return super.onOptionsItemSelected(item)
        }
    }
}