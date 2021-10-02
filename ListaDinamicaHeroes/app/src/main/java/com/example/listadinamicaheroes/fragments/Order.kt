package com.example.listadinamicaheroes.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.listadinamicaheroes.data.GestorExcel
import com.example.listadinamicaheroes.data.Order
import com.example.listadinamicaheroes.databinding.FragmentOrderBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import org.apache.poi.hssf.usermodel.HSSFCellStyle
import org.apache.poi.hssf.util.HSSFColor
import org.apache.poi.ss.usermodel.CellStyle
import java.lang.Exception

class Order : Fragment() {
    private lateinit var mBinding: FragmentOrderBinding
    private var excelGestor = GestorExcel()

    val database = Firebase.database
    val myRef = database.getReference("Order")
    val myRefSaleStand = database.getReference("Sale")
    var units: MutableList<Order> = mutableListOf()
    var adminEmail = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        mBinding = FragmentOrderBinding.inflate(inflater, container, false)

        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        myRef.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var index = 0
                for (rest: DataSnapshot in snapshot.children) {
                    var value =  rest.getValue<Order>()
                    var order = Order(value?.client_id, value?.list_products, value?.delivery_type, value?.address, value?.cellphone, value?.email)
                    if (order != null) {
                        units.add(index, order)
                    }

                    index = index +1
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })

        mBinding.button5.setOnClickListener{crearExcel(view)}
    }

    override fun setArguments(args: Bundle?) {
        super.setArguments(args)

        val username = arguments?.getString("username").toString()

        Log.d("entre por aca", username)
        val getQuery = myRefSaleStand.child(username).child("email").get()

        getQuery.addOnSuccessListener {

            Log.d("entre por aca 2", username)
            adminEmail =  getQuery.result?.value.toString()

            Log.d("entre por aca 3", adminEmail)
        }
    }

    private fun crearExcel(view: View) {

        excelGestor.crearLibro();
        excelGestor.crearHoja("FirstRepor")
        excelGestor.crearCellSytle(HSSFColor.BLACK.index, HSSFColor.GREEN.index, HSSFCellStyle.SOLID_FOREGROUND, CellStyle.ALIGN_CENTER)

        var numeroFila = 0
        excelGestor.crearFila(numeroFila)
        numeroFila++

        excelGestor.createCelda("client_id",0)
        excelGestor.createCelda("list_products",1)
        excelGestor.createCelda("delivery_type",2)
        excelGestor.createCelda("address",3)
        excelGestor.createCelda("cellphone",4)
        excelGestor.createCelda("email",5)

        excelGestor.crearCellSytle(HSSFColor.BLACK.index, HSSFColor.WHITE.index, HSSFCellStyle.SOLID_FOREGROUND, CellStyle.ALIGN_CENTER)

        for(unit in units) {
            excelGestor.crearFila(numeroFila)
            numeroFila++
            excelGestor.createCelda(unit.client_id,0)
            excelGestor.createCelda(unit.list_products,1)
            excelGestor.createCelda(unit.delivery_type,2)
            excelGestor.createCelda(unit.address,3)
            excelGestor.createCelda(unit.cellphone,4)
            excelGestor.createCelda(unit.email,5)
        }

        var uri = excelGestor.guardarLibro("reportOrders.xls",view.context)

        val mIntent = Intent(Intent.ACTION_SEND)
        mIntent.data = Uri.parse("mailto:"+adminEmail)
        mIntent.type = "text/plain"

        mIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf<String>(adminEmail))
        mIntent.putExtra(Intent.EXTRA_SUBJECT, "Reporte de ordenes")
        mIntent.putExtra(Intent.EXTRA_TEXT, "Reporte de ordenes generado")
        mIntent.putExtra(Intent.EXTRA_STREAM, uri);

        try {
            startActivity(Intent.createChooser(mIntent, "Elegir correo"))

        }catch (e: Exception) {
            Log.d("Fallo al enviar el correo", e.message.toString())
        }
    }
}