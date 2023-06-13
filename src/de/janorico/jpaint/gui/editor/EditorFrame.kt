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

package de.janorico.jpaint.gui.editor

import de.janorico.jgl.JGL
import de.janorico.jgl.helpers.*
import de.janorico.jgl.helpers.MenuItem
import de.janorico.jpaint.*
import de.janorico.jpaint.gui.*
import de.janorico.jpaint.gui.editor.tools.Tool
import de.janorico.jpaint.svg.*
import de.janorico.jpaint.svg.shapes.SVGShape
import java.awt.*
import java.awt.event.*
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO
import javax.swing.*
import javax.swing.border.TitledBorder
import javax.swing.filechooser.FileNameExtensionFilter
import kotlin.system.exitProcess

class EditorFrame(var image: SVGImage, private var file: File? = null) : JFrame() {
    // Components
    private val toolbox: Toolbox = Toolbox({
        imageLabel.repaint()
    }, { idPrefix: String ->
        Tool.ShapeParameters(SVGImage.generateId(idPrefix), bottomBar.getFillColor(), bottomBar.getStrokeColor())
    })
    val imageLabel: JLabel = object : JLabel() {
        override fun paint(g: Graphics) {
            super.paint(g)
            toolbox.selectedTool.paintDraft(g)
        }
    }
    private val elementsList = JList<SVGShape>()
    private val undoRedoList = JList<UndoRedoStep>()
    private val bottomBar = EditorBottomBar({
        if (selectedIndex > -1) {
            image.getShape(selectedIndex).fill = it
            actionPerformed("Change Fill")
            imageLabel.repaint()
        }
    }, {
        if (selectedIndex > -1) {
            image.getShape(selectedIndex).stroke = it
            actionPerformed("Change Stroke")
            imageLabel.repaint()
        }
    })

    // Other
    var selectedIndex = -1
        set(value) {
            field = value
            if (value < 0) {
                elementsList.clearSelection()
            } else {
                image.getShape(value).also {
                    bottomBar.setFillColor(it.fill)
                    bottomBar.setStrokeColor(it.stroke)
                }
                if (elementsList.selectedIndex != value) {
                    elementsList.selectedIndex = value
                }
            }
        }

    private var icon = SVGIcon(image)
    private var changed = true
    private val shapeListener = object : SVGImage.ShapeListener {
        override fun shapeAdded() {
            actionPerformed("Add Shape")
            refreshElementsList()
        }

        override fun shapeRemoved() {
            actionPerformed("Remove Shape")
            refreshElementsList()
        }

        override fun shapeRaised() {
            println("Shape raised: $selectedIndex")
            actionPerformed("Raise Shape")
            imageLabel.repaint()
            refreshElementsList()
        }

        override fun shapeLowered() {
            println("Shape lowered: $selectedIndex")
            actionPerformed("Lower Shape")
            imageLabel.repaint()
            refreshElementsList()
        }
    }

    // Undo and redo
    private var undoRedoSteps = ArrayList<UndoRedoStep>()
    private var undoRedoIndex = 0

