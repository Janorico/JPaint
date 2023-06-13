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

import org.w3c.dom.Document
import org.xml.sax.SAXException
import java.io.*
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.transform.*
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.stream.StreamResult

object XML {
    private val builder = DocumentBuilderFactory.newInstance().newDocumentBuilder()

    fun newDocument(): Document = builder.newDocument()

    @Throws(IOException::class, TransformerException::class, TransformerFactoryConfigurationError::class)
    fun write(file: File, doc: Document) {
        val t = TransformerFactory.newInstance().newTransformer()
        if (!file.isFile) {
            file.createNewFile()
        }
        val fos = FileOutputStream(file)
        val sr = StreamResult(fos)
        t.transform(DOMSource(doc), sr)
        fos.close()
    }

    @Throws(IOException::class, SAXException::class)
    fun read(file: File): Document {
        val fis = FileInputStream(file)
        val doc = builder.parse(fis)
        fis.close()
        return doc
    }
}
