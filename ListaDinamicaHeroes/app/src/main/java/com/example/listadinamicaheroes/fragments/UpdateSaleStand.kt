package com.example.listadinamicaheroes.fragments

import android.app.Activity
import android.content.ContentResolver
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.listadinamicaheroes.R
import com.example.listadinamicaheroes.databinding.FragmentUpdateSaleStandBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference


class UpdateSaleStand : Fragment() {

    private val RC_GALLERY = 7
    private val RC_WORKER = 8
    private val PATH_IMAGENES = "Sale"

    private lateinit var mBinding: FragmentUpdateSaleStandBinding
    private var mImageSelecionadaUri: Uri?= null
    private var mImageSelecionadaUriWorker: Uri?=null
    private lateinit var mStorageReference: StorageReference

    val database = Firebase.database
    val myRef = database.getReference("Sale")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        mBinding = FragmentUpdateSaleStandBinding.inflate(inflater, container, false)

        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding.btnPublicar.setOnClickListener{publicarImagen()}
        mBinding.btnSeleccionar.setOnClickListener{abrirGaleria(RC_GALLERY)}
        mBinding.btnSeleccionar2.setOnClickListener{abrirGaleria(RC_WORKER)}


        // val bitmap: Bitmap = BitmapFactory.decodeFile("../../../../../res/mipmap-xxxhdpi/login.jpg")
        //mBinding.ivPhoto.setImageBitmap(bitmap)

        val imageUri = Uri.parse(
            ContentResolver.SCHEME_ANDROID_RESOURCE.toString() +
                    "://" + requireContext().resources.getResourcePackageName(R.mipmap.avatar)
                    + '/' + requireContext().resources.getResourceTypeName(R.mipmap.avatar)
                    + '/' + requireContext().resources.getResourceEntryName(R.mipmap.avatar)
        )

        mBinding.ivPhoto2.setImageURI(imageUri)
        mImageSelecionadaUriWorker = imageUri



        mStorageReference = FirebaseStorage.getInstance().reference
    }

    private fun abrirGaleria(identifier: Int) {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, identifier)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if( resultCode == Activity.RESULT_OK) {
            if (requestCode == RC_GALLERY) {
                mImageSelecionadaUri = data?.data
                mBinding.ivPhoto.setImageURI(mImageSelecionadaUri)
            }

            if (requestCode == RC_WORKER) {
                mImageSelecionadaUriWorker = data?.data
                mBinding.ivPhoto2.setImageURI(mImageSelecionadaUriWorker)
            }
        }
    }

    override fun setArguments(args: Bundle?) {
        super.setArguments(args)

        val username = arguments?.getString("username").toString()
        val getQuery = myRef.child(username).child("stand").get()

        getQuery.addOnSuccessListener {
            mBinding.etName.setText(getQuery.result?.value.toString())
        }
    }

    private fun publicarImagen() {
        mBinding.progressBar.visibility = View.VISIBLE

        val username = arguments?.getString("username").toString()

        val storageReference = mStorageReference.child(PATH_IMAGENES).child(username).child("Terreno").child(mBinding.etName.text.toString())
        if (mImageSelecionadaUri != null && username != "" && mImageSelecionadaUriWorker!= null && mBinding.etName.text.toString() != "" &&  mBinding.etName2.text.toString() != null) {
//            var product = Product(mBinding.etName.text.toString(),mBinding.etName2.text.toString().toInt(), mBinding.spinner.selectedItem.toString())
//            myRef.child(mBinding.etName.text.toString()).setValue(product)
            myRef.child(username).child("stand").setValue(mBinding.etName.text.toString())

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
                    Snackbar.make(mBinding.root, "Se subio la imagen del terreno", Snackbar.LENGTH_SHORT).show()
                }
                .addOnFailureListener {
                    Snackbar.make(mBinding.root, "Debes intentar más tarde", Snackbar.LENGTH_SHORT).show()
                }

            val storageReference2 = mStorageReference.child(PATH_IMAGENES).child(username).child("Trabajador").child(mBinding.etName2.text.toString())
            storageReference2.putFile(mImageSelecionadaUriWorker!!)
                .addOnSuccessListener {
                    Snackbar.make(mBinding.root, "Se subio la imagen del trabajador", Snackbar.LENGTH_SHORT).show()
                }

        } else {
            Snackbar.make(mBinding.root, "Faltan campos por llenar", Snackbar.LENGTH_SHORT).show()
        }
    }
}


