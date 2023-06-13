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

package de.janorico.jpaint

import de.janorico.jgl.helpers.*
import de.janorico.jgl.helpers.Button
import de.janorico.jgl.helpers.Dialog
import java.awt.*
import java.awt.datatransfer.StringSelection
import java.util.logging.*
import javax.swing.*

class GUIHandler : Handler() {
    override fun publish(record: LogRecord?) {
        if (record == null || !isLoggable(record)) return
        when (record.level) {
            Level.SEVERE -> showTextDialog("Error at ${record.sourceClassName}", UIManager.getIcon("OptionPane.errorIcon"), record)
            Level.WARNING -> showTextDialog("Warning at ${record.sourceClassName}", UIManager.getIcon("OptionPane.warningIcon"), record)
            else -> OptionPane.showInformation(formatter.format(record))
        }
    }

    override fun flush() {}

    override fun close() {}

    private fun showTextDialog(title: String, icon: Icon, record: LogRecord) {
        val text = formatter.format(record)
        Dialog.showDialog(title, { dialog: JDialog ->
            dialog.add(JLabel(icon).apply { verticalAlignment = JLabel.TOP }, BorderLayout.WEST)
            return@showDialog JScrollPane(JLabel("<html><pre>$text</pre></html>"))
        }, { dialog: JDialog ->
            JPanel(FlowLayout(FlowLayout.RIGHT, 5, 5)).apply {
                this.add(Button.create("Copy") {
                    (it.source as JButton).apply {
                        val originalText = this.text
                        this.text = "\u2705 Copied"
                        ((Timer(3000) { this.text = originalText }).apply {
                            this.isRepeats = false
                            this.start()
                        })
                    }
                    StringSelection(text).apply {
                        Toolkit.getDefaultToolkit().systemClipboard.setContents(this, this)
                    }
                })
                val closeButton = Button.create("Close") { dialog.dispose() }
                dialog.rootPane.defaultButton = closeButton
                this.add(closeButton)
            }
        })
    }
}
