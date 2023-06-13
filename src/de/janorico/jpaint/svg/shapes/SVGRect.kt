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

package de.janorico.jpaint.svg.shapes

import org.w3c.dom.*
import java.awt.*
import kotlin.math.roundToInt

class SVGRect(var x: Float, var y: Float, var width: Float, var height: Float, override var id: String, override var fill: Color?, override var stroke: Color?) : SVGShape() {
    override fun paint(g: Graphics, offsetX: Int, offsetY: Int) {
        val x1 = x.roundToInt() + offsetX
        val y1 = y.roundToInt() + offsetY
        val width1 = width.roundToInt()
        val height1 = height.roundToInt()
        if (fill != null) {
            g.color = fill
            g.fillRect(x1, y1, width1, height1)
        }
        if (stroke != null) {
            g.color = stroke
            g.drawRect(x1, y1, width1, height1)
        }
    }

    override fun isOnPoint(point: Point): Boolean {
        if (fill != null) {
            if ((point.x > x && point.x < (x + width)) && (point.y > y && point.y < (y + height))) return true
        }
        if (stroke != null) {
            if ((point.x == x.roundToInt() || point.x == (x + width).roundToInt()) && (point.y == y.roundToInt() || point.y == (y + height).roundToInt())) return true
        }
        return false
    }

    override fun move(x: Int, y: Int) {
        this.x += x
        this.y += y
    }

    override fun toXML(doc: Document): Element {
        val element = doc.createElement("rect")
        element.setAttribute("x", encodeFloat(x))
        element.setAttribute("y", encodeFloat(y))
        element.setAttribute("width", encodeFloat(width))
        element.setAttribute("height", encodeFloat(height))
        writeXMLAttributes(element)
        return element
    }

    override fun clone(): SVGRect {
        return SVGRect(x, y, width, height, id, fill, stroke)
    }
}
