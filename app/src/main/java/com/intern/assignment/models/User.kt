package com.intern.assignment.models

class User {
    val name: String
    var number: String? = null
    val email: String
    var password: String? = null
    val signInMethod: String

    constructor(
        name: String,
        number: String?,
        email: String,
        password: String?,
        signInMethod: String
    ) {
        this.name = name
        this.number = number
        this.email = email
        this.password = password
        this.signInMethod = signInMethod
    }

    constructor(name: String, email: String, signInMethod: String) {
        this.name = name
        this.email = email
        this.signInMethod = signInMethod
    }
}