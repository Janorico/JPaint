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
import de.janorico.jpaint.svg.shapes.SVGShape
import java.awt.Point
import java.awt.event.MouseEvent

class MoveTool(override val redrawRequest: () -> Unit, override val getShapeParameters: (idPrefix: String) -> ShapeParameters) : Tool("Move", GUI.getIcon("icons8-move-48")) {
    private var prevPoint: Point? = null
    private var shape: SVGShape? = null

    override fun mousePressed(e: MouseEvent, editor: EditorFrame) {
        val shapeIndex = editor.getShapeOnPoint(e.point)
        if (shapeIndex > -1) shape = editor.image.getShape(shapeIndex)
    }

    override fun mouseReleased(e: MouseEvent, editor: EditorFrame) {
        if (prevPoint != null && shape != null) {
            editor.actionPerformed("Move Shape")
        }
        prevPoint = null
        shape = null
    }

    override fun mouseDragged(e: MouseEvent, editor: EditorFrame) {
        val point1 = prevPoint
        val point2 = e.point
        val shape1 = shape
        if (point1 != null && shape1 != null) {
            val x = point2.x - point1.x
            val y = point2.y - point1.y
            shape1.move(x, y)
            redrawRequest()
        }
        prevPoint = e.point
    }
}
