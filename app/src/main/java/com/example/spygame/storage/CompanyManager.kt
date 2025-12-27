package com.example.spygame.storage

import android.content.Context
import com.example.spygame.data.Company
import com.google.gson.Gson
import java.io.File

object CompanyManager {
    private const val DIR_NAME = "companies"
    private val gson = Gson()

    private fun dir(ctx: Context): File {
        val d = File(ctx.filesDir, DIR_NAME)
        if (!d.exists()) d.mkdirs()
        return d
    }

    fun saveCompany(ctx: Context, company: Company): Boolean {
        return try {
            val filename = "${company.name.trim()}.json"
            val f = File(dir(ctx), filename)
            f.writeText(gson.toJson(company))
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    fun listCompanies(ctx: Context): List<String> {
        return dir(ctx).listFiles()
            ?.mapNotNull { it.nameWithoutExtension.takeIf { it.isNotBlank() } }
            ?.sorted()
            ?: emptyList()
    }

    fun loadCompany(ctx: Context, name: String): Company? {
        val f = File(dir(ctx), "${name.trim()}.json")
        if (!f.exists()) return null
        val json = f.readText()
        return try {
            gson.fromJson(json, Company::class.java)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun deleteCompany(ctx: Context, name: String): Boolean {
        val f = File(dir(ctx), "${name.trim()}.json")
        return if (f.exists()) f.delete() else false
    }

    fun exists(ctx: Context, name: String): Boolean {
        return File(dir(ctx), "${name.trim()}.json").exists()
    }
}
