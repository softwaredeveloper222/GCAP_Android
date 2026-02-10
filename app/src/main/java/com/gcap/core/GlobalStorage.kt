package com.gcap.core

import com.gcap.core.models.PSIAExcelRow
import com.gcap.core.models.PSIFExcelRow
import com.gcap.core.models.PSIGExcelRow
import com.gcap.core.models.SuperheatExcelRow

object GlobalStorage {
    var PSIG_rows: MutableList<PSIGExcelRow> = mutableListOf()
    var PSIA_rows: MutableList<PSIAExcelRow> = mutableListOf()
    var PSIF_rows: MutableList<PSIFExcelRow> = mutableListOf()

    var wb: WorkbookData? = null

    var Superheat_rows: MutableList<SuperheatExcelRow> = mutableListOf()
}