/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.core.ui.serialize;

import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

public final class XmlSerializer {
  private XmlSerializer() {}

  public static XMLStreamWriter createWriter(Writer out) throws Exception {
    return XMLOutputFactory.newFactory().createXMLStreamWriter(out);
  }

  public static XMLStreamReader createReader(Reader in) throws Exception {
    return XMLInputFactory.newFactory().createXMLStreamReader(in);
  }

  public static String toXml(String root, String body) {
    try {
      StringWriter sw = new StringWriter();
      XMLStreamWriter w = createWriter(sw);
      w.writeStartDocument();
      w.writeStartElement(root);
      if (body != null) {
        w.writeCharacters(body);
      }
      w.writeEndElement();
      w.writeEndDocument();
      w.close();
      return sw.toString();
    } catch (Exception e) {
      return "";
    }
  }

  public static XMLStreamReader fromXml(String xml) throws Exception {
    return createReader(new StringReader(xml == null ? "" : xml));
  }
}
