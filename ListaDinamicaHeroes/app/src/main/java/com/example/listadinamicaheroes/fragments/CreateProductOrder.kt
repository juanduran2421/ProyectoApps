package com.example.listadinamicaheroes.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import com.example.listadinamicaheroes.data.Product
import com.example.listadinamicaheroes.databinding.FragmentCreateProductOrderBinding
import com.example.listadinamicaheroes.data.Order
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase


class CreateProductOrder : Fragment() {
    private lateinit var mBinding: FragmentCreateProductOrderBinding

    val database = Firebase.database
    val myRefProduct = database.getReference("Product")
    val myRefClient = database.getReference("Client")
    val myRefOrder = database.getReference("Order")
    var units: MutableList<Product> = mutableListOf()
    var unitsString: MutableList<String> = mutableListOf()
    var address = ""
    var cellphone = ""
    var email = ""
    var clientId = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        mBinding = FragmentCreateProductOrderBinding.inflate(inflater, container, false)

        return mBinding.root
    }

    fun selectItems (view: View) {
        var spinnermiles =  mBinding.spinnerOrder;
        var adapter = ArrayAdapter(view.context ,android.R.layout.simple_spinner_item, unitsString);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnermiles.setAdapter(adapter)

        mBinding.spinnerOrder.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parentView: AdapterView<*>?,
                selectedItemView: View?,
                position: Int,
                id: Long
            ) {
                mBinding.textView9.text = units[position].unit
            }

            override fun onNothingSelected(parentView: AdapterView<*>?) {
                // your code here
            }
        })

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        myRefProduct.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var index = 0
                for (rest: DataSnapshot in snapshot.children) {
                    var value =  rest.getValue<Product>()
                    if (value != null) {
                        units.add(index, value)
                        unitsString.add(index, value.name!!)
                        index = index +1
                    }
                }

                selectItems(view)
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })

        mBinding.button3.setOnClickListener{publicarOrden()}
    }

    override fun setArguments(args: Bundle?) {
        super.setArguments(args)

        val username = arguments?.getString("username").toString()

        val getQuery = myRefClient.child(username).get()

        getQuery.addOnSuccessListener {
            val resultQuery = getQuery.result?.value as HashMap<String, String>
            clientId = username
            address = resultQuery["address"]!!
            cellphone = resultQuery["cellphone"]!!
            email = resultQuery["email"]!!
        }
    }

    private fun publicarOrden() {
        var numberProducts = mBinding.etUnit2.text.toString()
        var listProduct = mBinding.spinnerOrder.selectedItem.toString()
        for(i in 0 until numberProducts.toInt()-1) {
            listProduct = listProduct + "," + mBinding.spinnerOrder.selectedItem.toString()
        }

        val newOrder = Order(clientId,  listProduct, mBinding.spinnerOrder2.selectedItem.toString(), address, cellphone, email)
        myRefOrder.push().setValue(newOrder)
    }
}