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

package de.janorico.jpaint.gui

import Version
import de.janorico.jgl.JGL
import de.janorico.jgl.helpers.*
import de.janorico.jgl.helpers.Button
import de.janorico.jgl.helpers.Dialog
import de.janorico.jpaint.*
import de.janorico.jpaint.svg.SVGImage
import java.awt.*
import java.awt.image.BufferedImage
import java.io.*
import java.util.logging.Level
import javax.imageio.ImageIO
import javax.swing.*
import javax.swing.event.*

object GUI {
    val copyrightText = """
        Free and open-source svg-editor.
        Copyright (C) 2023 Janosch Lion

        This program is free software: you can redistribute it and/or modify
        it under the terms of the GNU General Public License as published by
        the Free Software Foundation, either version 3 of the License, or
        (at your option) any later version.

        This program is distributed in the hope that it will be useful,
        but WITHOUT ANY WARRANTY; without even the implied warranty of
        MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
        GNU General Public License for more details.

        You should have received a copy of the GNU General Public License
        along with this program.  If not, see <https://www.gnu.org/licenses/>.
    """.trimIndent()

    fun setWindow(frame: JFrame) {
        JGL.dialogOwner = frame
        logger.log(Level.FINE, "Set window to instance from ${frame::class.qualifiedName} (Hash code: ${frame.hashCode()})")
    }

    fun aboutDialog() {
        Dialog.showDialog("About", {
            it.minimumSize = Dimension(800, 600)
            return@showDialog JTabbedPane().apply {
                val hl = HyperlinkListener { e: HyperlinkEvent ->
                    if (e.eventType == HyperlinkEvent.EventType.ACTIVATED) {
                        Desktop.getDesktop().browse(e.url.toURI())
                    }
                }
                this.addTab("About", JEditorPane(
                    "text/html", """
                        <h1><b>${Version.current.name}</b></h1>
                        <h2><i>Free and open-source svg-editor.</i></h2>
                        <p>
                        Author: Janosch Lion
                        <br>
                        Website: <a href="https://github.com/Janorico/JPaint">https://github.com/Janorico/JPaint</a>
                        <br>
                        Email: <a href="mailto:janorico@posteo.de">janorico@posteo.de</a>
                        </p>
                    """.trimIndent()
                ).apply {
                    this.addHyperlinkListener(hl)
                    this.isEditable = false
                })
                this.addTab("Copyright", JLabel("<html><pre>$copyrightText</pre></html>", JLabel.CENTER))
                this.addTab(
                    "Sources", JScrollPane(JEditorPane(
                        "text/html", """
                            <h1>Sources</h1>
                            <h2>Libraries</h2>
                            <ul>
                                <li>FlatLaf 3.1.1 (<a href="https://search.maven.org/artifact/com.formdev/flatlaf/3.1.1/jar">Maven</a>, <a href="https://github.com/JFormDesigner/FlatLaf">GitHub</a>)</li>
                                <li>JGL 2.0 (<a href="https://github.com/Janorico/JGL">GitHub</a>)</li>
                            </ul>
                            <h2>Icons</h2>
                            <ul>
                                <li>JPaint Logo: <a target="_blank" href="https://iconduck.com/emojis/37648/paintbrush">paintbrush</a> by <a target="_blank" href="https://iconduck.com">Iconduck</a></li>
                                <li><a target="_blank" href="https://icons8.com/icon/43727/cursor">Cursor</a> icon by <a target="_blank" href="https://icons8.com">Icons8</a></li>
                                <li><a target="_blank" href="https://icons8.com/icon/57315/move">Move</a> icon by <a target="_blank" href="https://icons8.com">Icons8</a></li>
                                <li><a target="_blank" href="https://icons8.com/icon/52612/add">Add</a> icon by <a target="_blank" href="https://icons8.com">Icons8</a></li>
                                <li><a target="_blank" href="https://icons8.com/icon/43698/folder">Folder</a> icon by <a target="_blank" href="https://icons8.com">Icons8</a></li>
                                <li><a target="_blank" href="https://icons8.com/icon/45310/save">Save</a> icon by <a target="_blank" href="https://icons8.com">Icons8</a></li>
                                <li><a target="_blank" href="https://icons8.com/icon/52639/save-as">Save as</a> icon by <a target="_blank" href="https://icons8.com">Icons8</a></li>
                                <li><a target="_blank" href="https://icons8.com/icon/57558/export">Export</a> icon by <a target="_blank" href="https://icons8.com">Icons8</a></li>
                                <li><a target="_blank" href="https://icons8.com/icon/43529/close">Close</a> icon by <a target="_blank" href="https://icons8.com">Icons8</a></li>
                                <li><a target="_blank" href="https://icons8.com/icon/45321/undo">Undo</a> icon by <a target="_blank" href="https://icons8.com">Icons8</a></li>
                                <li><a target="_blank" href="https://icons8.com/icon/51741/redo">Redo</a> icon by <a target="_blank" href="https://icons8.com">Icons8</a></li>
                                <li><a target="_blank" href="https://icons8.com/icon/56641/resize">Resize</a> icon by <a target="_blank" href="https://icons8.com">Icons8</a></li>
                                <li><a target="_blank" href="https://icons8.com/icon/54151/gear">Gear</a> icon by <a target="_blank" href="https://icons8.com">Icons8</a></li>
                                <li><a target="_blank" href="https://icons8.com/icon/43722/about">About</a> icon by <a target="_blank" href="https://icons8.com">Icons8</a>
                                <li><a target="_blank" href="https://icons8.com/icon/51760/scroll-up">Scroll Up</a> icon by <a target="_blank" href="https://icons8.com">Icons8</a></li>
                                <li><a target="_blank" href="https://icons8.com/icon/51753/scroll-down">Scroll Down</a> icon by <a target="_blank" href="https://icons8.com">Icons8</a></li>
                                <li><a target="_blank" href="https://icons8.com/icon/57270/rename">Rename</a> icon by <a target="_blank" href="https://icons8.com">Icons8</a></li>
                                <li><a target="_blank" href="https://icons8.com/icon/43949/delete">Delete</a> icon by <a target="_blank" href="https://icons8.com">Icons8</a></li>
                            </ul>
                            <h2>Other</h2>
                            <ul>
                                <li>Ellipse formula: https://www.geeksforgeeks.org/check-if-a-point-is-inside-outside-or-on-the-ellipse/</li>
                                <li>Line formula: https://stackoverflow.com/a/328337/21656861</li>
                                <li>WrapLayout: https://tips4java.wordpress.com/2008/11/06/wrap-layout/ (http://www.camick.com/java/source/WrapLayout.java)</li>
                            </ul>
                        """.trimIndent()
                    ).apply {
                        this.addHyperlinkListener(hl)
                        this.isEditable = false
                    })
                )
            }
        }, { dialog: JDialog ->
            Button.create("Close") {
                dialog.dispose()
            }
        })
    }

