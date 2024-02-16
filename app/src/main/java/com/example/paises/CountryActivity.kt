package com.example.paises

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class CountryActivity : AppCompatActivity() {

    private lateinit var country: Country
    private lateinit var tvNombre: TextView
    private lateinit var tvContinente: TextView
    private lateinit var tvCapital: TextView
    private lateinit var tvKm: TextView
    private lateinit var tvbandera: TextView
    private lateinit var tvDialCode: TextView
    private lateinit var tvCode2: TextView
    private lateinit var tvCode3: TextView
    private lateinit var starImageView: ImageView

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.actionarbargame, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {

            R.id.home -> {
                returninicial()
                return true
            }

            else -> return super.onOptionsItemSelected(item)
        }
    }

    private fun returninicial() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.recycler_countries_row_card_especific)
            tvNombre = findViewById(R.id.tv3)
            tvContinente = findViewById(R.id.tv1)
            tvCapital = findViewById(R.id.tv2)
            tvKm = findViewById(R.id.tv4)
            tvbandera = findViewById(R.id.bandera)
            tvDialCode = findViewById(R.id.tv)
            tvCode2 = findViewById(R.id.tv5)
            tvCode3 = findViewById(R.id.tv6)
            starImageView = findViewById(R.id.star)
            country = intent.getSerializableExtra("country") as Country
            tvNombre.text = country.nameEs
            tvContinente.text = country.continent_es
            tvCapital.text = country.capital_es
            tvKm.text = country.km2.toString()
            tvbandera.text = country.emoji
            tvDialCode.text = country.dialCode
            tvCode2.text = country.code2
            tvCode3.text = country.code3

            // Favorito marcat
            val starImageResource = if (country.isFavorite) {
                android.R.drawable.btn_star_big_on
            } else {
                android.R.drawable.btn_star_big_off
            }
            starImageView.setImageResource(starImageResource)

            starImageView.setOnClickListener {

                country.isFavorite = !country.isFavorite


                val updatedStarImageResource = if (country.isFavorite) {
                    android.R.drawable.btn_star_big_on
                } else {
                    android.R.drawable.btn_star_big_off
                }
                starImageView.setImageResource(updatedStarImageResource)
                saveFavoriteState(country.nameEn, country.isFavorite)
            }

    }

    private fun saveFavoriteState(countryName: String, isFavorite: Boolean) {
        //Guardar estat favorito sharedpreferences
        val sharedPref = getSharedPreferences("favorites", Context.MODE_PRIVATE)
        val editor = sharedPref.edit()
        editor.putBoolean(countryName, isFavorite)
        editor.apply()
    }
    override fun onBackPressed() {
        // Si ha canviat Favorito li pasa al main
        if (country.isFavorite != intent.getBooleanExtra("initialFavoriteState", false)) {
            val resultIntent = Intent()
            resultIntent.putExtra("updatedCountry", country)
            setResult(Activity.RESULT_OK, resultIntent)
        }
        super.onBackPressed()
    }
}
