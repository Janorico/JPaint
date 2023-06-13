/*
 * Free and open-source svg-editor.
 * Copyright (C) 2023 Janosch Lion
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package de.janorico.jpaint

import java.io.*
import java.time.LocalDate

object UDM {
    val data = read()

    fun getLoggerFile(): String {
        val now = LocalDate.now()
        var path = getAppData() + "logging/"
        File(path).mkdirs()
        path += "logging-${now.monthValue}-${now.dayOfMonth}-${now.year}.log"
        return path
    }

    fun write() {
        write(data, File(getSettingsFile()))
    }

    private fun write(dataToWrite: UserData, target: File) {
        val doc = XML.newDocument()
        val element = doc.createElement("data")
        // Add attributes
        element.setAttribute("images-dir", dataToWrite.imagesDir)
        if (dataToWrite.licenseAgreed) element.setAttribute("license-agreed", "true")
        // Write
        doc.appendChild(element)
        XML.write(target, doc)
    }

    private fun read(): UserData {
        val file = File(getSettingsFile())
        return if (file.isFile) {
            val doc = XML.read(file)
            val element = doc.documentElement ?: throw IOException("Data XML document does not have a root element!")
            // Read attributes
            val imagesDir = element.getAttribute("images-dir").ifEmpty { UserData.defaultImagesDir }
            val licenseAgreed = element.getAttribute("license-agreed") == "true"
            File(imagesDir).mkdirs()
            // Return
            UserData(imagesDir, licenseAgreed)
        } else {
            val defaultData = UserData(UserData.defaultImagesDir)
            write(defaultData, file)
            defaultData
        }
    }

    private fun getSettingsFile(): String {
        return getAppData() + "settings.xml"
    }

    private fun getAppData(): String {
        val path = System.getenv("APPDATA").replace('\\', '/') + "/Janorico/JPaint/"
        File(path).mkdirs()
        return path
    }
}

data class UserData(var imagesDir: String, var licenseAgreed: Boolean = false) {
    companion object {
        val defaultImagesDir = System.getProperty("user.home").replace('\\', '/') + "/Documents/JPaint/"
    }
}
