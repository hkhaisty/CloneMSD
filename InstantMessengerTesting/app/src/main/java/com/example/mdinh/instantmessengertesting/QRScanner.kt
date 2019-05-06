package com.example.mdinh.instantmessengertesting

import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.widget.TextView
import android.widget.Toast
import com.google.android.gms.vision.CameraSource
import com.google.android.gms.vision.Detector
import com.google.android.gms.vision.barcode.Barcode
import com.google.android.gms.vision.barcode.BarcodeDetector

class QRScanner : AppCompatActivity() {

    private lateinit var svBarCode: SurfaceView
    private lateinit var tvBarCode: TextView

    private lateinit var detector: BarcodeDetector
    private lateinit var cameraSource: CameraSource

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_qrscanner)

        svBarCode = findViewById(R.id.qrScannerBarcode)
        tvBarCode = findViewById(R.id.tv_barcode)

        detector = BarcodeDetector.Builder(this).setBarcodeFormats(Barcode.QR_CODE).build()
        detector.setProcessor(object: Detector.Processor<Barcode>{

            override fun release() {
            }

            override fun receiveDetections(detections: Detector.Detections<Barcode>?) {
                var barcodes = detections?.detectedItems
                if (barcodes!!.size() > 0){
                    tvBarCode.post{
                        tvBarCode.text = barcodes.valueAt(0).displayValue

                    }
                }
            }
        })

        cameraSource = CameraSource.Builder(this, detector).setRequestedPreviewSize(1024, 768).setRequestedFps(25.toFloat()).setAutoFocusEnabled(true).build()
        svBarCode.holder.addCallback(object : SurfaceHolder.Callback2{
            override fun surfaceRedrawNeeded(holder: SurfaceHolder?){}

            override fun surfaceChanged(holder: SurfaceHolder?, format: Int, w: Int, h: Int){}

            override fun surfaceDestroyed(holder: SurfaceHolder?){
                cameraSource.stop()
            }

            override fun surfaceCreated(holder: SurfaceHolder?){
                if (ContextCompat.checkSelfPermission(this@QRScanner, android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED)
                cameraSource.start(holder)

                else ActivityCompat.requestPermissions(this@QRScanner, arrayOf(android.Manifest.permission.CAMERA), 123)
            }
        })
    }

    @SuppressLint("MissingPermission")
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == 123){
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                cameraSource.start(svBarCode.holder)
            else Toast.makeText(this, "Scanner won't work without permission.", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        detector.release()
        cameraSource.stop()
        cameraSource.release()
    }
}
