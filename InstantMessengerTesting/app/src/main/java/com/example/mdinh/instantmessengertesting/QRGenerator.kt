package com.example.mdinh.instantmessengertesting

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import com.google.zxing.BarcodeFormat
import com.journeyapps.barcodescanner.BarcodeEncoder
import java.lang.Exception

class QRGenerator : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_qrgenerator)

        val etText = findViewById<EditText>(R.id.et_text)
        val btnGen = findViewById<Button>(R.id.bt_generate)
        val ivBarcode = findViewById<ImageView>(R.id.iv_barcode)

        btnGen.setOnClickListener{
            try{
                val encoder = BarcodeEncoder()
                val bitmap = encoder.encodeBitmap(etText.text.toString(), BarcodeFormat.QR_CODE, 500, 500)
                ivBarcode.setImageBitmap(bitmap)
            } catch (e: Exception){
                e.printStackTrace()
            }
        }
    }
}
