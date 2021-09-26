package com.example.listadinamicaheroes.data


import android.content.ContentValues
import android.content.Context
import android.util.Log
import android.widget.Toast
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import org.apache.poi.ss.usermodel.*
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.lang.Exception

class GestorExcel() {
    private lateinit var libro: Workbook
    private lateinit var hoja: Sheet
    private lateinit var fila: Row
    private lateinit var celda: Cell
    private lateinit var cellStyle: CellStyle

    fun crearLibro() {
        libro = HSSFWorkbook()
    }

    fun crearHoja(nombreHoja: String?) {
        hoja = libro.createSheet(nombreHoja)
    }

    fun crearFila(numeroFila: Int) {
        fila = hoja.createRow(numeroFila)
    }

    fun crearCellSytle(colorFondo: Short, colorTexto: Short, patron: Short, alineacion: Short) {
        cellStyle = libro.createCellStyle()
        cellStyle.setFillBackgroundColor(colorFondo)
        cellStyle.setFillForegroundColor(colorTexto)
        cellStyle.setFillPattern(patron)
        cellStyle.setAlignment(alineacion)
    }

    fun createCelda(valor: String?, numero: Int) {
        celda = fila.createCell(numero)
        celda.setCellValue(valor)
        celda.setCellStyle(cellStyle)
    }

    fun createCelda(valor: Double, numero: Int) {
        celda = fila.createCell(numero)
        celda.setCellValue(valor)
        celda.setCellStyle(cellStyle)
    }

    fun guardarLibro(fileName: String?, context: Context?): Boolean {
        var isSuccess: Boolean
        val file = File(context?.getExternalFilesDir(null), fileName)
        var fileOutputStream: FileOutputStream? = null
        try {
            fileOutputStream = FileOutputStream(file)
            libro!!.write(fileOutputStream)
            Log.e(ContentValues.TAG, "Writing file$file")

            Toast.makeText(
                context,
                "Exito al crear el excel",
                Toast.LENGTH_LONG
            ).show()
            isSuccess = true
        } catch (e: FileNotFoundException) {

            Toast.makeText(
                context,
                "Fallo al crear el excel",
                Toast.LENGTH_LONG
            ).show()

            Log.e(ContentValues.TAG, "Failed to save file due to Exception: ", e)
            isSuccess = false
        } catch (e: IOException) {
            Log.e(ContentValues.TAG, "Error writing Exception: ", e)
            isSuccess = false
        } finally {
            try {
                fileOutputStream?.close()
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }
        return isSuccess
    }


}