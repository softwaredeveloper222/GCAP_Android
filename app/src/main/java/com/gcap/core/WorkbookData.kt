package com.gcap.core

import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.InputStream
import kotlin.math.max
import kotlin.math.min

class WorkbookData(file: InputStream) {

    val sheets: MutableMap<String, MutableMap<String, String>> = mutableMapOf()

    init {
        val wb = XSSFWorkbook(file)
        for (sheetIndex in 0 until wb.numberOfSheets) {
            val sheet = wb.getSheetAt(sheetIndex)
            val sheetName = sheet.sheetName.uppercase()
            val cells: MutableMap<String, String> = mutableMapOf()

            for (row in sheet) {
                for (cell in row) {
                    val cellRef = cell.address.formatAsString()
                    val text = when (cell.cellType) {
                        org.apache.poi.ss.usermodel.CellType.STRING -> cell.stringCellValue
                        org.apache.poi.ss.usermodel.CellType.NUMERIC -> cell.numericCellValue.toString()
                        org.apache.poi.ss.usermodel.CellType.BOOLEAN -> cell.booleanCellValue.toString()
                        org.apache.poi.ss.usermodel.CellType.FORMULA -> cell.cellFormula
                        else -> ""
                    }
                    if (text.isNotEmpty()) {
                        cells[cellRef] = text
                    }
                }
            }
            sheets[sheetName] = cells
        }
        wb.close()
    }

    fun value(sheet: String, address: String): String? {
        return sheets[sheet.uppercase()]?.get(address.uppercase())
    }
}

fun normalizeCellRef(input: String): String {
    val trimmed = input.trim().replace("$", "")
    var letters = StringBuilder()
    var numbers = StringBuilder()
    for (ch in trimmed) {
        when {
            ch.isLetter() -> letters.append(ch)
            ch.isDigit() -> numbers.append(ch)
        }
    }
    return letters.toString().uppercase() + numbers.toString()
}

fun colLettersToIndex(letters: String): Int {
    var result = 0
    for (ch in letters.uppercase()) {
        result = result * 26 + (ch.code - 'A'.code + 1)
    }
    return result - 1
}

fun indexToColLetters(index: Int): String {
    var i = index
    val builder = StringBuilder()
    var current = i
    while (current >= 0) {
        val rem = current % 26
        builder.insert(0, (rem + 'A'.code).toChar())
        current = (current / 26) - 1
    }
    return builder.toString()
}

fun parseCellRef(ref: String): Pair<Int, Int>? {
    val normalized = normalizeCellRef(ref)
    if (normalized.isEmpty()) return null
    val letters = normalized.takeWhile { it.isLetter() }
    val numbers = normalized.dropWhile { it.isLetter() }
    if (letters.isEmpty() || numbers.isEmpty()) return null
    val col = colLettersToIndex(letters)
    val row = numbers.toIntOrNull()?.minus(1) ?: return null
    return Pair(col, row)
}

fun addressFrom(col: Int, row: Int): String {
    return "${indexToColLetters(col)}${row + 1}"
}

fun expandRange(startRef: String, endRef: String): List<String>? {
    val s = parseCellRef(startRef) ?: return null
    val e = parseCellRef(endRef) ?: return null

    val startRow = min(s.second, e.second)
    val endRow = max(s.second, e.second)
    val startCol = min(s.first, e.first)
    val endCol = max(s.first, e.first)

    val addresses = mutableListOf<String>()
    for (r in startRow..endRow) {
        for (c in startCol..endCol) {
            addresses.add(addressFrom(c, r))
        }
    }
    return addresses
}

fun indirect(cellRefContainingAddress: String, workbook: WorkbookData, defaultSheet: String = "Sheet1"): String? {
    val parsed = parseCellRef(cellRefContainingAddress) ?: return null
    val addr = addressFrom(parsed.first, parsed.second)
    return workbook.value(defaultSheet, addr)
}

fun vlookup(
    lookupValue: String,
    sheet: String,
    startRef: String,
    endRef: String,
    colIndex: Int,
    workbook: WorkbookData?
): String? {
    val addresses = expandRange(startRef, endRef) ?: return null
    if (addresses.isEmpty()) return null

    val rows = mutableListOf<List<String>>()
    val firstParsed = parseCellRef(addresses[0]) ?: return null
    var currentRow = firstParsed.second
    var rowAddrs = mutableListOf<String>()

    for (addr in addresses) {
        val p = parseCellRef(addr) ?: continue
        if (p.second != currentRow) {
            rows.add(rowAddrs)
            rowAddrs = mutableListOf()
            currentRow = p.second
        }
        rowAddrs.add(addr)
    }
    if (rowAddrs.isNotEmpty()) rows.add(rowAddrs)

    val candidates = mutableListOf<Pair<Double, List<String>>>()
    for (r in rows) {
        val firstAddr = r.firstOrNull() ?: continue
        val valString = workbook?.value(sheet, firstAddr)
        val num = valString?.toDoubleOrNull()
        if (num != null) {
            candidates.add(Pair(num, r))
        }
    }

    if (candidates.isEmpty()) return null

    candidates.sortBy { it.first }

    val lookupDouble = lookupValue.toDoubleOrNull() ?: return null
    var bestMatch: List<String>? = null
    var bestValue = Double.NEGATIVE_INFINITY
    for (c in candidates) {
        if (c.first <= lookupDouble && c.first > bestValue) {
            bestValue = c.first
            bestMatch = c.second
        }
    }

    val matchedRow = bestMatch ?: return null

    val idx = colIndex - 1
    if (idx < 0 || idx >= matchedRow.size) return null

    return workbook?.value(sheet, matchedRow[idx])
}
