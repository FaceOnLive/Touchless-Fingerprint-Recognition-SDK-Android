package com.ttv.fingerdemo

import android.content.Intent
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import com.ttv.finger.FingerSDK
import android.widget.TextView

class MainActivity : AppCompatActivity() {

    private val permissionsDelegate = PermissionsDelegate(this)
    private var hasPermission: Boolean = false;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val fingreSDK = FingerSDK.createInstance(this);
        Log.e("TestEngine", "hwid: " + fingreSDK.currentHWID);

        val ret = fingreSDK.setActivation("")
        Log.e("TestEngine", "activation: " + ret);
        if(ret != 0) {
            findViewById<TextView>(R.id.txtState).text = "No Activated!"
        }

        fingreSDK.init()

        val btnRegister = findViewById<Button>(R.id.btnRegister)
        btnRegister.setOnClickListener {
            val intent = Intent(this, FingerCaptureActivity::class.java)
            intent.putExtra("mode", 0);
            startActivityForResult(intent, 1)
        }

        val btnVerify = findViewById<Button>(R.id.btnVerify)
        btnVerify.setOnClickListener {
            val intent = Intent(this, FingerCaptureActivity::class.java)
            intent.putExtra("mode", 1);
            startActivityForResult(intent, 2)
        }

        hasPermission = permissionsDelegate.hasPermissions()
        if (!hasPermission) {
            permissionsDelegate.requestPermissions()
        }
    }

    override fun onResume() {
        super.onResume()
        if (permissionsDelegate.hasPermissions() && !hasPermission) {
            hasPermission = true
        } else {
            permissionsDelegate.requestPermissions()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == 1 && resultCode == RESULT_OK) {
            val registerID = data!!.getStringExtra("registerID")
            Toast.makeText(this, "Register succeed! " + registerID, Toast.LENGTH_SHORT).show()
        } else if(requestCode == 2 && resultCode == RESULT_OK) {
            val verifyResult = data!!.getIntExtra ("verifyResult", 0)
            val verifyID = data!!.getStringExtra("verifyID");
            if(verifyResult == 1) {
                val verified = "Verify succeed! ID: " + verifyID;
                Toast.makeText(this, verified, Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Verify failed!", Toast.LENGTH_SHORT).show()
            }
        }
    }
}