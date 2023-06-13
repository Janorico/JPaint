/*
 * Version singleton for Kotlin - Store version info and check for updates.
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

import java.awt.Component
import java.io.*
import java.net.URL
import java.util.*
import javax.swing.JOptionPane

data class Version(val key: Float, val name: String) {
    companion object {
        val current =
            getFromProperties(Version::class.java.classLoader.getResource("version.properties") ?: throw IOException("Error while loading version info: can't load resource!"))

        @Throws(Exception::class)
        fun checkForUpdates(url: URL, dialogParent: Component? = null) {
            val properties = Properties()
            val stream = url.openStream()
            properties.load(stream)
            stream.close()
            val version = getFromProperties(properties)
            if (version.key > current.key) {
                if (JOptionPane.showConfirmDialog(dialogParent, "${version.name} is available! Update now?", "Update", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION) {
                    val inStream = URL(properties.getProperty("installer-url")).openStream()
                    val path = System.getProperty("user.home") + "/Downloads/${version.name} Setup.exe"
                    val fos = FileOutputStream(path)
                    val buffer = ByteArray(1024)
                    while (inStream.read(buffer) >= 0) {
                        fos.write(buffer)
                    }
                    inStream.close()
                    Runtime.getRuntime().addShutdownHook(Thread {
                        Runtime.getRuntime().exec(path)
                    })
                }
            }
        }

        private fun getFromProperties(url: URL): Version {
            val properties = Properties()
            val stream = url.openStream()
            properties.load(stream)
            stream.close()
            return getFromProperties(properties)
        }

        private fun getFromProperties(properties: Properties): Version =
            Version(properties.getProperty("key").toFloatOrNull() ?: throw IOException("Error while loading version info: can't get key!"), properties.getProperty("name"))
    }
}
