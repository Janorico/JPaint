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

import de.janorico.jpaint.*
import org.w3c.dom.*
import java.awt.*
import java.util.*
import kotlin.math.*

abstract class SVGShape {
    abstract var id: String
    abstract var fill: Color?
    abstract var stroke: Color?

    abstract fun paint(g: Graphics, offsetX: Int, offsetY: Int)
    abstract fun isOnPoint(point: Point): Boolean
    abstract fun move(x: Int, y: Int)
    abstract fun toXML(doc: Document): Element
    abstract fun clone(): SVGShape

    companion object {
        fun decodeColor(string: String): Color {
            return Color(HexFormat.fromHexDigits(string, 1, string.length))
        }
    }

    private fun encodeColor(color: Color): String = buildString {
        append("#")
        append(Integer.toHexString(color.red).fill("0", 2))
        append(Integer.toHexString(color.green).fill("0", 2))
        append(Integer.toHexString(color.blue).fill("0", 2))
    }

    fun encodeFloat(float: Float): String = if (round(float) == float) float.roundToInt().toString() else float.toString()

    fun writeXMLAttributes(element: Element) {
        element.setAttribute("id", id)
        fill.notNull { element.setAttribute("fill", encodeColor(this)) }
        stroke.notNull { element.setAttribute("stroke", encodeColor(this)) }
    }

    override fun toString(): String {
        return "$id (${this.hashCode()})"
    }
}
