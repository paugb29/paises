package com.example.paises

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson

class GameActivity : AppCompatActivity() {

    private lateinit var questionTextView: TextView
    private lateinit var option1Button: Button
    private lateinit var option2Button: Button
    private lateinit var option3Button: Button
    private lateinit var option4Button: Button
    private lateinit var scoreTextView: TextView
    private lateinit var highScoreTextView: TextView

    private lateinit var countries: List<Country>
    private var currentCountry: Country? = null
    private var correctAnswer: String = ""
    private var consecutiveCorrectAnswers = 0
    private var highestScore = 0
    private var score = 0
    private lateinit var filteredCapitals: List<String>
    private var selectedContinent: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_game)
        initializeViews()
        //Agafa la llista filtrada
        val currentFilteredList = intent.getSerializableExtra("currentFilteredList") as? List<Country>
        if (currentFilteredList != null) {
            countries = currentFilteredList
        } else {
            loadCountries()
        }
        selectedContinent = intent.getStringExtra("selectedContinent")
        loadFilteredCapitals()
        loadHighScore()
        loadNewQuestion()
        option1Button.setOnClickListener { checkAnswer(option1Button.text.toString()) }
        option2Button.setOnClickListener { checkAnswer(option2Button.text.toString()) }
        option3Button.setOnClickListener { checkAnswer(option3Button.text.toString()) }
        option4Button.setOnClickListener { checkAnswer(option4Button.text.toString()) }
    }

    private fun initializeViews() {
        questionTextView = findViewById(R.id.questionTextView)
        option1Button = findViewById(R.id.option1Button)
        option2Button = findViewById(R.id.option2Button)
        option3Button = findViewById(R.id.option3Button)
        option4Button = findViewById(R.id.option4Button)
        scoreTextView = findViewById(R.id.scoreTextView)
        highScoreTextView = findViewById(R.id.highScoreTextView)
    }

    private fun loadCountries() {
        val json: String = this.assets.open("countries.json").bufferedReader().use { it.readText() }
        val countriesData: Countries = Gson().fromJson(json, Countries::class.java)
        countries = countriesData.countries
    }
    private fun loadFilteredCapitals() {
        // Filtrar las capitales segons el continent selecciopnat
        filteredCapitals = if (selectedContinent.isNullOrEmpty()) {
            // map fa la llistade totas les capitals
            countries.map { it.capital_es }
        } else {
            countries.filter { it.continent_es == selectedContinent }.map { it.capital_es }
        }
    }


    private fun loadNewQuestion() {
        currentCountry = getRandomCountry()
        correctAnswer = currentCountry?.capital_es ?: ""
        val availableCapitals = if (filteredCapitals.isNotEmpty()) {
            filteredCapitals
        } else {
            countries.filter { it.continent_es == selectedContinent }.map { it.capital_es }
        }

        // Agafem pais aleatori i guardem la seva capital
        currentCountry = getRandomCountry()
        correctAnswer = currentCountry?.capital_es ?: ""

        // Barreja i agafa 3 capitals incorrectes

        val incorrectOptions = (availableCapitals - correctAnswer).shuffled().take(3)

        // Crem llista amb les incorrecte(3) i la correcta(1)
        val options = mutableListOf<String>().apply {
            add(correctAnswer)
            addAll(incorrectOptions)
        }

        // Barreja per que no sortin al mateix lloc
        options.shuffle()

        // Mostra la pregunta i las opcions
        updateQuestionViews(options)
        updateScoreTextView()
    }

    private fun updateScoreTextView() {
        scoreTextView.text = "Puntuación: $score"
        highScoreTextView.text = "Puntuación máxima: $highestScore"
    }

    private fun getRandomCountry(): Country {
        return countries.random()
    }
    private fun updateQuestionViews(options: List<String>) {
        currentCountry?.let {
            questionTextView.text = "¿Cuál es la capital de ${it.nameEs}?"

            if (options.size >= 4) {
                // Asignar las opciones a los botones
                option1Button.text = options[0]
                option2Button.text = options[1]
                option3Button.text = options[2]
                option4Button.text = options[3]
            }
        }
    }
    private fun checkAnswer(selectedAnswer: String) {
        //Comprova si la reposta es correct sies puntuacio++
        if (selectedAnswer == correctAnswer) {
            consecutiveCorrectAnswers++
            score++
            if (consecutiveCorrectAnswers > highestScore) {
                highestScore = consecutiveCorrectAnswers
                saveHighScore()
            }
            loadNewQuestion()
            updateScoreTextView()
        } else {
            consecutiveCorrectAnswers = 0
            showCorrectAnswer()
        }
    }

    private fun showCorrectAnswer() {
        Toast.makeText(
            this,
            "Respuesta incorrecta. La respuesta correcta es $correctAnswer",
            Toast.LENGTH_SHORT
        ).show()
        finish()
    }

    private fun saveHighScore() {
        //Guardem puntuacio maxima ams sharedprefences
        val preferences = getPreferences(MODE_PRIVATE)
        val editor = preferences.edit()
        editor.putInt(PREF_HIGH_SCORE, highestScore)
        editor.apply()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.actionarbargame, menu)
        return true
    }
    private fun returninicial() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
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
    private fun loadHighScore() {
        val preferences = getPreferences(MODE_PRIVATE)
        highestScore = preferences.getInt(PREF_HIGH_SCORE, 0)
    }

    companion object {
        //objecte de la classe per fer una constant i guardar puntuacio maxima shared prefrences
        private const val PREF_HIGH_SCORE = "high_score"
    }
}
