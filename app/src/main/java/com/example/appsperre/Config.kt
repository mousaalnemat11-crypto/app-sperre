package com.example.appsperre

import android.content.Context

object Config {
    private val defaults = listOf("ماما", "بابا", "")

    fun get(ctx: Context, i: Int): String =
        ctx.getSharedPreferences("cfg", Context.MODE_PRIVATE).getString("c$i", defaults[i]) ?: ""

    fun allowed(ctx: Context): List<String> =
        (0..2).map { norm(get(ctx, it)) }.filter { it.isNotEmpty() }

    fun norm(s: String): String = s.filter {
        it.code !in 0x200E..0x200F && it.code !in 0x202A..0x202E && it.code !in 0x2066..0x2069
    }.trim()
}
