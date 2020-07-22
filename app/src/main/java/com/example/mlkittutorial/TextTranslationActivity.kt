package com.example.mlkittutorial

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.speech.RecognizerIntent
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
import java.lang.Exception
import java.util.*

class TextTranslationActivity : AppCompatActivity() {

    private val labels = arrayListOf("English","Afrikaans", "Arabic", "Belarusian","bg-Bulgarian","bn-Bengali","cs-Czech","cy-Welsh",
        "Catalan","de-German","el-Greek","Danish","es-Spanish","Finnish","French","Gujarati","Hindi","Italian","Japanese",
        "Korean","Romanian","Russian","Thai","Tamil","Telugu","Vietnamese","Ukrainian","Urdu","fa-Persian","ga-Irish","kn-Kannada",
        "mr-Marathi","nl-Dutch","pt-Portuguese","tr-Turkish","zh-Chinese")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_text_translation)

        setSupportActionBar(toolbar)

        setUpSpinner()

        mic_button.setOnClickListener {
            val i = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)

            i.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            i.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
            i.putExtra(RecognizerIntent.EXTRA_PROMPT,"Speak Now!!!")

            try {
                startActivityForResult(i,1000)
            }
            catch (e:Exception){
                Toast.makeText(this,"Failed to detect the text",Toast.LENGTH_SHORT).show()
            }
        }

//        TRANSLATE LANGUAGE
        btnTranslate.setOnClickListener {
//            MainActivity::alertDialog
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
                    translatedText.setText(it.toString())
                    if (editText.text.toString() == ""){
                        translatedText.setText("Please enter some text")
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when(requestCode){
            1000->{
                if (resultCode == Activity.RESULT_OK && data!=null){
                    editText.setText(data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS).get(0))
                }
            }
        }
    }
}
