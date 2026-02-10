package com.gcap.excel

import android.content.Context
import com.gcap.core.GlobalStorage.PSIA_rows
import com.gcap.core.GlobalStorage.PSIF_rows
import com.gcap.core.GlobalStorage.PSIG_rows
import com.gcap.core.GlobalStorage.Superheat_rows
import com.gcap.core.GlobalStorage.wb
import com.gcap.core.WorkbookData
import com.gcap.core.models.PSIAExcelRow
import com.gcap.core.models.PSIFExcelRow
import com.gcap.core.models.PSIGExcelRow
import com.gcap.core.models.SuperheatExcelRow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.apache.poi.ss.usermodel.CellType
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.IOException


object ExcelDataModel {

    fun formatValue(value: String?): String? {
        val v = value ?: return null
        val num = v.toDoubleOrNull() ?: return v

        var str = String.format("%.4f", num)

        while (str.contains(".") && (str.endsWith("0") || str.endsWith("."))) {
            str = str.dropLast(1)
        }
        return str
    }

    private suspend fun readExcel(context: Context, fileName: String): List<List<String>> {
        return withContext(Dispatchers.IO) {
            val result = mutableListOf<List<String>>()
            try {
                val assetFiles = context.assets.list("") ?: arrayOf()
                if (!assetFiles.contains(fileName)) {
                    withContext(Dispatchers.Main) {
//                        Toast.makeText(context, "File $fileName not found in assets!", Toast.LENGTH_LONG).show()
                    }
                    return@withContext result
                }

                context.assets.open(fileName).use { inputStream ->
                    val workbook = try {
                        XSSFWorkbook(inputStream)
                    } catch (e: Exception) {
                        withContext(Dispatchers.Main) {
//                            Toast.makeText(context, "Error reading XLSX: ${e.message}", Toast.LENGTH_LONG).show()
                        }
                        return@withContext result
                    }

                    val sheet = workbook.getSheetAt(0)
                    for (row in sheet) {
                        val rowValues = mutableListOf<String>()
                        for (cell in row) {
                            rowValues.add(
                                when (cell.cellType) {
                                    CellType.STRING -> cell.stringCellValue
                                    CellType.NUMERIC -> cell.numericCellValue.toString()
                                    CellType.BOOLEAN -> cell.booleanCellValue.toString()
                                    CellType.BLANK -> ""
                                    else -> cell.toString()
                                }
                            )
                        }
                        result.add(rowValues)
                    }
                    workbook.close()
                }

            } catch (e: IOException) {
                withContext(Dispatchers.Main) {
//                    Toast.makeText(context, "IOException: ${e.message}", Toast.LENGTH_LONG).show()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
//                    Toast.makeText(context, "Unexpected error: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }

            result
        }
    }

    // ============================================================
    //                      PSIG DATA MODEL
    // ============================================================
    suspend fun loadExcel_PSIG(context: Context) {
        val rows = readExcel(context, "PSIG.xlsx")

        PSIG_rows = rows.mapNotNull {
            if (it.size >= 7) PSIGExcelRow(
                PSIG = it[0],
                PISA = it[1],
                SVL = it[2],
                SVV = it[3],
                DL = it[4],
                DV = it[5],
                TDF = it[6]
            ) else null
        }.toMutableList()
    }

    fun PSIG_vlookup(
        lookupValue: String,
        tableArray: List<PSIGExcelRow>,
        columnIndex: Int
    ): String? {

        val lookupDouble = lookupValue.trim().toDoubleOrNull()
            ?: return null

        // Convert keys to numbers and sort ascending
        val sortedRows = tableArray
            .mapNotNull { row ->
                val key = row.PSIG.trim().toDoubleOrNull()
                if (key != null) key to row else null
            }
            .sortedBy { it.first }

        if (sortedRows.isEmpty()) return null

        for ((key, row) in sortedRows) {
            if (key == lookupDouble) {
                return formatValue(getPSIGColumnValue(row, columnIndex))
            }
        }

        var bestRow: PSIGExcelRow? = null
        var bestKey: Double? = null

        for ((key, row) in sortedRows) {
            if (key <= lookupDouble) {
                if (bestKey == null || key > bestKey!!) {
                    bestKey = key
                    bestRow = row
                }
            }
        }

        if (bestRow == null) return null

        return formatValue(getPSIGColumnValue(bestRow, columnIndex))
    }

    private fun getPSIGColumnValue(row: PSIGExcelRow, col: Int): String? {
        return when (col) {
            1 -> row.PSIG
            2 -> row.PISA
            3 -> row.SVL
            4 -> row.SVV
            5 -> row.DL
            6 -> row.DV
            7 -> row.TDF
            else -> null
        }
    }

    // ============================================================
    //                      PSIA DATA MODEL
    // ============================================================
    suspend fun loadExcel_PSIA(context: Context) {
        val rows = readExcel(context, "PSIA.xlsx")

        PSIA_rows = rows.mapNotNull {
            if (it.size >= 7) PSIAExcelRow(
                PSIA = it[0],
                PISG = it[1],
                SVL = it[2],
                SVV = it[3],
                DL = it[4],
                DV = it[5],
                TDF = it[6]
            ) else null
        }.toMutableList()
    }

    fun PSIA_vlookup(
        lookupValue: String,
        tableArray: List<PSIAExcelRow>,
        columnIndex: Int
    ): String? {

        val lookup = lookupValue.trim()
        val lookupDouble = lookup.toDoubleOrNull() ?: return null

        val sortedRows = tableArray
            .mapNotNull { row ->
                val key = row.PSIA.trim().toDoubleOrNull()
                if (key != null) key to row else null
            }
            .sortedBy { it.first }

        if (sortedRows.isEmpty()) return null

        for ((key, row) in sortedRows) {
            if (key == lookupDouble) {
                return formatValue(getPSIAColumnValue(row, columnIndex))
            }
        }

        var bestRow: PSIAExcelRow? = null
        var bestKey: Double? = null

        for ((key, row) in sortedRows) {
            if (key <= lookupDouble) {
                if (bestKey == null || key > bestKey!!) {
                    bestKey = key
                    bestRow = row
                }
            }
        }

        if (bestRow == null) return null

        return formatValue(getPSIAColumnValue(bestRow, columnIndex))
    }


    private fun getPSIAColumnValue(row: PSIAExcelRow, col: Int): String? {
        return when (col) {
            1 -> row.PSIA
            2 -> row.PISG
            3 -> row.SVL
            4 -> row.SVV
            5 -> row.DL
            6 -> row.DV
            7 -> row.TDF
            else -> null
        }
    }


    // ============================================================
    //                      PSIF DATA MODEL
    // ============================================================
    suspend fun loadExcel_PSIF(context: Context) {
        val rows = readExcel(context, "PSIF.xlsx")

        PSIF_rows = rows.mapNotNull {
            if (it.size >= 10) PSIFExcelRow(
                TDF = it[0],
                PISG = it[1],
                PSIA = it[2],
                SVL = it[3],
                SVV = it[4],
                DL = it[5],
                DV = it[6],
                EB_Hf = it[7],
                EB_Hg = it[8],
                EB_Sg = it[9]
            ) else null
        }.toMutableList()
    }

    fun PSIF_vlookup(
        lookupValue: String,
        tableArray: List<PSIFExcelRow>,
        columnIndex: Int
    ): String? {

        val lookup = lookupValue.trim()
        val lookupDouble = lookup.toDoubleOrNull() ?: return null

        val sortedRows = tableArray
            .mapNotNull { row ->
                val key = row.TDF.trim().toDoubleOrNull()
                if (key != null) key to row else null
            }
            .sortedBy { it.first }

        for ((key, row) in sortedRows) {
            if (key == lookupDouble) {
                return formatValue(getPSIFColumnValue(row, columnIndex))
            }
        }

        var bestRow: PSIFExcelRow? = null
        var bestKey: Double? = null

        for ((key, row) in sortedRows) {
            if (key <= lookupDouble) {
                if (bestKey == null || key > bestKey!!) {
                    bestKey = key
                    bestRow = row
                }
            }
        }

        val resultRow = bestRow ?: sortedRows.first().second

        return formatValue(getPSIFColumnValue(resultRow, columnIndex))
    }


    private fun getPSIFColumnValue(row: PSIFExcelRow, col: Int): String? {
        return when (col) {
            1 -> row.TDF
            2 -> row.PISG
            3 -> row.PSIA
            4 -> row.SVL
            5 -> row.SVV
            6 -> row.DL
            7 -> row.DV
            8 -> row.EB_Hf
            9 -> row.EB_Hg
            10 -> row.EB_Sg
            else -> null
        }
    }


    // ============================================================
    //                  SUPERHEAT DATA MODEL
    // ============================================================
    suspend fun loadExcel_Superheat(context: Context) {
        val rows = readExcel(context, "Superheat.xlsx")

        Superheat_rows = rows.mapNotNull {
            if (it.size >= 2) SuperheatExcelRow(
                PSIG = it[0],
                Temp = it[1]
            ) else null
        }.toMutableList()
    }

    fun Superheat_vlookup(
        lookupValue: String,
        tableArray: List<SuperheatExcelRow>,
        columnIndex: Int
    ): String? {

        val lookup = lookupValue.trim()
        val lookupDouble = lookup.toDoubleOrNull() ?: return null

        val sortedRows = tableArray
            .mapNotNull { row ->
                val key = row.PSIG.trim().toDoubleOrNull()
                if (key != null) key to row else null
            }
            .sortedBy { it.first }

        for ((key, row) in sortedRows) {
            if (key == lookupDouble) {
                return formatValue(getSuperheatColumnValue(row, columnIndex))
            }
        }

        var bestRow: SuperheatExcelRow? = null
        var bestKey: Double? = null

        for ((key, row) in sortedRows) {
            if (key <= lookupDouble) {
                if (bestKey == null || key > bestKey!!) {
                    bestKey = key
                    bestRow = row
                }
            }
        }

        val resultRow = bestRow ?: sortedRows.first().second

        return formatValue(getSuperheatColumnValue(resultRow, columnIndex))
    }


    private fun getSuperheatColumnValue(row: SuperheatExcelRow, col: Int): String? {
        return when (col) {
            1 -> row.PSIG
            2 -> row.Temp
            else -> null
        }
    }

    suspend fun loadPressureExcel(context: Context) {
        withContext(Dispatchers.IO) {
            try {
                val file = context.assets.open("PE.xlsx")
                val workbook = WorkbookData(file)
                withContext(Dispatchers.Main) {
                    wb = workbook
                }

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
