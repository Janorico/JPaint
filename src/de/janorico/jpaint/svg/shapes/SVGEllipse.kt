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

class SVGEllipse(var rx: Float, var ry: Float, var cx: Float, var cy: Float, override var id: String, override var fill: Color?, override var stroke: Color?) : SVGShape() {
    override fun paint(g: Graphics, offsetX: Int, offsetY: Int) {
        val x = (cx - rx).roundToInt() + offsetX
        val y = (cy - ry).roundToInt() + offsetY
        val width = (rx * 2.0f).roundToInt()
        val height = (ry * 2.0f).roundToInt()
        if (fill != null) {
            g.color = fill
            g.fillOval(x, y, width, height)
        }
        if (stroke != null) {
            g.color = stroke
            g.drawOval(x, y, width, height)
        }
    }

    override fun isOnPoint(point: Point): Boolean {
        /*
         * Bigger than 1: Outside
         * Equal to 1: On outline
         * Smaller than 1: Inner
         */
        val result = (((point.x.toFloat() - cx).pow(2.0f) / rx.pow(2.0f)) + ((point.y - cy).pow(2.0f) / ry.pow(2.0f)))
        println("Ellipse, ID: $id    Result: $result")
        if (stroke != null && (result < 1.01 && result > 0.99)) {
            return true
        }
        return fill != null && result < 1
    }

    override fun move(x: Int, y: Int) {
        cx += x
        cy += y
    }

    override fun toXML(doc: Document): Element {
        val element = doc.createElement("ellipse")
        element.setAttribute("rx", encodeFloat(rx))
        element.setAttribute("ry", encodeFloat(ry))
        element.setAttribute("cx", encodeFloat(cx))
        element.setAttribute("cy", encodeFloat(cy))
        writeXMLAttributes(element)
        return element
    }

    override fun clone(): SVGEllipse {
        return SVGEllipse(rx, ry, cx, cy, id, fill, stroke)
    }
}
