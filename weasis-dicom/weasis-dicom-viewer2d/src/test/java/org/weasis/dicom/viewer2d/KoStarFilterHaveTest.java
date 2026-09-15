/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.dicom.viewer2d;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.image.BufferedImage;
import org.dcm4che3.data.Attributes;
import org.dcm4che3.data.Sequence;
import org.dcm4che3.data.Tag;
import org.dcm4che3.data.UID;
import org.dcm4che3.data.VR;
import org.junit.jupiter.api.Test;
import org.weasis.core.api.media.data.ImageElement;
import org.weasis.core.api.media.data.Series;
import org.weasis.core.api.media.data.TagW;
import org.weasis.dicom.codec.DicomMediaIO;
import org.weasis.dicom.codec.KOSpecialElement;

class KoStarFilterHaveTest {

  @Test
  void filterShowsOnlyStarredSeriesFramesAndJumpsOffHidden() {
    ImageElement a = image("2.25.a");
    ImageElement b = image("2.25.b");
    ImageElement c = image("2.25.c");
    Series<ImageElement> series = new Series<>();
    series.addMedia(a);
    series.addMedia(b);
    series.addMedia(c);
    View2d view = new View2d();
    view.setSeries(series);
    view.setFrameIndex(1);
    KeyObjectToolBar bar = new KeyObjectToolBar();
    bar.bind(view);
    assertTrue(bar.toggle("2.25.a"));
    assertTrue(bar.toggle("2.25.c"));
    assertEquals(3, view.visibleMedias().size());
    assertTrue(bar.filter());
    assertEquals(2, view.visibleMedias().size());
    assertEquals("2.25.a", KOManager.sopOf(view.visibleMedias().getFirst()));
    assertEquals("2.25.c", KOManager.sopOf(view.visibleMedias().get(1)));
    assertEquals(0, view.getFrameIndex());
    assertFalse(bar.filter());
    assertEquals(3, view.visibleMedias().size());
  }

  @Test
  void koDocumentRefsBecomeKeyImagesAndTurnFilterOn() {
    Attributes dcm = new Attributes();
    dcm.setString(Tag.SOPClassUID, VR.UI, UID.KeyObjectSelectionDocumentStorage);
    Sequence seriesSeq = dcm.newSequence(Tag.ReferencedSeriesSequence, 1);
    Attributes seriesItem = new Attributes();
    Sequence sops = seriesItem.newSequence(Tag.ReferencedSOPSequence, 2);
    sops.add(ref("2.25.a"));
    sops.add(ref("2.25.c"));
    seriesSeq.add(seriesItem);
    KOSpecialElement ko =
        new KOSpecialElement(new DicomMediaIO(dcm, UID.ExplicitVRLittleEndian));
    KOManager manager = new KOManager();
    assertEquals(2, manager.applyDocument(ko));
    assertTrue(manager.isFilterKeyImages());
    assertTrue(manager.isKeyImage("2.25.a"));
    assertTrue(manager.isKeyImage("2.25.c"));
    assertFalse(manager.isKeyImage("2.25.b"));
  }

  static ImageElement image(String sop) {
    ImageElement image = new ImageElement();
    image.setImage(new BufferedImage(4, 2, BufferedImage.TYPE_INT_RGB));
    image.setTag(TagW.SOPInstanceUID, sop);
    return image;
  }

  static Attributes ref(String sop) {
    Attributes item = new Attributes();
    item.setString(Tag.ReferencedSOPInstanceUID, VR.UI, sop);
    return item;
  }
}