    fun newDialog(onNew: (SVGImage) -> Unit) {
        widthHeightDialog("Create New", { width: Int, height: Int ->
            onNew(SVGImage(width, height))
        })
    }

    fun widthHeightDialog(title: String, onOk: (width: Int, height: Int) -> Unit, initialWidth: Int = 1920, initialHeight: Int = 1080) {
        val widthModel = SpinnerNumberModel(initialWidth, 1, 4096, 1)
        val heightModel = SpinnerNumberModel(initialHeight, 1, 4096, 1)
        Dialog.showDialog(title, {
            JPanel(GridLayout(2, 1, 5, 5)).apply {
                this.add(JPanel(BorderLayout(5, 5)).apply {
                    this.add(JLabel("Width: "), BorderLayout.WEST)
                    this.add(JSpinner(widthModel))
                })
                this.add(JPanel(BorderLayout(5, 5)).apply {
                    this.add(JLabel("Height: "), BorderLayout.WEST)
                    this.add(JSpinner(heightModel))
                })
            }
        }, {
            onOk(widthModel.number.toInt(), heightModel.number.toInt())
        }, {})
    }

    fun settingsDialog() {
        val imagesDirIn = JTextField(UDM.data.imagesDir, 20)
        Dialog.showDialog("Settings", {
            JPanel(GridLayout(1, 1, 5, 5)).apply {
                this.add(JPanel(BorderLayout(5, 5)).apply {
                    this.add(JLabel("Images Dir: "), BorderLayout.WEST)
                    this.add(imagesDirIn)
                    this.add(Button.create(getIcon("icons8-folder-16")) {
                        val chooser = FileChooser.createOpenDirectory()
                        chooser.currentDirectory = File(imagesDirIn.text)

                        val result = chooser.showOpenDialog(JGL.dialogOwner)

                        if (result == JFileChooser.APPROVE_OPTION) {
                            val selectedFile = chooser.selectedFile
                            if (selectedFile.isDirectory) {
                                imagesDirIn.text = selectedFile.path.replace('\\', '/')
                            } else {
                                OptionPane.showInformation("Selection isn't a directory!")
                            }
                        }
                    }, BorderLayout.EAST)
                })
            }
        }, {
            UDM.data.imagesDir = imagesDirIn.text
            UDM.write()
        }, {})
    }

    fun getIconBI(name: String): BufferedImage = ImageIO.read(GUI::class.java.classLoader.getResource("icons/$name.png") ?: throw IOException("Can't find icon $name!"))
    fun getIcon(name: String): ImageIcon = ImageIcon(GUI::class.java.classLoader.getResource("icons/$name.png") ?: throw IOException("Can't find icon $name!"))
}
