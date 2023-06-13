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

import Version
import de.janorico.jgl.JGL
import de.janorico.jgl.helpers.SplashScreen
import de.janorico.jpaint.gui.*
import de.janorico.jpaint.gui.editor.EditorFrame
import de.janorico.jpaint.gui.theme.JPaintTheme
import de.janorico.jpaint.svg.SVGImage
import java.io.File
import java.net.URL
import java.util.logging.*

val logos = listOf(
    GUI.getIconBI("logo/logo16"),
    GUI.getIconBI("logo/logo20"),
    GUI.getIconBI("logo/logo24"),
    GUI.getIconBI("logo/logo32"),
    GUI.getIconBI("logo/logo40"),
    GUI.getIconBI("logo/logo48"),
)
val logger: Logger = Logger.getLogger("de.janorico.jpaint")

fun main(args: Array<String>) {
    println(GUI.copyrightText)
    logger.level = Level.ALL
    logger.addHandler(FileHandler(UDM.getLoggerFile(), true).apply {
        this.formatter = SimpleFormatter()
        this.level = Level.ALL
    })
    logger.addHandler(GUIHandler().apply {
        this.formatter = SimpleFormatter()
        this.level = Level.SEVERE
    })
    Thread.setDefaultUncaughtExceptionHandler { _: Thread, e: Throwable ->
        logger.log(Level.SEVERE, "An error has occurred.", e)
    }
    logger.log(Level.INFO, "Starting JPaint...")
    JGL.programName = Version.current.name
    JPaintTheme.setup()
    val splashScreen = SplashScreen.create(GUI.getIcon("logo/splash"))
    splashScreen.isVisible = true
    try {
        Version.checkForUpdates(URL("https://raw.githubusercontent.com/Janorico/JPaint/master/src/version.properties"))
    } catch (e: Exception) {
        logger.log(Level.WARNING, "Can't check for updates!", e)
    }
    splashScreen.dispose()
    if (args.isEmpty()) {
        StartFrame()
    } else {
        for (arg in args) {
            val file = File(arg)
            if (!file.isFile) {
                logger.log(Level.WARNING, "File \"$arg\" is no file!")
            } else {
                val rr = SVGImage.read(file)
                if (rr != null) {
                    EditorFrame(rr, file)
                }
            }
        }
    }
}

fun exception(msg: String, thrown: Throwable) {
    logger.log(Level.SEVERE, msg, thrown)
}

fun <T> T?.notNull(onNotNull: T.() -> Unit) {
    this?.onNotNull()
}

fun String.fill(fillString: String, targetLength: Int): String {
    var result = this
    while (result.length < targetLength) {
        result = fillString + result
    }
    return result
}
