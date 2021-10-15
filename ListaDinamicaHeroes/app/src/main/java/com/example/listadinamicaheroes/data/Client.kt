package com.example.listadinamicaheroes.data

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class Client (
                      var password:String?=null,
                      var username:String?=null,
                      var address:String?=null,
                      var cellphone:String?=null,
                      var email:String?=null
                  )