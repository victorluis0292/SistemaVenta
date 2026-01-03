package com.example.spacevi

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val btnGuardar:Button=findViewById(R.id.btnGuardar)

        btnGuardar.setOnClickListener{

            val campoNombre:EditText=findViewById(R.id.txtID)
            val campoApellido:EditText=findViewById(R.id.txtNombre)
            val campoTelefono:EditText=findViewById(R.id.txtTelefono)

            val nombre:String=campoNombre.text.toString()
            val apellido:String=campoApellido.text.toString()
            val telefono:Int=campoTelefono.text.toString().toInt()


            saveFirestore(nombre,apellido,telefono)
        }
    }
    fun saveFirestore(nombre:String, apellido:String, telefono: Int){
        val db=FirebaseFirestore.getInstance()
        val user:MutableMap<String,Any> =HashMap()
        user["nombre"]=nombre
        user["apellido"]=apellido
        user["telefono"]=telefono

        db.collection("usuarios")
            .add(user)
            .addOnSuccessListener{
                Toast.makeText(this@MainActivity, "exito", Toast.LENGTH_LONG).show()
            }
            .addOnFailureListener{
                Toast.makeText(this@MainActivity, "no hay exito", Toast.LENGTH_LONG).show()

            }
    }
}