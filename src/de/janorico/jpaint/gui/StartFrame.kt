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

import WrapLayout
import de.janorico.jgl.JGL
import de.janorico.jgl.helpers.*
import de.janorico.jgl.helpers.Button
import de.janorico.jgl.helpers.Dialog
import de.janorico.jpaint.*
import de.janorico.jpaint.gui.editor.EditorFrame
import de.janorico.jpaint.svg.SVGImage
import java.awt.*
import java.awt.event.*
import java.io.File
import javax.swing.*
import javax.swing.border.LineBorder
import javax.swing.filechooser.FileSystemView
import kotlin.system.exitProcess

class StartFrame : JFrame() {
    init {
        GUI.setWindow(this)
        this.layout = BorderLayout(5, 5)
        this.add(JLabel("Welcome to ${Version.current.name}!", JLabel.CENTER).apply {
            font = font.deriveFont(30.0f)
            layout = BorderLayout()
            this.add(JPanel(GridLayout(1, 3, 5, 5)).apply {
                this.add(Button.create("New", GUI.getIcon("icons8-add-32")) {
                    GUI.newDialog {
                        this@StartFrame.dispose()
                        EditorFrame(it)
                    }
                })
                this.add(Button.create("Open", GUI.getIcon("icons8-folder-32")) {
                    val chooser = FileChooser.createOpenFile(arrayOf(SVGImage.svgFilter))
                    chooser.isAcceptAllFileFilterUsed = false
                    chooser.currentDirectory = File(UDM.data.imagesDir)

                    val result = chooser.showOpenDialog(JGL.dialogOwner)

                    if (result == JFileChooser.APPROVE_OPTION) {
                        val selectedFile = chooser.selectedFile
                        val image = SVGImage.read(selectedFile)
                        if (image != null) {
                            this@StartFrame.dispose()
                            EditorFrame(image, selectedFile)
                        }
                    }
                })
                this.add(Button.create("Settings", GUI.getIcon("icons8-gear-32")) { GUI.settingsDialog() })
            }, BorderLayout.EAST)
        }, BorderLayout.NORTH)
        this.add(JScrollPane(JPanel(WrapLayout(WrapLayout.LEFT, 5, 5)).apply {
            Thread {
                recursiveAddFiles(this, File(UDM.data.imagesDir)) {
                    if (SVGImage.svgFilter.accept(it)) {
                        val image = SVGImage.read(it)
                        if (image != null) {
                            this@StartFrame.dispose()
                            EditorFrame(image, it)
                        }
                    } else {
                        Desktop.getDesktop().open(it)
                    }
                }
            }.start()
        }))

        this.pack()
        this.iconImages = logos
        this.defaultCloseOperation = DISPOSE_ON_CLOSE
        this.extendedState = MAXIMIZED_BOTH
        this.setLocationRelativeTo(null)
        this.title = Version.current.name
        this.isVisible = true
        if (!UDM.data.licenseAgreed) {
            Dialog.showDialog("License", {
                it.addWindowListener(object : WindowAdapter() {
                    override fun windowClosing(e: WindowEvent?) {
                        exitProcess(0)
                    }
                })
                return@showDialog JLabel("<html><pre>" + GUI.copyrightText + "</pre></html>")
            }, { dialog: JDialog ->
                JPanel(FlowLayout(FlowLayout.CENTER)).apply {
                    this.add(Button.create("I Agree") {
                        dialog.dispose()
                        UDM.data.licenseAgreed = true
                        UDM.write()
                    })
                }
            })
        }
    }

    private fun recursiveAddFiles(to: JComponent, dir: File, onClick: (File) -> Unit) {
        val files = dir.listFiles()
        if (files != null) {
            for (file in files) {
                if (file.isFile) {
                    val label = FileLabel(file, onClick)
                    to.add(label)
                    to.revalidate()
                } else if (file.isDirectory) {
                    recursiveAddFiles(to, file, onClick)
                }
            }
        }
    }

    private class FileLabel(file: File, onClick: (File) -> Unit) : JLabel(file.name, FileSystemView.getFileSystemView().getSystemIcon(file, 64, 64), CENTER) {
        init {
            Dimension(100, 100).also {
                this.minimumSize = it
                this.preferredSize = it
                this.maximumSize = it
            }
            this.verticalTextPosition = BOTTOM
            this.horizontalTextPosition = CENTER
            this.toolTipText = file.path
            this.border = LineBorder(Color.WHITE)
            this.isOpaque = true
            this.addMouseListener(object : MouseAdapter() {
                override fun mouseClicked(e: MouseEvent?) {
                    onClick(file)
                }

                override fun mouseEntered(e: MouseEvent?) {
                    this@FileLabel.background = Color.DARK_GRAY
                }

                override fun mouseExited(e: MouseEvent?) {
                    this@FileLabel.background = Color.BLACK
                }
            })
        }
    }
}
