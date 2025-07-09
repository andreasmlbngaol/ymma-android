package com.andreasmlbngaol.ymma.utils

import java.io.File
import java.util.Properties

object Env {
    fun init() {
        val localPropsFile = File("local.properties")
        if (localPropsFile.exists()) {
            val localProps = Properties()
            localProps.load(localPropsFile.inputStream())

            localProps.forEach { key, value ->
                println("Setting property: $key = $value")
                System.setProperty(key.toString(), value.toString())
            }
        } else {
            throw Exception("File local.properties not found")
        }
    }
}