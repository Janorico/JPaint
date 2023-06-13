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

package de.janorico.jpaint.gui.editor

import de.janorico.jpaint.gui.editor.tools.*
import java.awt.GridLayout
import java.awt.event.*
import javax.swing.*

class Toolbox(redrawRequest: () -> Unit, getShapeParameters: (idPrefix: String) -> Tool.ShapeParameters) : JPanel() {
    private val tools = arrayOf(
        SelectTool(redrawRequest, getShapeParameters),
        MoveTool(redrawRequest, getShapeParameters),
        DrawRectTool(redrawRequest, getShapeParameters),
        DrawCircleTool(redrawRequest, getShapeParameters),
        DrawEllipseTool(redrawRequest, getShapeParameters),
        DrawLineTool(redrawRequest, getShapeParameters),
    )

    var selectedTool: Tool = tools[0]

    private val toolButtons = Array(tools.size) { i: Int ->
        return@Array ToolButton(tools[i], (i + 1).toString()[0].code, i == 0)
    }

    init {
        this.layout = GridLayout(toolButtons.size, 1, 5, 5)
        val buttonGroup = ButtonGroup()
        val actionListener = ActionListener {
            selectedTool = (it.source as ToolButton).tool
        }
        for (button in toolButtons) {
            buttonGroup.add(button)
            button.addActionListener(actionListener)
            this.add(button)
        }
    }

    private class ToolButton(val tool: Tool, keyCode: Int, selected: Boolean = false) : JToggleButton(tool.icon, selected) {
        init {
            this.toolTipText = tool.displayName
            this.registerKeyboardAction({ this.doClick() }, KeyStroke.getKeyStroke(keyCode, InputEvent.CTRL_DOWN_MASK), JComponent.WHEN_IN_FOCUSED_WINDOW)
        }
    }
}