    init {
        GUI.setWindow(this)
        this.layout = BorderLayout(5, 5)
        this.jMenuBar = EditorMenuBar(this)
        if (file != null) {
            changed = false
        }
        // Toolbox
        this.add(JPanel(BorderLayout(5, 5)).apply {
            this.border = TitledBorder("Toolbox")
            this.add(toolbox, BorderLayout.NORTH)
        }, BorderLayout.WEST)
        // Viewport
        val imagePopup = JPopupMenu().apply {
            this.add(MenuItem.create("Undo", GUI.getIcon("icons8-undo-16")) { undo() })
            this.add(MenuItem.create("Redo", GUI.getIcon("icons8-redo-16")) { redo() })
            this.addSeparator()
            this.add(MenuItem.create("Rename", GUI.getIcon("icons8-rename-16")) {
                if (selectedIndex > -1) {
                    val shape = image.getShape(selectedIndex)
                    val result = OptionPane.showPromptDialog("Enter name:", "Rename", shape.id)
                    if (result != null) {
                        shape.id = result
                        actionPerformed("Rename Shape")
                        refreshElementsList()
                    }
                } else {
                    message("No selection!")
                }
            })
            this.add(MenuItem.create("Delete", GUI.getIcon("icons8-delete-16")) {
                if (selectedIndex > -1) {
                    image.removeShape(selectedIndex)
                    imageLabel.repaint()
                } else {
                    message("No selection!")
                }
            })
            this.add(MenuItem.create("Raise", GUI.getIcon("icons8-scroll-up-16")) {
                if (selectedIndex > -1) {
                    if (selectedIndex > 0) {
                        image.raiseShape(selectedIndex)
                        selectedIndex--
                    } else {
                        message("Can't raise first shape!")
                    }
                } else {
                    message("No selection!")
                }
            })
            this.add(MenuItem.create("Lower", GUI.getIcon("icons8-scroll-down-16")) {
                if (selectedIndex > -1) {
                    if (selectedIndex < image.getShapes().lastIndex) {
                        image.lowerShape(selectedIndex)
                        selectedIndex++
                    } else {
                        message("Can't lower last shape!")
                    }
                } else {
                    message("No selection!")
                }
            })
        }
        imageLabel.addMouseListener(object : MouseAdapter() {
            override fun mouseClicked(e: MouseEvent) {
                if (SwingUtilities.isLeftMouseButton(e)) toolbox.selectedTool.mouseClicked(e, this@EditorFrame)
            }

            override fun mousePressed(e: MouseEvent) {
                if (SwingUtilities.isLeftMouseButton(e)) toolbox.selectedTool.mousePressed(e, this@EditorFrame)
            }

            override fun mouseReleased(e: MouseEvent) {
                if (e.isPopupTrigger) {
                    imagePopup.show(e.component, e.x, e.y)
                } else if (SwingUtilities.isLeftMouseButton(e)) {
                    toolbox.selectedTool.mouseReleased(e, this@EditorFrame)
                }
            }
        })
        imageLabel.addMouseMotionListener(object : MouseMotionListener {
            override fun mouseDragged(e: MouseEvent) {
                bottomBar.setMousePosition(e.x, e.y)
                if (SwingUtilities.isLeftMouseButton(e)) toolbox.selectedTool.mouseDragged(e, this@EditorFrame)
            }

            override fun mouseMoved(e: MouseEvent) {
                bottomBar.setMousePosition(e.x, e.y)
            }
        })
        imageLabel.cursor = Cursor(Cursor.CROSSHAIR_CURSOR)
        add(JScrollPane(object : JPanel(GridBagLayout()) {
            init {
                this.add(imageLabel)
            }

            override fun paint(g: Graphics) {
                super.paint(g)
                for (component in components) {
                    g.color = Color.WHITE
                    g.drawRect(component.x - 1, component.y - 1, component.width + 1, component.height + 1)
                }
            }
        }).apply { this.border = TitledBorder("Viewport") })
        // Lists
        Dimension(250, 0).also {
            elementsList.minimumSize = it
            elementsList.preferredSize = it
            undoRedoList.minimumSize = it
            undoRedoList.preferredSize = it
        }
        // Elements list
        refreshElementsList()
        elementsList.addListSelectionListener {
            val index = elementsList.selectedIndex
            if (index != selectedIndex) {
                selectedIndex = index
            }
        }
        // Undo redo list
        imageChanged(true)
        undoRedoList.addListSelectionListener {
            val index = undoRedoList.selectedIndex
            if (index != undoRedoIndex) {
                undoRedo(index)
            }
        }
        add(JPanel(GridLayout(2, 1, 5, 5)).apply {
            this.add(JScrollPane(elementsList).apply { this.border = TitledBorder("Elements") })
            this.add(JScrollPane(undoRedoList).apply { this.border = TitledBorder("Undo/Redo") })
        }, BorderLayout.EAST)
        updateStatusText()
        this.add(bottomBar, BorderLayout.SOUTH)

        this.pack()
        this.iconImages = logos
        this.defaultCloseOperation = DO_NOTHING_ON_CLOSE
        this.addWindowListener(object : WindowAdapter() {
            override fun windowClosing(e: WindowEvent?) {
                exit()
            }
        })
        this.extendedState = MAXIMIZED_BOTH
        updateTitle()
        this.setLocationRelativeTo(null)
        this.isVisible = true
    }

