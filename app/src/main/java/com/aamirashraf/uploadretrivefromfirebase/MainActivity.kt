package com.aamirashraf.uploadretrivefromfirebase

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {
    private var personCollectionRef=Firebase.firestore.collection("persons")
    lateinit var etfirstName:EditText
    lateinit var etlastName:EditText
    lateinit var etAge:EditText
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val btnUploadData=findViewById<Button>(R.id.btnUploadData)
        etfirstName=findViewById(R.id.etFirstName)
        etlastName=findViewById(R.id.etLastName)
        etAge=findViewById(R.id.etAge)

        btnUploadData.setOnClickListener {
            val firstName=etfirstName.text.toString()
            val lastName=etlastName.text.toString()
            val age=etAge.text.toString().toInt()
//
            val person=Person(firstName,lastName,age)
            savePerson(person)
        }
    }
    private fun savePerson(person: Person){
        CoroutineScope(Dispatchers.IO).launch {
            try {
                personCollectionRef.add(person).await()
                withContext(Dispatchers.Main){
                    Toast.makeText(this@MainActivity,"Successfully saved data",Toast.LENGTH_LONG).show()
                }
            }catch (e:Exception){
                withContext(Dispatchers.Main){
                    Toast.makeText(this@MainActivity,e.message,Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}