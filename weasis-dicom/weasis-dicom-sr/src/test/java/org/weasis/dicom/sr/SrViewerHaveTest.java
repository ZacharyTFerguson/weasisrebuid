/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.dicom.sr;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.image.BufferedImage;
import java.awt.print.PageFormat;
import java.awt.print.Paper;
import java.awt.print.Printable;
import java.util.List;
import javax.swing.AbstractButton;
import org.dcm4che3.data.Attributes;
import org.dcm4che3.data.Sequence;
import org.dcm4che3.data.Tag;
import org.dcm4che3.data.UID;
import org.dcm4che3.data.VR;
import org.junit.jupiter.api.Test;
import org.weasis.core.api.gui.Insertable;

class SrViewerHaveTest {

  @Test
  void imageReferenceResolvesSopInstanceUidAndToolbarPrintsHtml() {
    Attributes sr = imageSr("1.2.840.10008.1.2.1.9");
    SRImageReference ref = SRImageReference.fromContentItem(contentImage(sr));
    assertNotNull(ref);
    assertEquals("1.2.840.10008.1.2.1.9", ref.sopInstanceUid());
    assertEquals(UID.CTImageStorage, ref.sopClassUid());
    assertEquals(2, ref.frames()[0]);

    List<SRImageReference> all = SRImageReference.fromDocument(sr);
    assertEquals(1, all.size());
    assertEquals("1.2.840.10008.1.2.1.9", all.getFirst().sopInstanceUid());

    SRView view = new SRView();
    view.display(sr);
    assertTrue(view.displayedText().contains("1.2.840.10008.1.2.1.9"));
    assertTrue(view.html().contains("<html>"));
    assertTrue(view.html().contains("1.2.840.10008.1.2.1.9"));

    SrToolBar bar = new SrToolBar();
    assertEquals("SR", bar.getComponentName());
    assertEquals(Insertable.Type.TOOLBAR, bar.getType());
    bar.bind(view);
    assertEquals(1, bar.imageReferences().size());
    String html = bar.printHtml();
    assertTrue(html.contains("<h1>"));
    assertTrue(html.contains("Findings"));

    EditorPanePrinter printer = new EditorPanePrinter(html);
    assertEquals(1, printer.pageCount());
    BufferedImage preview = printer.render();
    assertEquals(EditorPanePrinter.PAGE_WIDTH, preview.getWidth());
    Paper paper = new Paper();
    paper.setSize(612, 792);
    paper.setImageableArea(0, 0, 612, 792);
    PageFormat format = new PageFormat();
    format.setPaper(paper);
    assertEquals(Printable.PAGE_EXISTS, printer.print(preview.createGraphics(), format, 0));
    assertEquals(Printable.NO_SUCH_PAGE, printer.print(preview.createGraphics(), format, 1));
    assertFalse(bar.printPreview().getWidth() <= 0);
  }

  @Test
  void srContainerWiresPrintChrome() {
    SRContainer container = new SRContainer();
    assertEquals(SRContainer.NAME, container.getPluginName());
    assertTrue(
        container.getSeriesViewerUI().getToolBar().stream()
            .anyMatch(b -> SrToolBar.NAME.equals(b.getComponentName())));
    AbstractButton print = (AbstractButton) container.getSrToolBar().getComponent(0);
    assertEquals("Print", print.getText());
    assertEquals("printSr", print.getName());
    assertEquals(container.getSRView(), container.getSrToolBar().boundView());
  }

  static Attributes imageSr(String sopInstanceUid) {
    Attributes sr = new Attributes();
    sr.setString(Tag.SOPClassUID, VR.UI, UID.BasicTextSRStorage);
    sr.setString(Tag.Modality, VR.CS, "SR");
    Sequence title = sr.newSequence(Tag.ConceptNameCodeSequence, 1);
    Attributes titleCode = new Attributes();
    titleCode.setString(Tag.CodeMeaning, VR.LO, "Findings");
    title.add(titleCode);
    Sequence content = sr.newSequence(Tag.ContentSequence, 1);
    content.add(imageItem(sopInstanceUid));
    return sr;
  }

  static Attributes contentImage(Attributes sr) {
    return sr.getSequence(Tag.ContentSequence).get(0);
  }

  static Attributes imageItem(String sopInstanceUid) {
    Attributes image = new Attributes();
    image.setString(Tag.RelationshipType, VR.CS, "CONTAINS");
    image.setString(Tag.ValueType, VR.CS, "IMAGE");
    Sequence name = image.newSequence(Tag.ConceptNameCodeSequence, 1);
    Attributes concept = new Attributes();
    concept.setString(Tag.CodeMeaning, VR.LO, "Source image");
    name.add(concept);
    Sequence refs = image.newSequence(Tag.ReferencedSOPSequence, 1);
    Attributes ref = new Attributes();
    ref.setString(Tag.ReferencedSOPClassUID, VR.UI, UID.CTImageStorage);
    ref.setString(Tag.ReferencedSOPInstanceUID, VR.UI, sopInstanceUid);
    ref.setInt(Tag.ReferencedFrameNumber, VR.IS, 2);
    refs.add(ref);
    return image;
  }
}
