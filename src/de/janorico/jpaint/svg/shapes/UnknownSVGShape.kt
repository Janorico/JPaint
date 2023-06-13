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

class UnknownSVGShape(var tagName: String, var otherAttributes: Map<String, String>, override var id: String) : SVGShape() {
    override var fill: Color? = null
    override var stroke: Color? = null

    override fun paint(g: Graphics, offsetX: Int, offsetY: Int) {}

    override fun isOnPoint(point: Point): Boolean {
        return false
    }

    override fun move(x: Int, y: Int) {}

    override fun toXML(doc: Document): Element {
        val element = doc.createElement(tagName)
        otherAttributes.forEach {
            element.setAttribute(it.key, it.value)
        }
        element.setAttribute("id", id)
        return element
    }

    override fun clone(): UnknownSVGShape {
        return UnknownSVGShape(tagName, otherAttributes, id)
    }
}
