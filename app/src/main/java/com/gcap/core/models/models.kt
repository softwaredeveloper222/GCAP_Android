package com.gcap.core.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.util.UUID

data class ValveSubItem(
    val id: String,
    val category_id: String,
    val sub_category_id: String,
    val name: String,
    val image: String,
    val etc: String
)

data class ValveItem(
    val id: String,
    val category_id: String,
    val name: String,
    val image: String,
    val list: List<ValveSubItem>
)

data class ChartItem(
    val id: String,
    val category_id: String,
    val sub_category_id: String,
    val name: String,
    val image: String,
    val etc: String
)

data class AnimationItem(
    val id: String,
    val category_id: String,
    val sub_category_id: String,
    val name: String,
    val image: String,
    val etc: String
)
@Parcelize
data class IndustryItem(
    val id: String,
    val category_id: String,
    val sub_category_id: String,
    val name: String,
    val image: String,
    val cperson: String,
    val phone: String,
    val email: String,
    val website: String,
    val address: String,
    val about: String
) : Parcelable

data class ContactInfoItem(
    val id: String,
    val email: String,
    val phone: String,
    val address: String,
    val website: String
)

data class ContactusItem(
    val name: String,
    val email: String,
    val message: String
)

data class ContactUsResponse(
    val success: Boolean,
    val message: String
)

data class PSIGExcelRow(
    val PSIG: String,
    val PISA: String,
    val SVL: String,
    val SVV: String,
    val DL: String,
    val DV: String,
    val TDF: String
)

data class PSIAExcelRow(
    val PSIA: String,
    val PISG: String,
    val SVL: String,
    val SVV: String,
    val DL: String,
    val DV: String,
    val TDF: String
)

data class PSIFExcelRow(
    val TDF: String,
    val PISG: String,
    val PSIA: String,
    val SVL: String,
    val SVV: String,
    val DL: String,
    val DV: String,
    val EB_Hf: String,
    val EB_Hg: String,
    val EB_Sg: String
)

data class SuperheatExcelRow(
    val PSIG: String,
    val Temp: String
)

data class RefrigerationParameter(
    val id: String = UUID.randomUUID().toString(),
    val parameter: String,
    val value: String
)

data class ThermodynamicPoint(
    val id: String = UUID.randomUUID().toString(),
    val point: String,
    val temperatureF: Double,
    val psia: Double,
    val psig: Double,
    val enthalpy: Double,
    val entropy: Double? = null,
    val rowColor: RowColor
) {
    enum class RowColor {
        LIGHT_BLUE,
        ORANGE,
        YELLOW,
        MEDIUM_BLUE;

        fun mainColorHex(): String = when (this) {
            LIGHT_BLUE -> "#00CCFF"
            ORANGE -> "#FF9900"
            YELLOW -> "#FFCC00"
            MEDIUM_BLUE -> "#00FFFF"
        }

        fun entropyColorHex(): String = when (this) {
            LIGHT_BLUE -> "#005C73"
            ORANGE -> "#FF9900"
            YELLOW -> "#FFCC00"
            MEDIUM_BLUE -> "#00FFFF"
        }

        fun mainColorArgbInt(): Int = parseHexToInt(mainColorHex())
        fun entropyColorArgbInt(): Int = parseHexToInt(entropyColorHex())

        private fun parseHexToInt(hex: String): Int {
            return try {
                val clean = hex.removePrefix("#")
                when (clean.length) {
                    6 -> (0xFF shl 24) or clean.toInt(16)
                    8 -> clean.toLong(16).toInt()
                    else -> (0xFF shl 24) or clean.toInt(16)
                }
            } catch (t: Throwable) {
                (0xFF shl 24) or 0xFFFFFF
            }
        }
    }
}

class CrosshatchPattern(
    private val spacing: Double = 3.0
) {
    data class Point(val x: Double, val y: Double)
    data class Line(val start: Point, val end: Point)

    fun generateLines(width: Double, height: Double): List<Line> {
        val lines = mutableListOf<Line>()

        var x = -height
        while (x < width + height) {
            val start = Point(x, 0.0)
            val end = Point(x + height, height)
            lines.add(Line(start, end))
            x += spacing
        }

        x = width + height
        while (x > -height) {
            val start = Point(x, 0.0)
            val end = Point(x - height, height)
            lines.add(Line(start, end))
            x -= spacing
        }

        return lines
    }
}