package com.example.listadinamicaheroes.data

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class Admin (
                      var password:String?=null,
                      var username:String?=null
                  )