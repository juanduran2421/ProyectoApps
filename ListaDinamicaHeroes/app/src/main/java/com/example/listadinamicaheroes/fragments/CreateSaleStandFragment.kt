package com.example.listadinamicaheroes.fragments

import android.content.ContentValues.TAG
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.listadinamicaheroes.R
import com.example.listadinamicaheroes.data.SaleStand
import com.example.listadinamicaheroes.databinding.FragmentCreateSaleStandBinding
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import java.lang.Exception

class CreateSaleStandFragment : Fragment() {

    val database = Firebase.database
    private lateinit var mBinding: FragmentCreateSaleStandBinding
    val myRef = database.getReference("Sale")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        mBinding = FragmentCreateSaleStandBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mBinding.button2.setOnClickListener{saveUser(view)}
    }

    private fun saveUser(view: View) {
        var saleStand = SaleStand(mBinding.eText3.text.toString(),mBinding.eText4.text.toString(),mBinding.eText2.text.toString(),mBinding.eText1.text.toString(),mBinding.eText.text.toString())

        myRef.child(mBinding.eText.text.toString()).setValue(saleStand)

        Toast.makeText(view.context, "Puesto de venta creado exitosamente", Toast.LENGTH_SHORT).show()

        val mIntent = Intent(Intent.ACTION_SEND)

        mIntent.data = Uri.parse("mailto:")
        mIntent.type = "text/plain"

        mIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf(mBinding.eText4.text.toString().trim()))
        mIntent.putExtra(Intent.EXTRA_SUBJECT, "Nuevo usuario creado")
        mIntent.putExtra(Intent.EXTRA_TEXT, "Bienvenido "+ mBinding.eText.text.toString()+ " tu usuario ha sido creado")

        try {
            startActivity(Intent.createChooser(mIntent, "Elegir correo"))

        }catch (e:Exception) {
            Log.d("Fallo al enviar el correo", e.message.toString())
        }

        mBinding.eText.text.clear()
        mBinding.eText1.text.clear()
        mBinding.eText2.text.clear()
        mBinding.eText3.text.clear()
        mBinding.eText4.text.clear()
    }
}

