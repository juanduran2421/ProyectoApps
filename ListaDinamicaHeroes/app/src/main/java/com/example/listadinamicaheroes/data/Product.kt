package com.example.listadinamicaheroes.data

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class Product (
                      var name:String?=null,
                      var price:Int?=null,
                      var unit:String?=null
                      )