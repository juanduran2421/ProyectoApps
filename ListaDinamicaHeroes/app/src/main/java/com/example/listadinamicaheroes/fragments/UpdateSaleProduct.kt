package com.example.listadinamicaheroes.fragments

import android.app.Activity
import android.content.ContentResolver
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import com.example.listadinamicaheroes.R
import com.example.listadinamicaheroes.data.Product
import com.example.listadinamicaheroes.data.SaleStand
import com.example.listadinamicaheroes.databinding.FragmentUpdateSaleProductBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class UpdateSaleProduct : Fragment() {

    private val RC_GALLERY = 7
    private val PATH_IMAGENES = "Product"

    private lateinit var mBinding: FragmentUpdateSaleProductBinding
    private var mImageSelecionadaUri: Uri?=null
    private lateinit var mStorageReference: StorageReference

    val database = Firebase.database
    val myRef = database.getReference("Unit")
    val myRefProduct = database.getReference("Product")
    var units: MutableList<String> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment

        mBinding = FragmentUpdateSaleProductBinding.inflate(inflater, container, false)


        return mBinding.root
    }

    fun selectItems (view: View) {
        var spinnermiles =  mBinding.spinner;
        var adapter = ArrayAdapter(view.context ,android.R.layout.simple_spinner_item, units);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnermiles.setAdapter(adapter)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        myRef.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var index = 0
                for (rest:DataSnapshot in snapshot.children) {
                    var value =  rest.getValue().toString()
                    units.add(index, value)

                    index = index +1
                }

                selectItems(view)
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })

        mBinding.btnPublicar.setOnClickListener{publicarImagen()}
        mBinding.btnSeleccionar.setOnClickListener{abrirGaleria()}

        val imageUri = Uri.parse(
            ContentResolver.SCHEME_ANDROID_RESOURCE.toString() +
                    "://" + requireContext().resources.getResourcePackageName(R.mipmap.food)
                    + '/' + requireContext().resources.getResourceTypeName(R.mipmap.food)
                    + '/' + requireContext().resources.getResourceEntryName(R.mipmap.food)
        )

        mBinding.ivPhoto.setImageURI(imageUri)
        mImageSelecionadaUri = imageUri

        mStorageReference = FirebaseStorage.getInstance().reference
    }

    private fun abrirGaleria() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, RC_GALLERY)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if( resultCode == Activity.RESULT_OK) {
            if (requestCode == RC_GALLERY) {
                mImageSelecionadaUri = data?.data
                mBinding.ivPhoto.setImageURI(mImageSelecionadaUri)
            }
        }
    }

    private fun publicarImagen() {
        mBinding.progressBar.visibility = View.VISIBLE
        val storageReference = mStorageReference.child(PATH_IMAGENES).child(mBinding.etName.text.toString())
        if (mImageSelecionadaUri != null && mBinding.etName.text.toString() != "" && mBinding.etName2.text.toString() != "" && mBinding.spinner.selectedItem.toString() != "") {
            var product = Product(mBinding.etName.text.toString(),mBinding.etName2.text.toString().toInt(), mBinding.spinner.selectedItem.toString())
            myRefProduct.child(mBinding.etName.text.toString()).setValue(product)

            storageReference.putFile(mImageSelecionadaUri!!)
                .addOnProgressListener {
                    val progress = (100 * it.bytesTransferred / it.totalByteCount).toDouble()
                    mBinding.progressBar.progress = progress.toInt()
                    mBinding.tvPasoAdd.text = "$progress"
                }
                .addOnCompleteListener {
                    mBinding.progressBar.visibility = View.INVISIBLE
                }
                .addOnSuccessListener {
                    Snackbar.make(mBinding.root, "Se subio la imagen", Snackbar.LENGTH_SHORT).show()
                    mBinding.etName.text.clear()
                    mBinding.etName2.text.clear()
                }
                .addOnFailureListener {
                    Snackbar.make(mBinding.root, "Debes intentar más tarde", Snackbar.LENGTH_SHORT).show()
                }
        } else {
            Snackbar.make(mBinding.root, "Faltan campos por llenar", Snackbar.LENGTH_SHORT).show()
        }
    }
}


