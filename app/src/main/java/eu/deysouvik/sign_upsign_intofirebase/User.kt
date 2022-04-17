package eu.deysouvik.sign_upsign_intofirebase

import java.io.Serializable

data class User(
    val name:String,
    val email:String,
    val password:String
):Serializable
