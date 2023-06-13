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
import java.awt.*
import java.awt.event.*
import java.awt.font.*
import javax.swing.*
import javax.swing.border.TitledBorder
import kotlin.math.roundToInt

class EditorBottomBar(fillColorChanged: (newColor: Color?) -> Unit, strokeColorChanged: (newColor: Color?) -> Unit) : JPanel(BorderLayout(5, 5)) {
    private val statusLabel = JLabel()
    private val mousePositionLabel = JLabel("0, 0")
    private val messageLabel = JLabel()
    private val messageLabelTimer = (Timer(5000) {
        messageLabel.text = ""
    }).apply {
        this.isRepeats = false
    }
    private val fillColorButton = ColorButton("Fill", Color.ORANGE, fillColorChanged)
    private val strokeColorButton = ColorButton("Stroke", Color.WHITE, strokeColorChanged)

    init {
        Dimension(0, 48).also {
            this.minimumSize = it
            this.preferredSize = it
        }
        this.add(JPanel(GridLayout(1, 3)).apply {
            statusLabel.border = TitledBorder("Status")
            this.add(statusLabel)
            mousePositionLabel.border = TitledBorder("Mouse Position")
            this.add(mousePositionLabel)
            messageLabel.border = TitledBorder("Message")
            this.add(messageLabel)
        })
        this.add(JPanel(GridLayout(1, 2, 5, 5)).apply {
            this.add(fillColorButton)
            this.add(strokeColorButton)
        }, BorderLayout.EAST)
    }

    fun setStatusText(text: String) {
        statusLabel.text = text
    }

    fun setMousePosition(x: Int, y: Int) {
        mousePositionLabel.text = "$x, $y"
    }

    fun showMessage(msg: String) {
        messageLabel.text = msg
        messageLabelTimer.restart()
    }

    fun setFillColor(value: Color?) {
        fillColorButton.color = value
        fillColorButton.repaint()
    }

    fun getFillColor(): Color? = fillColorButton.color

    fun setStrokeColor(value: Color?) {
        strokeColorButton.color = value
        strokeColorButton.repaint()
    }

    fun getStrokeColor(): Color? = strokeColorButton.color

    private class ColorButton(colorName: String, var color: Color?, colorChanged: (newColor: Color?) -> Unit) : JComponent() {
        init {
            Dimension(38, 48).also {
                this.minimumSize = it
                this.preferredSize = it
            }
            this.toolTipText = "$colorName (Click to change, CTRL+Click to remove)"
            this.addMouseListener(object : MouseAdapter() {
                override fun mouseClicked(e: MouseEvent) {
                    if (e.isControlDown) {
                        color = null
                        this@ColorButton.repaint()
                        colorChanged(color)
                    } else {
                        val result = JColorChooser.showDialog(JGL.dialogOwner, "Choose $colorName", color, false)
                        if (result != null) {
                            color = result
                            this@ColorButton.repaint()
                            colorChanged(color)
                        }
                    }
                }
            })
        }

        override fun paint(g: Graphics) {
            g.color = this.foreground
            g.drawRect(0, 8, width - 1, height - 11)
            if (color == null) {
                g.font = this.font
                val size = TextLayout("none", g.font, FontRenderContext(g.font.transform, false, false)).bounds
                g.drawString(
                    "none", ((this.width.toDouble() - size.width) / 2.0).roundToInt(), ((((this.height.toDouble() - 8.0) - size.height) / 2.0) + size.height + 8.0).roundToInt()
                )
            } else {
                g.color = color
                g.fillRect(1, 9, width - 2, height - 12)
            }
        }
    }
}
