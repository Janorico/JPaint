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
import kotlin.math.*

class SVGLine(var x1: Float, var y1: Float, var x2: Float, var y2: Float, override var id: String, override var stroke: Color?) : SVGShape() {
    override var fill: Color? = null

    override fun paint(g: Graphics, offsetX: Int, offsetY: Int) {
        val x3 = x1.roundToInt() + offsetX
        val y3 = y1.roundToInt() + offsetY
        val x4 = x2.roundToInt() + offsetX
        val y4 = y2.roundToInt() + offsetY
        if (stroke != null) {
            g.color = stroke
            g.drawLine(x3, y3, x4, y4)
        }
    }

    override fun isOnPoint(point: Point): Boolean {
        val x3 = point.x.toFloat()
        val y3 = point.y.toFloat()
        val result = ((x2 - x1) * (y3 - y1) == (x3 - x1) * (y2 - y1) && abs(cmp(x1, x3) + cmp(x2, x3)) <= 1 && abs(cmp(y1, y3) + cmp(y2, y3)) <= 1)
        println("Line, ID: $id    Result: $result")
        return result
    }

    override fun move(x: Int, y: Int) {
        x1 += x
        y1 += y
        x2 += x
        y2 += y
    }

    private fun cmp(a: Float, b: Float): Int = a.compareTo(b)

    override fun toXML(doc: Document): Element {
        val element = doc.createElement("line")
        element.setAttribute("x1", encodeFloat(x1))
        element.setAttribute("y1", encodeFloat(y1))
        element.setAttribute("x2", encodeFloat(x2))
        element.setAttribute("y2", encodeFloat(y2))
        writeXMLAttributes(element)
        return element
    }

    override fun clone(): SVGLine {
        return SVGLine(x1, y1, x2, y2, id, stroke)
    }
}
