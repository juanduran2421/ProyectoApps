package com.example.listadinamicaheroes.fragments

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.listadinamicaheroes.R
import com.example.listadinamicaheroes.databinding.FragmentCustomSaleStandBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class CustomSaleStand : Fragment() {

    private val RC_GALLERY = 7
    private val PATH_IMAGENES = "Imagenes"

    private lateinit var mBinding: FragmentCustomSaleStandBinding
    private var mImageSelecionadaUri: Uri?=null
    private lateinit var mStorageReference: StorageReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        mBinding = FragmentCustomSaleStandBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mBinding.btnPublicar.setOnClickListener{publicarImagen()}
        mBinding.btnSeleccionar.setOnClickListener{abrirGaleria()}

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
                mBinding.tilTitle.visibility = View.VISIBLE
                mBinding.tvPasoAdd.text = "Pon titulo a la imagen"
            }
        }
    }

    private fun publicarImagen() {
        mBinding.progressBar.visibility = View.VISIBLE
        val storageReference = mStorageReference.child(PATH_IMAGENES).child(mBinding.tietTitle.text.toString())
        if (mImageSelecionadaUri != null) {
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
                }
                .addOnFailureListener {
                    Snackbar.make(mBinding.root, "Debes intentar más tarde", Snackbar.LENGTH_SHORT).show()
                }
        }
    }
}