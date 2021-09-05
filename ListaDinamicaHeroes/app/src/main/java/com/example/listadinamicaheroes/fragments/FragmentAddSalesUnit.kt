package com.example.listadinamicaheroes.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.listadinamicaheroes.R
import com.example.listadinamicaheroes.data.SaleStand
import com.example.listadinamicaheroes.databinding.AddSalesUnitBinding
import com.example.listadinamicaheroes.databinding.FragmentAddSalesUnitBinding
import com.example.listadinamicaheroes.databinding.FragmentCreateSaleStandBinding
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class FragmentAddSalesUnit : Fragment() {
    val database = Firebase.database
    private lateinit var mBinding: FragmentAddSalesUnitBinding
    val myRef = database.getReference("Unit")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        mBinding = FragmentAddSalesUnitBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mBinding.button.setOnClickListener{saveUnit(view)}
    }

    private fun saveUnit(view: View) {
        myRef.push().setValue(mBinding.etUnit.text.toString())

        Toast.makeText(view.context, "Unidad añadida exitosamente", Toast.LENGTH_SHORT).show()

        mBinding.etUnit.text.clear()
    }

}