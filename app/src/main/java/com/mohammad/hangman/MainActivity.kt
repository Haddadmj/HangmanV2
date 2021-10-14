package com.mohammad.hangman

import android.content.Context
import android.content.SharedPreferences
import android.hardware.input.InputManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {

    private lateinit var tvPhrase: TextView
    private lateinit var tvGuess: TextView
    private lateinit var tvScore: TextView
    private lateinit var mainRV: RecyclerView
    private lateinit var rvAdapter: RVAdapter
    private lateinit var messages: ArrayList<String>
    private lateinit var etGuess: EditText
    private lateinit var checkBtn: Button
    private lateinit var sharedPreferences: SharedPreferences

    private var phrase = "Something is here"
    private var secret = CharArray(phrase.length)
    private var letters = ArrayList<String>()
    private var numOfGuess = 10
    private var score = 0
    private var highScore = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sharedPreferences =
            this.getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE)

        highScore = sharedPreferences.getInt("highscore",0)

        initVars()
        updateText()

        checkBtn.setOnClickListener {
            if (etGuess.hint == "Enter Full Phrase")
                checkPhrase()
            else
                checkLetter()
        }

    }

    override fun recreate() {
        super.recreate()

        phrase = "Something is here"
        secret = CharArray(phrase.length)
        letters = ArrayList()
        numOfGuess = 10
        score = 0
        highScore = 0
        phrase.forEachIndexed { index, c ->
            if (c == ' ')
                secret[index] = ' '
            else secret[index] = '*'
        }

    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putCharArray("secret", secret)
        outState.putStringArrayList("letters", letters)
        outState.putString("sep", tvPhrase.text.toString())
        outState.putInt("guesses", numOfGuess)
        outState.putStringArrayList("messages", messages)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        secret = savedInstanceState.getCharArray("secret") as CharArray
        tvPhrase.text = savedInstanceState.getString("sep")
        numOfGuess = savedInstanceState.getInt("guesses")
        messages = savedInstanceState.getStringArrayList("messages") as ArrayList<String>
        letters = savedInstanceState.getStringArrayList("letters") as ArrayList<String>
        mainRV.adapter = RVAdapter(messages)
        mainRV.layoutManager = LinearLayoutManager(this)
        updateGuesses()

    }

    private fun updateText() {
        tvPhrase.text = "Phrase: ${String(secret)}"
        tvScore.text = "HighScore: $highScore"
    }

    fun updateGuesses() {
        tvGuess.text = "Guessed Letter: $letters"
    }

    private fun checkLetter() {
        if (etGuess.text.length != 1) {
            Toast.makeText(
                applicationContext,
                "Enter One Letter",
                Toast.LENGTH_SHORT
            ).show()
            return
        }
        var found = 0
        val letter = etGuess.text.toString().uppercase()[0]
        for (i in phrase.uppercase().indices) {
            if (letter == phrase.uppercase()[i] && secret[i] != letter) {
                secret[i] = letter
                found++
            }
        }

        letters.add("$letter")
        updateGuesses()

        updateText()

        if (found != 0) {
            rvAdapter.add("Found $found $letter(s)")
            score++
        } else {
            rvAdapter.add("No $letter Left in Phrase")
            rvAdapter.add("Number of guesses ${--numOfGuess}")
        }

        save()

        if (!secret.contains('*')){
            disable()
            alertDialog(true)
        }

        if (numOfGuess == 0) {
            disable()
            alertDialog(false)
        }

        clear()
        etGuess.hint = "Enter Full Phrase"
        mainRV.scrollToPosition(messages.size - 1)
    }

    private fun save(){
        if(score > highScore){
            with(sharedPreferences.edit()){
                putInt("highscore",score)
                apply()
            }
        }
    }

    private fun checkPhrase() {
        if (etGuess.text.length == 1) {
            Toast.makeText(
                applicationContext,
                "Enter Full Phrase",
                Toast.LENGTH_SHORT
            ).show()
            return
        }
        if (etGuess.text.toString().uppercase() == phrase.uppercase()) {
            tvPhrase.text = "Phrase: ${phrase.uppercase()}"
            rvAdapter.add("Correct Guess")
            disable()
            alertDialog(true)
        } else {
            rvAdapter.add("Wrong Guess")
        }
        clear()
        etGuess.hint = "Enter A Letter"
        mainRV.scrollToPosition(messages.size - 1)
    }

    private fun clear() {
        etGuess.text.clear()
        etGuess.clearFocus()
        (getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).hideSoftInputFromWindow(
            etGuess.windowToken,
            0
        )
    }

    private fun disable() {
        etGuess.isEnabled = false
        etGuess.isClickable = false
        checkBtn.isEnabled = false
        checkBtn.isClickable = false
    }

    private fun alertDialog(b: Boolean) {
        val alertDialog = AlertDialog.Builder(this)
        if (b) alertDialog.setTitle("You Won") else alertDialog.setTitle("You Lose")

        alertDialog.setMessage("Want to play again")
        alertDialog.setPositiveButton("Yes") { _, _ -> this.recreate() }
        alertDialog.setNegativeButton("No") { dialog, _ -> dialog.dismiss() }
        alertDialog.show()
    }

    private fun initVars() {
        tvPhrase = findViewById(R.id.tvPhrase)
        tvGuess = findViewById(R.id.tvGuess)
        tvScore = findViewById(R.id.tvScore)
        mainRV = findViewById(R.id.rvMain)
        messages = arrayListOf()
        rvAdapter = RVAdapter(messages)
        mainRV.adapter = rvAdapter
        mainRV.layoutManager = LinearLayoutManager(this)
        etGuess = findViewById(R.id.etGuess)
        checkBtn = findViewById(R.id.checkBtn)

        phrase.forEachIndexed { index, c ->
            if (c == ' ')
                secret[index] = ' '
            else secret[index] = '*'
        }
    }
}