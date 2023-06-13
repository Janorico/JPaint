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
import de.janorico.jpaint.svg.shapes.SVGLine
import java.awt.*
import java.awt.event.MouseEvent

class DrawLineTool(override val redrawRequest: () -> Unit, override val getShapeParameters: (idPrefix: String) -> ShapeParameters) : Tool("Draw Line", GUI.getIcon("line-48")) {
    private var startPoint: Point? = null
    private var tempPoint: Point? = null

    override fun paintDraft(g: Graphics) {
        val startPoint1 = startPoint
        val tempPoint1 = tempPoint
        if (startPoint1 != null && tempPoint1 != null) {
            g.drawLine(startPoint1.x, startPoint1.y, tempPoint1.x, tempPoint1.y)
        }
    }

    override fun mousePressed(e: MouseEvent, editor: EditorFrame) {
        startPoint = e.point
    }

    override fun mouseReleased(e: MouseEvent, editor: EditorFrame) {
        val startPoint1 = startPoint
        if (startPoint1 != null) {
            val shapeParameters = getShapeParameters("line")
            val endPoint = e.point
            editor.image.addShape(SVGLine(startPoint1.x.toFloat(), startPoint1.y.toFloat(), endPoint.x.toFloat(), endPoint.y.toFloat(), shapeParameters.id, shapeParameters.stroke))
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
