package com.example.mlkittutorial

import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.label.ImageLabeling
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_image_labelling.*
import kotlinx.android.synthetic.main.activity_image_labelling.img

class ImageLabellingActivity : AppCompatActivity() {

    var selected_photo : Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_labelling)

        setSupportActionBar(toolbar)

        ilButton.setOnClickListener {
            alertDialog()
        }
    }

    private fun alertDialog() {
        AlertDialog.Builder(this)
            .setTitle("Select Image")
            .setPositiveButton("Camera",{ dialogInterface: DialogInterface, i: Int ->
                val i = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                startActivityForResult(i, 100)
            })
            .setNegativeButton("Gallery",{ dialogInterface: DialogInterface, i: Int ->
                val intent = Intent(Intent.ACTION_PICK)
                intent.type = "image/*"
                startActivityForResult(intent,0)
            })
            .show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 100 && resultCode == Activity.RESULT_OK){
            val photo = data!!.extras!!.get("data") as Bitmap

            img.visibility = View.VISIBLE

            Glide.with(this).load(photo).into(img)

            imageLabelBitmap(photo)
        }
        else if (requestCode == 0 && resultCode == Activity.RESULT_OK && data!=null && data.data!=null){
            selected_photo = data.data

            img.visibility = View.VISIBLE
            Picasso.get().load(selected_photo).into(img)

            imageLabelUri(selected_photo)
        }
        else if (requestCode == 100 && resultCode == Activity.RESULT_CANCELED){
            Toast.makeText(this,"Operation Cancelled by the user", Toast.LENGTH_SHORT).show()
        }
        else if (requestCode == 0 && resultCode == Activity.RESULT_CANCELED){
            Toast.makeText(this,"Operation Cancelled by the user", Toast.LENGTH_SHORT).show()
        }
        else{
            Toast.makeText(this,"Operation Failed", Toast.LENGTH_SHORT).show()
        }
    }

    private fun imageLabelUri(selectedPhoto: Uri?) {
        val image = InputImage.fromFilePath(this,selectedPhoto!!)
        val labeler = ImageLabeling.getClient(ImageLabelerOptions.DEFAULT_OPTIONS)

        labeler.process(image)
            .addOnSuccessListener { labels ->
                // Task completed successfully
                // ...
                for (label in labels) {
                    val text = label.text
                    val confidence = label.confidence
                    val index = label.index

                    textView.visibility = View.VISIBLE
                    textView.text = "text: "+text+"\n confidence: "+confidence+"\n index: "+index
                }
            }
            .addOnFailureListener { e ->
                // Task failed with an exception
                // ...
            }
    }

    private fun imageLabelBitmap(photo: Bitmap) {
        val image = InputImage.fromBitmap(photo, 0)

        val labeler = ImageLabeling.getClient(ImageLabelerOptions.DEFAULT_OPTIONS)

        labeler.process(image)
            .addOnSuccessListener { labels ->
                // Task completed successfully
                // ...
                for (label in labels) {
                    val text = label.text
                    val confidence = label.confidence
                    val index = label.index

                    textView.visibility = View.VISIBLE
                    textView.text = "text: "+text+"\n confodence: "+confidence+"\n index: "+index
                }
            }
            .addOnFailureListener { e ->
                // Task failed with an exception
                // ...
            }
    }



    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu,menu)
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