    // FILE MENU

    fun new() {
        GUI.newDialog {
            if (saveQuestion()) {
                changeImage(it)
                file = null
                changed = true
                updateTitle()
                updateStatusText()
                bottomBar.showMessage("New image created.")
            }
        }
    }

    fun open() {
        val chooser = FileChooser.createOpenFile(arrayOf(SVGImage.svgFilter))
        chooser.isAcceptAllFileFilterUsed = false
        chooser.currentDirectory = File(UDM.data.imagesDir)

        val result = chooser.showOpenDialog(JGL.dialogOwner)

        if (result == JFileChooser.APPROVE_OPTION && saveQuestion()) {
            val selectedFile = chooser.selectedFile
            val rr = SVGImage.read(selectedFile)
            if (rr != null) {
                changeImage(rr)
                file = selectedFile
                changed = false
                updateTitle()
                updateStatusText()
                bottomBar.showMessage("File \"${selectedFile.path}\" opened.")
            }
        }
    }

    fun save(saveAs: Boolean, onSaved: () -> Unit = {}) {
        val file1 = file
        if (file1 != null && !saveAs) {
            save(file1, onSaved)
        } else {
            val chooser = FileChooser.createSaveFile(arrayOf(SVGImage.svgFilter))
            chooser.isAcceptAllFileFilterUsed = false
            chooser.currentDirectory = File(UDM.data.imagesDir)

            val result = chooser.showSaveDialog(JGL.dialogOwner)

            if (result == JFileChooser.APPROVE_OPTION) {
                var path = chooser.selectedFile.path
                if (!path.endsWith(".svg")) {
                    path += ".svg"
                }
                val saveFile = File(path)
                if (!saveFile.isFile || (OptionPane.overwriteFileDialog(path) == JOptionPane.YES_OPTION)) {
                    save(saveFile, onSaved)
                    file = saveFile
                }
            }
        }
    }

    private fun save(file: File, onSaved: () -> Unit) {
        image.write(file)
        bottomBar.showMessage("File \"${file.path}\" saved.")
        changed = false
        onSaved()
    }

    fun export() {
        val pngFilter = FileNameExtensionFilter("Portable Network Groups (*.png)", "png")
        val jpegFilter = FileNameExtensionFilter("Joint Photographic Experts Group (*.jpg, *.jpeg)", "jpg", "jpeg")
        val chooser = FileChooser.createSaveFile(arrayOf(pngFilter, jpegFilter))
        chooser.dialogTitle = JGL.programName + ": Export"
        chooser.isAcceptAllFileFilterUsed = false
        chooser.currentDirectory = File(UDM.data.imagesDir)

        val result = chooser.showSaveDialog(JGL.dialogOwner)

        if (result == JFileChooser.APPROVE_OPTION) {
            val bi = BufferedImage(image.width, image.height, BufferedImage.TYPE_INT_ARGB)
            image.paint(bi.graphics)
            var path = chooser.selectedFile.path
            val format = if (chooser.fileFilter == jpegFilter) {
                if (!path.endsWith(".jpg") && !path.endsWith(".jpeg")) {
                    path += ".jpeg"
                }
                "jpeg"
            } else {
                if (!path.endsWith(".png")) {
                    path += ".png"
                }
                "png"
            }
            val exportFile = File(path)
            if (!exportFile.isFile || (OptionPane.overwriteFileDialog(path) == JOptionPane.YES_OPTION)) {
                ImageIO.write(bi, format, exportFile)
            }
        }
    }

    fun close() {
        if (saveQuestion()) {
            this.dispose()
            StartFrame()
        }
    }

