package com.mrdevs.foodorder.tool

import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class Formatter {

    companion object {
        fun currency(number: Int): String {
            return NumberFormat.getCurrencyInstance(Locale("id", "ID")).format(number)
        }

        fun date(timestamp: Long): String {
            return SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH).format(Date(timestamp))
        }
    }
}