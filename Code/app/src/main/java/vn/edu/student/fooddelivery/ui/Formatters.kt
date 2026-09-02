package vn.edu.student.fooddelivery.ui

import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val vietnameseLocale = Locale("vi", "VN")

fun formatCurrency(value: Double): String =
    NumberFormat.getCurrencyInstance(vietnameseLocale).format(value)

fun formatDateTime(timestamp: Long): String =
    SimpleDateFormat("dd/MM/yyyy · HH:mm", vietnameseLocale).format(Date(timestamp))

fun shortId(id: String): String = id.take(8).uppercase(vietnameseLocale)