    fun exit() {
        if (saveQuestion()) {
            exitProcess(0)
        }
    }

    // EDIT MENU

    fun undo() {
        if (undoRedoIndex > 0) {
            undoRedo(undoRedoIndex - 1)
        } else {
            message("Nothing to undo!")
        }
    }

    fun redo() {
        if (undoRedoIndex < undoRedoSteps.lastIndex) {
            undoRedo(undoRedoIndex + 1)
        } else {
            message("Nothing to redo!")
        }
    }

    fun resizeImage() {
        GUI.widthHeightDialog("Resize", { width: Int, height: Int ->
            image.width = width
            image.height = height
            actionPerformed("Resize")
            imageLabel.revalidate()
            this.repaint()
            bottomBar.showMessage("Image resized to ${width}x${height}.")
        }, image.width, image.height)
    }


    // UNDO AND REDO

    private fun undoRedo(newIndex: Int) {
        if (newIndex > -1 && newIndex < undoRedoSteps.size) {
            undoRedoIndex = newIndex
            image = undoRedoSteps[undoRedoIndex].image.clone()
            imageChanged(false)
        }
    }

    fun actionPerformed(name: String) {
        changed = true
        if (undoRedoIndex < undoRedoSteps.lastIndex) {
            undoRedoSteps.subList(undoRedoIndex + 1, undoRedoSteps.size).clear()
        }
        undoRedoSteps.add(UndoRedoStep(name, image.clone()))
        undoRedoIndex = undoRedoSteps.lastIndex
        updateStatusText()
        refreshUndoRedoList()
    }

    // OTHER

    private fun updateTitle() {
        val file1 = file
        this.title = Version.current.name + ": " + if (file1 == null) "New Image" else "${file1.name} (${file1.path})"
    }

    private fun updateStatusText() {
        val file1 = file
        bottomBar.setStatusText((if (file1 == null) "New Image" else file1.name) + ", ${image.getHashCodeString()}")
    }

    private fun refreshElementsList() {
        val idx = selectedIndex
        elementsList.setListData(image.getShapes())
        selectedIndex = idx
        elementsList.selectedIndex = selectedIndex
    }

    private fun refreshUndoRedoList() {
        undoRedoList.setListData(undoRedoSteps.toTypedArray())
        undoRedoList.selectedIndex = undoRedoIndex
    }


    private fun changeImage(newImage: SVGImage) {
        image = newImage
        imageChanged(true)
    }


    private fun imageChanged(resetUndoRedo: Boolean) {
        image.shapeListener = shapeListener
        icon = SVGIcon(image)
        imageLabel.icon = icon
        imageLabel.revalidate()
        imageLabel.repaint()
        updateStatusText()
        refreshElementsList()
        if (resetUndoRedo) {
            undoRedoIndex = 0
            undoRedoSteps.clear()
            undoRedoSteps.add(UndoRedoStep("Base Image", image.clone()))
        }
        refreshUndoRedoList()
    }

    fun getShapeOnPoint(point: Point): Int {
        val shapes = image.getShapes()
        for (i in shapes.indices.reversed()) {
            if (shapes[i].isOnPoint(point)) {
                return i
            }
        }
        return -1
    }

    private fun message(msg: String) {
        bottomBar.showMessage(msg)
        OptionPane.showInformation(msg)
    }

    private fun saveQuestion(): Boolean {
        var returnValue = false
        if (changed) {
            when (OptionPane.showConfirmDialog("Do you want to save before continue?", "Confirmation", JOptionPane.YES_NO_CANCEL_OPTION)) {
                JOptionPane.YES_OPTION -> {
                    save(false) {
                        returnValue = true
                    }
                }

                JOptionPane.NO_OPTION -> {
                    returnValue = true
                }
            }
        } else {
            returnValue = true
        }
        return returnValue
    }

    private data class UndoRedoStep(val name: String, val image: SVGImage) {
        override fun toString(): String {
            return "$name (${image.getHashCodeString()})"
        }
    }
}
