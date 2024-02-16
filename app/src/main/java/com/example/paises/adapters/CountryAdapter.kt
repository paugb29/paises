package com.example.paises.adapters

import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.paises.Country
import com.example.paises.CountryActivity
import com.example.paises.R

class CountryAdapter(private var countries: List<Country>,private val activity: AppCompatActivity) :
    RecyclerView.Adapter<CountryAdapter.ViewHolder>() {

    private lateinit var context: Context

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.tv3)
        val capital: TextView = itemView.findViewById(R.id.tv2)
        val continent: TextView = itemView.findViewById(R.id.tv1)
        val km2: TextView = itemView.findViewById(R.id.tv4)
        val cardView: CardView = itemView.findViewById(R.id.card)
        val star: ImageView = itemView.findViewById(R.id.star)
        val flag: TextView = itemView.findViewById(R.id.bandera)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        val itemView =
            LayoutInflater.from(context).inflate(R.layout.recycler_countries_row_card, parent, false)
        return ViewHolder(itemView)
    }

    fun getCurrentFilteredList(): List<Country> {
        return countries
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val country = countries[position]

        holder.name.text = country.nameEs
        holder.capital.text = country.capital_es
        holder.continent.text = country.continent_es
        holder.flag.text = country.emoji


        val km2Text = country.km2.toString()
        holder.km2.text = km2Text

        holder.star.setOnClickListener {
            // Canviar la imatge si es favorito
            country.isFavorite = !country.isFavorite
            val starImageResource = if (country.isFavorite) {
                android.R.drawable.btn_star_big_on
            } else {
                android.R.drawable.btn_star_big_off
            }
            holder.star.setImageResource(starImageResource)
            saveFavoriteState(country.nameEn, country.isFavorite)
        }
        if (country.isFavorite) {
            holder.star.setImageResource(R.drawable.estrella_on)
        } else {
            holder.star.setImageResource(R.drawable.estrella_off)
        }

        // Km en negreta
        if (country.km2 > 1000000) {
            holder.km2.setTypeface(null, Typeface.BOLD)
        } else {
            holder.km2.setTypeface(null, Typeface.NORMAL)
        }
//Canviar color de fons x continent
        when (country.continent_es) {
            "Europa" -> holder.cardView.setBackgroundColor(holder.itemView.context.getColor(R.color.colorEuropa))
            "África" -> holder.cardView.setBackgroundColor(holder.itemView.context.getColor(R.color.colorAfrica))
            "América del Norte" -> holder.cardView.setBackgroundColor(holder.itemView.context.getColor(R.color.colorAmericaNorte))
            "América del Sur" -> holder.cardView.setBackgroundColor(holder.itemView.context.getColor(R.color.colorAmericaSur))
            "Asia" -> holder.cardView.setBackgroundColor(holder.itemView.context.getColor(R.color.colorAsia))
            "Oceanía" -> holder.cardView.setBackgroundColor(holder.itemView.context.getColor(R.color.colorOceania))
        }


        holder.cardView.setOnClickListener {

            val intent = Intent(context, CountryActivity::class.java)
            intent.putExtra("country", country)

            activity.startActivityForResult(intent, 1)
        }

    }

    fun setCountries(newCountries: List<Country>) {
        //Actualitza la llista de paisos
        countries = newCountries
        notifyDataSetChanged()
    }
    private fun saveFavoriteState(countryName: String, isFavorite: Boolean) {
        //Guardar estat favorito amb sharedPreferences
        val sharedPref = context.getSharedPreferences("favorites", Context.MODE_PRIVATE)
        val editor = sharedPref.edit()
        editor.putBoolean(countryName, isFavorite)
        editor.apply()
    }


    override fun getItemCount(): Int {
        return countries.size
    }
}