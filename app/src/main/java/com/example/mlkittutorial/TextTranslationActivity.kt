package com.example.mlkittutorial

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.Toast
import com.google.android.gms.tasks.OnCompleteListener
import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.nl.languageid.LanguageIdentification
import com.google.mlkit.nl.translate.*
import kotlinx.android.synthetic.main.activity_text_translation.*
import org.intellij.lang.annotations.Language

class TextTranslationActivity : AppCompatActivity() {

    private val labels = arrayListOf("Afrikaans", "Arabic", "Belarusian","Catalan","Danish",
        "English", "Finnish","French","Gujarati","Hindi","Italian","Japanese", "Korean","Romanian","Russian",
        "Thai","Tamil","Telugu","Vietnamese","Ukrainian","Urdu")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_text_translation)

        setSupportActionBar(toolbar)

        setUpSpinner()

//        TRANSLATE LANGUAGE
        btnTranslate.setOnClickListener {
            val category = spinnerCategory.selectedItem.toString()
            Log.d("lang",category.toLowerCase().substring(0,2))

            val options = TranslatorOptions.Builder()
                .setSourceLanguage(spinnerCategory1.selectedItem.toString().substring(0,2).toLowerCase())
                .setTargetLanguage(category.toLowerCase().substring(0,2))
                .build()

            val englishHindiTranslator = Translation.getClient(options)

            var conditions = DownloadConditions.Builder()
                .requireWifi()
                .build()
            englishHindiTranslator.downloadModelIfNeeded(conditions)
                .addOnSuccessListener {
                    // Model downloaded successfully. Okay to start translating.
                    // (Set a flag, unhide the translation UI, etc.)
                }
                .addOnFailureListener { //exception ->
                    // Model couldn’t be downloaded or other internal error.
                    // ...
                    Toast.makeText(this,"Failed to download module. Please try later!!", Toast.LENGTH_LONG).show()
                }

            englishHindiTranslator.translate(editText.text.toString())
                .addOnSuccessListener {
                    translatedText.text = it.toString()
                    if (editText.text.toString() == ""){
                        translatedText.text = "Please enter some text"
                    }
                    // Translation successful.
                }
                .addOnFailureListener { //exception ->
                    // Error.
                    // ...
                }
        }


//        identify language
        val languageIdentifier = LanguageIdentification.getClient()
        languageIdentifier.identifyLanguage(translatedText.toString())
            .addOnSuccessListener {
                if (it == "und") {
                    Log.i("langId", "Can't identify language.")
                } else {
                    Log.i("langId", "Language: $it")
                }
            }
            .addOnFailureListener {
                // Model couldn’t be loaded or other internal error.
                // ...
            }

    }

    private fun setUpSpinner() {
        val adapter = ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, labels)

        labels.sort()
        spinnerCategory.adapter = adapter
        spinnerCategory1.adapter = adapter
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.text_recognition->{
                startActivity(Intent(this,MainActivity::class.java))
                finish()
            }
            R.id.image_labelling->{
                startActivity(Intent(this,ImageLabellingActivity::class.java))
                finish()
            }
            R.id.text_translate->{
                startActivity(Intent(this,TextTranslationActivity::class.java))
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
