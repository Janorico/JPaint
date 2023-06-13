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

import de.janorico.jgl.helpers.MenuItem
import de.janorico.jpaint.gui.GUI
import java.awt.event.*
import javax.swing.*

class EditorMenuBar(editor: EditorFrame) : JMenuBar() {
    init {
        add(JMenu("File").apply {
            this.setMnemonic('F')
            this.add(MenuItem.create("New", GUI.getIcon("icons8-add-16"), KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_DOWN_MASK)) { editor.new() })
            this.add(MenuItem.create("Open", GUI.getIcon("icons8-folder-16"), KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_DOWN_MASK)) { editor.open() })
            this.addSeparator()
            this.add(MenuItem.create("Save", GUI.getIcon("icons8-save-16"), KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK)) { editor.save(false) })
            this.add(MenuItem.create(
                "Save As", GUI.getIcon("icons8-save-as-16"), KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK + InputEvent.SHIFT_DOWN_MASK)
            ) { editor.save(true) })
            this.add(MenuItem.create("Export", GUI.getIcon("icons8-export-16"), KeyStroke.getKeyStroke(KeyEvent.VK_E, InputEvent.CTRL_DOWN_MASK)) { editor.export() })
            this.addSeparator()
            this.add(MenuItem.create("Close", GUI.getIcon("icons8-close-16"), KeyStroke.getKeyStroke(KeyEvent.VK_W, InputEvent.CTRL_DOWN_MASK)) { editor.close() })
            this.add(MenuItem.create("Exit", GUI.getIcon("icons8-close-16"), KeyStroke.getKeyStroke(KeyEvent.VK_Q, InputEvent.CTRL_DOWN_MASK)) { editor.exit() })
        })
        add(JMenu("Edit").apply {
            this.setMnemonic('E')
            this.add(MenuItem.create("Undo", GUI.getIcon("icons8-undo-16"), KeyStroke.getKeyStroke(KeyEvent.VK_Z, InputEvent.CTRL_DOWN_MASK)) { editor.undo() })
            this.add(MenuItem.create(
                "Redo", GUI.getIcon("icons8-redo-16"), KeyStroke.getKeyStroke(KeyEvent.VK_Z, InputEvent.CTRL_DOWN_MASK + InputEvent.SHIFT_DOWN_MASK)
            ) { editor.redo() })
            this.addSeparator()
            this.add(MenuItem.create("Resize Image", GUI.getIcon("icons8-resize-16")) { editor.resizeImage() })
            this.addSeparator()
            this.add(MenuItem.create("Settings", GUI.getIcon("icons8-gear-16")) { GUI.settingsDialog() })
        })
        add(JMenu("Help").apply {
            this.setMnemonic('H')
            this.add(MenuItem.create("About", GUI.getIcon("icons8-about-16"), 'A') { GUI.aboutDialog() })
        })
    }
}
