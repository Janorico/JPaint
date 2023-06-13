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

package de.janorico.jpaint.svg

import de.janorico.jpaint.*
import de.janorico.jpaint.svg.shapes.*
import org.w3c.dom.*
import java.awt.Graphics
import java.io.*
import java.util.logging.Level
import javax.swing.filechooser.FileNameExtensionFilter
import kotlin.random.Random

/**
 * @constructor Creates an SVG image with the given shapes.
 * @param width The width for the image.
 * @param height The height for the image.
 * @param shapes The shapes for the image.
 * @see SVGImage.read
 */
class SVGImage(var width: Int, var height: Int, private val shapes: ArrayList<SVGShape>) {
    companion object {
        val svgFilter = FileNameExtensionFilter("SVG Files (*.svg)", "svg")
        const val svgNS = "http://www.w3.org/2000/svg"

        /**
         * Reads the given file.
         * @param file The file to read.
         * @return The image from the file.
         */
        fun read(file: File): SVGImage? {
            logger.log(Level.FINE, "Try parsing file $file...")
            try {
                val doc = XML.read(file)
                val element = doc.documentElement ?: throw IOException("Document has no root element!")
                if (element.tagName != "svg") throw IOException("Root elements tag name is not 'svg'!")
                // Width and height
                val width = element.getAttribute("width").toIntOrNull() ?: throw IOException("Image doesn't have a width attribute!")
                val height = element.getAttribute("height").toIntOrNull() ?: throw IOException("Image doesn't have a height attribute!")
                // Parse shapes
                val shapes = ArrayList<SVGShape>()
                val children = element.childNodes
                for (i in 0 until children.length) {
                    val rawChild = children.item(i)
                    if (rawChild !is Element) continue
                    val child: Element = rawChild
                    fun getExceptionMessage(attributeName: String, tagName: String): String = "Can't get '$attributeName' attribute on $tagName, child $i."
                    shapes.add(
                        when (child.tagName) {
                            "rect" -> {
                                SVGRect(child.getAttribute("x").toFloatOrNull() ?: throw IOException(getExceptionMessage("x", child.tagName)),
                                    child.getAttribute("y").toFloatOrNull() ?: throw IOException(getExceptionMessage("y", child.tagName)),
                                    child.getAttribute("width").toFloatOrNull() ?: throw IOException(getExceptionMessage("width", child.tagName)),
                                    child.getAttribute("height").toFloatOrNull() ?: throw IOException(getExceptionMessage("height", child.tagName)),
                                    child.getAttribute("id").ifEmpty { generateId("rect") },
                                    child.getAttribute("fill").let {
                                        if (it.isEmpty()) null else SVGShape.decodeColor(it)
                                    },
                                    child.getAttribute("stroke").let {
                                        if (it.isEmpty()) null else SVGShape.decodeColor(it)
                                    })
                            }

                            "circle" -> {
                                SVGCircle(child.getAttribute("r").toFloatOrNull() ?: throw IOException(getExceptionMessage("r", child.tagName)),
                                    child.getAttribute("cx").toFloatOrNull() ?: throw IOException(getExceptionMessage("cx", child.tagName)),
                                    child.getAttribute("cy").toFloatOrNull() ?: throw IOException(getExceptionMessage("cy", child.tagName)),
                                    child.getAttribute("id").ifEmpty { generateId("circle") },
                                    child.getAttribute("fill").let {
                                        if (it.isEmpty()) null else SVGShape.decodeColor(it)
                                    },
                                    child.getAttribute("stroke").let {
                                        if (it.isEmpty()) null else SVGShape.decodeColor(it)
                                    })
                            }

                            "ellipse" -> {
                                SVGEllipse(child.getAttribute("rx").toFloatOrNull() ?: throw IOException(getExceptionMessage("rx", child.tagName)),
                                    child.getAttribute("ry").toFloatOrNull() ?: throw IOException(getExceptionMessage("ry", child.tagName)),
                                    child.getAttribute("cx").toFloatOrNull() ?: throw IOException(getExceptionMessage("cx", child.tagName)),
                                    child.getAttribute("cy").toFloatOrNull() ?: throw IOException(getExceptionMessage("cy", child.tagName)),
                                    child.getAttribute("id").ifEmpty { generateId("ellipse") },
                                    child.getAttribute("fill").let {
                                        if (it.isEmpty()) null else SVGShape.decodeColor(it)
                                    },
                                    child.getAttribute("stroke").let {
                                        if (it.isEmpty()) null else SVGShape.decodeColor(it)
                                    })
                            }

                            "line" -> {
                                SVGLine(child.getAttribute("x1").toFloatOrNull() ?: throw IOException(getExceptionMessage("x1", child.tagName)),
                                    child.getAttribute("y1").toFloatOrNull() ?: throw IOException(getExceptionMessage("y1", child.tagName)),
                                    child.getAttribute("x2").toFloatOrNull() ?: throw IOException(getExceptionMessage("x2", child.tagName)),
                                    child.getAttribute("y2").toFloatOrNull() ?: throw IOException(getExceptionMessage("y2", child.tagName)),
                                    child.getAttribute("id").ifEmpty { generateId("line") },
                                    child.getAttribute("stroke").let {
                                        if (it.isEmpty()) null else SVGShape.decodeColor(it)
                                    })
                            }

                            else -> {
                                UnknownSVGShape(child.tagName, buildMap {
                                    val attributes = child.attributes
                                    for (j in 0 until attributes.length) {
                                        val attr = attributes.item(j) as Attr
                                        this[attr.name] = attr.value
                                    }
                                }, child.getAttribute("id").ifEmpty { generateId("unknown") })
                            }
                        }
                    )
                }
                // Return image
                logger.log(Level.INFO, "Successful parsed file $file.")
                return SVGImage(width, height, shapes)
            } catch (e: Throwable) {
                exception("Error while parsing file $file!", e)
            }
            return null
        }

        fun generateId(idPrefix: String): String = idPrefix + Random.nextInt(1000).toString()
    }

