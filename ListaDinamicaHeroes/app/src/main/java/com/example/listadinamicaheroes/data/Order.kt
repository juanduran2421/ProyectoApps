package com.example.listadinamicaheroes.data

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class Order (
                      var client_id:String?=null,
                      var list_products:String?=null,
                      var delivery_type:String?=null,
                      var address:String?=null,
                      var cellphone:String?=null,
                      var email:String?=null
                      )