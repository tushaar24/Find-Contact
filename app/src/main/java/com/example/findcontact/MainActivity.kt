package com.example.findcontact

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.launch
import androidx.core.content.ContextCompat
import com.google.android.material.button.MaterialButton

class MainActivity : AppCompatActivity() {
    @SuppressLint("Recycle", "CutPasteId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val contact = registerForActivityResult(ActivityResultContracts.PickContact()) {
            var phoneNumber: String? = null
            val name: String?

            try {

                Log.d("URI", it.toString())

                val contactData: Uri = it
                val phone: Cursor? =
                    contentResolver.query(contactData, null, null, null, null)
                if (phone!!.moveToFirst()) {
                    name =
                        phone.getString(phone.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME))

                    val id: String =
                        phone.getString(phone.getColumnIndex(ContactsContract.Contacts._ID))
                    if (phone.getString(phone.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))
                            .toInt() > 0
                    ) {
                        val phones = contentResolver.query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + id,
                            null,
                            null
                        )
                        while (phones!!.moveToNext()) {
                            phoneNumber =
                                phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
                            Log.d("## Number", phoneNumber)
                        }
                        phones.close()
                    }

                    Log.d("## Contact Name", name)

                    val tvPhoneNumber: TextView = findViewById(R.id.tvPhoneNumber)
                    val tvName: TextView = findViewById(R.id.tvName)

                    tvPhoneNumber.text = phoneNumber
                    tvName.text = name
                }
            } catch (e: Exception) {
                Log.d("Contact", "error: $e")
            }
        }

        val readContactPermissionGiven = registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            if (it) {
                contact.launch()

            } else {
                Toast.makeText(this, "Please Grant the Contact Permission", Toast.LENGTH_SHORT)
                    .show()
            }
        }

        val getContactButton = findViewById<MaterialButton>(R.id.btnGetContact)

        getContactButton.setOnClickListener {

            if(ContextCompat.checkSelfPermission(applicationContext, Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED){
                contact.launch()
            }else{
                readContactPermissionGiven.launch(Manifest.permission.READ_CONTACTS)
            }
        }
    }
}