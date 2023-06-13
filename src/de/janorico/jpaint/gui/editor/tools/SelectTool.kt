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
import java.awt.event.MouseEvent

class SelectTool(override val redrawRequest: () -> Unit, override val getShapeParameters: (idPrefix: String) -> ShapeParameters) : Tool("Select", GUI.getIcon("icons8-cursor-48")) {
    override fun mouseClicked(e: MouseEvent, editor: EditorFrame) {
        editor.selectedIndex = editor.getShapeOnPoint(e.point)
    }
}
