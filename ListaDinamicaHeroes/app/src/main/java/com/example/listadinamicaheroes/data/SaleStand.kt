package com.example.listadinamicaheroes.data

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class SaleStand (
                      var cellphone:String?=null,
                      var email:String?=null,
                      var password:String?=null,
                      var stand:String?=null,
                      var username:String?=null
                      )