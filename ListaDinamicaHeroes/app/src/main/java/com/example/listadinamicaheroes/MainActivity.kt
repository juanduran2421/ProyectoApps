package com.example.listadinamicaheroes

import android.content.DialogInterface
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.example.listadinamicaheroes.data.SaleStand
import com.example.listadinamicaheroes.databinding.ActivityMainBinding
import com.example.listadinamicaheroes.fragments.CreateSaleStandFragment
import com.example.listadinamicaheroes.fragments.FragmentAddSalesUnit
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {

    private lateinit var linearLayoutManager: RecyclerView.LayoutManager
    private lateinit var viewBinding: ActivityMainBinding
    private lateinit var fragmentManager: FragmentManager;
    private lateinit var activeFragment: Fragment
    private lateinit var snapshotDataBase: DataSnapshot

    val database = Firebase.database
    val myRef = database.getReference("Sale")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        myRef.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshotDataBase = snapshot
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })

        var correctPassword = ""

        val dialogView = layoutInflater.inflate(R.layout.dialog_register, null)

        var alertDialog = MaterialAlertDialogBuilder(this)
            .setView(dialogView)
            .setCancelable(false)
            .setPositiveButton("Entrar", null)
            .show();

        var positiveButton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
        positiveButton.setOnClickListener {
            val userName = dialogView.findViewById<EditText>(R.id.etUserName).text.toString()
            val password = dialogView.findViewById<EditText>(R.id.etPassword).text.toString()

            while (!snapshotDataBase.exists()) {
                Toast.makeText(
                        this,
                        "Esperando a la base de datos",
                        Toast.LENGTH_SHORT
                ).show()
            }

            val value = snapshotDataBase.child(userName)

            if (value.getValue<SaleStand>()!= null) {
                correctPassword = value.getValue<SaleStand>()?.password.toString()

                removeAlertDialog(alertDialog, correctPassword, password)
            }
        };

        setUpBottonNav()
    }

    private fun removeAlertDialog(alertDialog: AlertDialog, correctPassword: String, password: String){
        if (correctPassword == password) {
            Toast.makeText(
                this,
                "Ingresaste exitosamente",
                Toast.LENGTH_LONG
            ).show()

            alertDialog.dismiss()

            fragmentManager.beginTransaction()
                .show(activeFragment)
                .commit()
        } else {
            Toast.makeText(
                this,
                "Contraseña incorrecta",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun setUpBottonNav() {
        fragmentManager = supportFragmentManager

        val addFragment = FragmentAddSalesUnit()
        val createSaleStandFragment = CreateSaleStandFragment()

        activeFragment = createSaleStandFragment

        fragmentManager.beginTransaction()
            .add(R.id.fragmentContainer, createSaleStandFragment)
            .hide(createSaleStandFragment)
            .commit()

        fragmentManager.beginTransaction()
            .add(R.id.fragmentContainer, addFragment)
            .hide(addFragment)
            .commit()

        viewBinding.bottomNav.setOnNavigationItemSelectedListener {
            when(it.itemId) {
                R.id.item_home -> {
                    fragmentManager.beginTransaction()
                        .hide(activeFragment)
                        .show(createSaleStandFragment)
                        .commit()
                    activeFragment = createSaleStandFragment
                    true
                }

                R.id.item_add -> {
                    fragmentManager.beginTransaction()
                        .hide(activeFragment)
                        .show(addFragment)
                        .commit()
                    activeFragment = addFragment
                    true
                }

                else -> false
            }

        }
    }
}
