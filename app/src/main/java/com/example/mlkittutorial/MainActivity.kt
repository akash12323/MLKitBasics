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
import com.google.mlkit.vision.text.TextRecognition
import com.squareup.picasso.Picasso
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    var selected_photo: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(toolbar)

        trButton.setOnClickListener {
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
            .setNegativeButton("Gallery",{dialogInterface: DialogInterface, i: Int ->
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
            Glide.with(this)
                .load(photo)
                .centerCrop()
                .into(img)

            textRecoginitionFromBitmap(photo)
        }
        else if (requestCode == 0 && resultCode == Activity.RESULT_OK && data!=null && data.data!=null){
            selected_photo = data.data

            img.visibility = View.VISIBLE

            CropImage.activity(selected_photo)
                .setGuidelines(CropImageView.Guidelines.ON)
                .setCropShape(CropImageView.CropShape.RECTANGLE)
                .start(this)

        }
        else if (requestCode == 100 && resultCode == Activity.RESULT_CANCELED){
            Toast.makeText(this,"Operation Cancelled by the user",Toast.LENGTH_SHORT).show()
        }
        else if (requestCode == 0 && resultCode == Activity.RESULT_CANCELED){
            Toast.makeText(this,"Operation Cancelled by the user",Toast.LENGTH_SHORT).show()
        }
        else{
            Toast.makeText(this,"Operation Failed",Toast.LENGTH_SHORT).show()
        }

        if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
            val result = CropImage.getActivityResult(data)
            if (resultCode == Activity.RESULT_OK){
                Glide.with(this).load(result.uri)
                    .into(img)

                textRecoginitionFromUri(result.uri)
            }
        }
    }

    private fun textRecoginitionFromUri(selectedPhoto: Uri?) {
        val image = InputImage.fromFilePath(this,selectedPhoto!!)
//        try {
//            image = InputImage.fromFilePath(this,selectedPhoto!!)
//        } catch (e: IOException) {
//            e.printStackTrace()
//        }
        val recognizer = TextRecognition.getClient()

        val result = recognizer.process(image)
            .addOnSuccessListener { visionText ->
                for (block in visionText.textBlocks) {
                    val boundingBox = block.boundingBox
                    val cornerPoints = block.cornerPoints
                    val text = block.text

                    textView.text = text
                    textView.visibility = View.VISIBLE

                    for (line in block.lines) {
                        // ...
                        for (element in line.elements) {
//                            Toast.makeText(this,"Element: "+ element.text,Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
            .addOnFailureListener { e ->
                // Task failed with an exception
                // ...
                Toast.makeText(this,"Failed to recoginize text",Toast.LENGTH_SHORT).show()
            }
    }

    private fun textRecoginitionFromBitmap(bitmap: Bitmap) {
        val image = InputImage.fromBitmap(bitmap, 0)

        val recognizer = TextRecognition.getClient()

        //PROCESS IMAGE
        val result = recognizer.process(image)
            .addOnSuccessListener { visionText ->
                for (block in visionText.textBlocks) {
                    val boundingBox = block.boundingBox
                    val cornerPoints = block.cornerPoints
                    val text = block.text

                    textView.text = text
                    textView.visibility = View.VISIBLE

                    for (line in block.lines) {
                        // ...
                        for (element in line.elements) {
//                            Toast.makeText(this,"Element: "+ element.text,Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
            .addOnFailureListener { e ->
                // Task failed with an exception
                // ...
                Toast.makeText(this,"Failed to recoginize text",Toast.LENGTH_SHORT).show()
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
