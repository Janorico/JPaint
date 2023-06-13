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

package de.janorico.jpaint.gui.editor.tools

import de.janorico.jpaint.gui.GUI
import de.janorico.jpaint.gui.editor.EditorFrame
import de.janorico.jpaint.svg.shapes.SVGEllipse
import java.awt.*
import java.awt.event.MouseEvent

class DrawEllipseTool(override val redrawRequest: () -> Unit, override val getShapeParameters: (idPrefix: String) -> ShapeParameters) :
    Tool("Draw Ellipse", GUI.getIcon("ellipse-48")) {
    private var startPoint: Point? = null
    private var tempPoint: Point? = null

    private fun calculateXRadius(x1: Int, x2: Int): Int = if (x1 < x2) x2 - x1 else x1 - x2

    private fun calculateYRadius(y1: Int, y2: Int): Int = if (y1 < y2) y2 - y1 else y1 - y2

    override fun paintDraft(g: Graphics) {
        val startPoint1 = startPoint
        val tempPoint1 = tempPoint
        if (startPoint1 != null && tempPoint1 != null && startPoint1.x != tempPoint1.x && startPoint1.y != tempPoint1.y) {
            val xRadius = calculateXRadius(startPoint1.x, tempPoint1.x)
            val yRadius = calculateYRadius(startPoint1.y, tempPoint1.y)
            g.drawOval(startPoint1.x - xRadius, startPoint1.y - yRadius, xRadius * 2, yRadius * 2)
        }
    }

    override fun mousePressed(e: MouseEvent, editor: EditorFrame) {
        startPoint = e.point
    }

    override fun mouseReleased(e: MouseEvent, editor: EditorFrame) {
        val startPoint1 = startPoint
        if (startPoint1 != null) {
            // Mouse input
            val x1 = startPoint1.x
            val y1 = startPoint1.y
            val x2 = e.point.x
            val y2 = e.point.y
            // Shape Parameters
            val shapeParameters = getShapeParameters("ellipse")
            // Add shape
            editor.image.addShape(
                SVGEllipse(
                    calculateXRadius(x1, x2).toFloat(),
                    calculateYRadius(y1, y2).toFloat(),
                    x1.toFloat(),
                    y1.toFloat(),
                    shapeParameters.id,
                    shapeParameters.fill,
                    shapeParameters.stroke,
                )
            )
        }
        startPoint = null
        tempPoint = null
        redrawRequest()
    }

    override fun mouseDragged(e: MouseEvent, editor: EditorFrame) {
        if (startPoint != null) {
            tempPoint = e.point
        }
        redrawRequest()
    }
}
