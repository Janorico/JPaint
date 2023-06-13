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

import de.janorico.jpaint.gui.editor.EditorFrame
import java.awt.*
import java.awt.event.MouseEvent
import javax.swing.ImageIcon

abstract class Tool(val displayName: String, val icon: ImageIcon) {
    abstract val redrawRequest: () -> Unit
    abstract val getShapeParameters: (idPrefix: String) -> ShapeParameters

    open fun paintDraft(g: Graphics) {}

    open fun mouseClicked(e: MouseEvent, editor: EditorFrame) {}

    open fun mousePressed(e: MouseEvent, editor: EditorFrame) {}

    open fun mouseReleased(e: MouseEvent, editor: EditorFrame) {}

    open fun mouseDragged(e: MouseEvent, editor: EditorFrame) {}

    data class ShapeParameters(val id: String, val fill: Color?, val stroke: Color?)
}

/*
    SELECT("Select", GUI.getIcon("icons8-cursor-48")),
    DRAW_RECT("Draw Rect", GUI.getIcon("rect-48")),
    DRAW_CIRCLE("Draw Circle", GUI.getIcon("circle-48")),
    DRAW_ELLIPSE("Draw Ellipse", GUI.getIcon("ellipse-48")),
    DRAW_LINE("Draw Line", GUI.getIcon("line-48")),
    DRAW_PATH("Draw Path", GUI.getIcon("path-48"));
 */