    var shapeListener: ShapeListener? = null

    /**
     * Creates an empty SVG image.
     * @param width The width for the image.
     * @param height The height for the image.
     */
    constructor(width: Int, height: Int) : this(width, height, ArrayList())

    /**
     * Paints the SVG image to the given image.
     * @param g The graphics object.
     */
    fun paint(g: Graphics, offsetX: Int = 0, offsetY: Int = 0) {
        for (shape in shapes) shape.paint(g, offsetX, offsetY)
    }

    /**
     * Creates XML for the image.
     * @param doc The document for creating the elements.
     * @see Document.createElementNS
     */
    private fun toXML(doc: Document): Element {
        val element = doc.createElementNS(svgNS, "svg")
        element.setAttribute("width", width.toString())
        element.setAttribute("height", height.toString())
        element.setAttribute("viewBox", "0 0 $width $height")
        element.setAttribute("fill", "none")
        for (shape in shapes) element.appendChild(shape.toXML(doc))
        return element
    }

    /**
     * Creates XML and puts it to the given file.
     * @see toXML
     */
    fun write(file: File) {
        val doc = XML.newDocument()
        doc.appendChild(toXML(doc))
        XML.write(file, doc)
    }

    fun addShape(shape: SVGShape) {
        shapes.add(shape)
        shapeListener?.shapeAdded()
    }

    fun removeShape(index: Int) {
        shapes.removeAt(index)
        shapeListener?.shapeRemoved()
    }

    fun raiseShape(index: Int) {
        val shape = shapes.removeAt(index)
        shapes.add(index - 1, shape)
        shapeListener?.shapeRaised()
    }

    fun lowerShape(index: Int) {
        val shape = shapes.removeAt(index)
        shapes.add(index + 1, shape)
        shapeListener?.shapeLowered()
    }

    fun getShape(index: Int): SVGShape = shapes[index]

    fun getShapes(): Array<SVGShape> = shapes.toTypedArray()

    fun getHashCodeString(): String = "${this.hashCode()}, ${shapes.hashCode()}"

    fun clone(): SVGImage {
        val newList = ArrayList<SVGShape>()
        for (shape in shapes) {
            newList.add(shape.clone())
        }
        return SVGImage(width, height, newList)
    }

    interface ShapeListener {
        fun shapeAdded()
        fun shapeRemoved()
        fun shapeRaised()
        fun shapeLowered()
    }
}
