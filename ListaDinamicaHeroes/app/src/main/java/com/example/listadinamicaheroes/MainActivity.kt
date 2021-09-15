package com.example.listadinamicaheroes

import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.example.listadinamicaheroes.data.Admin
import com.example.listadinamicaheroes.data.Product
import com.example.listadinamicaheroes.data.SaleStand
import com.example.listadinamicaheroes.databinding.ActivityMainBinding
import com.example.listadinamicaheroes.fragments.CreateSaleStandFragment
import com.example.listadinamicaheroes.fragments.UpdateSaleProduct
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
    private lateinit var snapshotDataBaseAdmin: DataSnapshot

    val database = Firebase.database
    val myRef = database.getReference("Sale")
    val myRefAdmin = database.getReference("Admin")
    val addFragment = FragmentAddSalesUnit()
    val createSaleStandFragment = CreateSaleStandFragment()
    val customSaleProduct = UpdateSaleProduct()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshotDataBase = snapshot
            }
            override fun onCancelled(error: DatabaseError) {
            }
        })

        myRefAdmin.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshotDataBaseAdmin = snapshot
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

        setUpBottonNav()

        var positiveButton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
        positiveButton.setOnClickListener {
            val userName = dialogView.findViewById<EditText>(R.id.etUserName).text.toString()
            val password = dialogView.findViewById<EditText>(R.id.etPassword).text.toString()
            val role = dialogView.findViewById<Spinner>(R.id.spinner2).selectedItem.toString()

            while (role == "Vendedor" && !snapshotDataBase.exists()) {
                Toast.makeText(
                    this,
                    "Esperando a la base de datos",
                    Toast.LENGTH_SHORT
                ).show()
            }

            while (role == "Administrador" && !snapshotDataBaseAdmin.exists()) {
                Toast.makeText(
                    this,
                    "Esperando a la base de datos",
                    Toast.LENGTH_SHORT
                ).show()
            }

            if (role == "Administrador") {
                val value = snapshotDataBaseAdmin.child(userName)

                if (value.getValue<Admin>() != null) {
                    correctPassword = value.getValue<Admin>()?.password.toString()

                    removeAlertDialog(alertDialog, correctPassword, password, role)
                }
            } else {
                val value = snapshotDataBase.child(userName)

                if (value.getValue<SaleStand>() != null) {
                    correctPassword = value.getValue<SaleStand>()?.password.toString()

                    removeAlertDialog(alertDialog, correctPassword, password, role)
                }
            }
        };
    }

    private fun removeAlertDialog(
        alertDialog: AlertDialog,
        correctPassword: String,
        password: String,
        role: String
    ) {
        if (correctPassword == password) {
            Toast.makeText(
                this,
                "Ingresaste exitosamente",
                Toast.LENGTH_LONG
            ).show()

            if (role == "Administrador") {
                Log.d("entre", "entre aca")
                activeFragment = customSaleProduct

                viewBinding.bottomNav.menu.findItem(R.id.item_add).isEnabled = false
                viewBinding.bottomNav.menu.findItem(R.id.item_add).isCheckable = false

                viewBinding.bottomNav.menu.findItem(R.id.item_home).isEnabled = false
                viewBinding.bottomNav.menu.findItem(R.id.item_home).isCheckable = false

            } else {
                Log.d("entre2", "entre aca2")

                activeFragment = createSaleStandFragment

                viewBinding.bottomNav.menu.findItem(R.id.item_custom).isEnabled = false
                viewBinding.bottomNav.menu.findItem(R.id.item_custom).isCheckable = false
            }

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
        fragmentManager.beginTransaction()
            .add(R.id.fragmentContainer, customSaleProduct)
            .hide(customSaleProduct)
            .commit()

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
            when (it.itemId) {
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

                R.id.item_custom -> {
                    fragmentManager.beginTransaction()
                        .hide(activeFragment)
                        .show(customSaleProduct)
                        .commit()
                    activeFragment = customSaleProduct
                    true
                }

                else -> false
            }

        }
    }
}
