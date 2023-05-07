package ch.woggle.aethercatch.util

import android.content.Context
import ch.woggle.aethercatch.model.Network
import java.io.BufferedWriter
import java.io.File
import java.io.OutputStream
import java.io.Writer

private const val FILE_NAME = "aether_catch_networks.csv"

fun exportToFile(context: Context, networks: List<Network>): File {
    val directory = context.getExternalFilesDir(null)
    val file = File(directory, FILE_NAME)
    file.outputStream().csvWriter().use { csvWriter ->
        csvWriter.writeEntry("ssid", "bssid", "capabilities")
        networks.forEach { csvWriter.writeEntry(it.ssid, it.bssid, it.capabilities) }
        csvWriter.flush()
    }
    return file
}

private fun OutputStream.csvWriter(separator: String = ","): CsvWriter = CsvWriter(separator, writer())

private class CsvWriter(private val separator: String, writer: Writer) : BufferedWriter(writer) {
    fun writeEntry(vararg values: Any) {
        val line = values.joinToString(separator) { "\"$it\"" }
        write(line)
        newLine()
    }
}