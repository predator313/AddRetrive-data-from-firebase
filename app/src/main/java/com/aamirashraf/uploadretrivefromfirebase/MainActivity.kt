package com.aamirashraf.uploadretrivefromfirebase

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {
    private var personCollectionRef=Firebase.firestore.collection("persons")
    private lateinit var etfirstName:EditText
    private lateinit var etlastName:EditText
    private lateinit var etAge:EditText
    lateinit var tvPerson:TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val btnUploadData=findViewById<Button>(R.id.btnUploadData)
        val btnRetrieveData=findViewById<Button>(R.id.btnRetrieveData)
        etfirstName=findViewById(R.id.etFirstName)
        etlastName=findViewById(R.id.etLastName)
        etAge=findViewById(R.id.etAge)
        tvPerson=findViewById(R.id.tvPersons)

        btnUploadData.setOnClickListener {
            val firstName=etfirstName.text.toString()
            val lastName=etlastName.text.toString()
//            val age=etAge.text.toString()
//
            val person=Person(firstName,lastName)
            savePerson(person)
        }
        subscribleToRealTimeUpdates()
//        btnRetrieveData.setOnClickListener {
//            retrievePerson()
//        }
    }
    private fun subscribleToRealTimeUpdates(){
        personCollectionRef.addSnapshotListener{querySnapshot,firebaseFirestoreException ->
            firebaseFirestoreException?.let {
                Toast.makeText(this,it.message,Toast.LENGTH_LONG).show()
                return@addSnapshotListener
            }
            querySnapshot?.let {
                val sb=StringBuilder()
                for (document in it){
                    //also we need to make default constructor of Person class otherwise it will crash
                    val person=document.toObject<Person>()  //or we use Person:: class.java also
                    sb.append("$person\n")
                }
                tvPerson.text=sb.toString()
            }
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
    private fun retrievePerson(){
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val querySnapshot=personCollectionRef.get().await()
                val sb=StringBuilder()
                for (document in querySnapshot.documents){
                    //also we need to make default constructor of Person class otherwise it will crash
                    val person=document.toObject<Person>()  //or we use Person:: class.java also
                    sb.append("$person\n")
                }
                withContext(Dispatchers.Main){
                    tvPerson.text=sb.toString()
                }
            }catch (e:Exception){
                withContext(Dispatchers.Main){
                    Toast.makeText(this@MainActivity,e.message,Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}