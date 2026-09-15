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
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.image.BufferedImage;
import org.dcm4che3.data.Attributes;
import org.dcm4che3.data.Sequence;
import org.dcm4che3.data.Tag;
import org.dcm4che3.data.UID;
import org.dcm4che3.data.VR;
import org.junit.jupiter.api.Test;
import org.weasis.core.api.media.data.ImageElement;
import org.weasis.core.api.media.data.MediaElement;
import org.weasis.core.api.media.data.Series;
import org.weasis.core.api.media.data.TagW;
import org.weasis.dicom.codec.DicomMediaIO;
import org.weasis.dicom.codec.KOSpecialElement;

class KoApplyOnOpenHaveTest {

  @Test
  void applyOverlayFiltersStarredSopsWithoutFillingHangSlot() {
    View2dContainer container = new View2dContainer();
    Series<MediaElement> dx = series("2.25.dx.pa", "2.25.a", "2.25.b", "2.25.c");
    container.addSeries(dx);
    container.applyHanging(1, 2);
    assertEquals(1, container.getOpenSeries().size());
    assertSame(dx, container.getLayoutViews().get(0).getSeries());
    assertNull(container.getLayoutViews().get(1).getSeries());
    assertEquals(3, container.getView2d().visibleMedias().size());

    Series<MediaElement> koSeries = new Series<>("2.25.ko");
    koSeries.addMedia(ko("2.25.a", "2.25.c"));
    container.applyOverlay(koSeries);

    assertEquals(1, container.getOpenSeries().size());
    assertNull(container.getLayoutViews().get(1).getSeries());
    assertTrue(container.getView2d().getKoManager().isFilterKeyImages());
    assertTrue(container.getView2d().getKoManager().isKeyImage("2.25.a"));
    assertTrue(container.getView2d().getKoManager().isKeyImage("2.25.c"));
    assertFalse(container.getView2d().getKoManager().isKeyImage("2.25.b"));
    assertEquals(2, container.getView2d().visibleMedias().size());
    assertEquals("2.25.a", KOManager.sopOf(container.getView2d().visibleMedias().getFirst()));
  }

  static Series<MediaElement> series(String uid, String... sops) {
    Series<MediaElement> series = new Series<>(uid);
    for (String sop : sops) {
      series.addMedia(image(sop));
    }
    return series;
  }

  static ImageElement image(String sop) {
    ImageElement image = new ImageElement();
    image.setImage(new BufferedImage(4, 2, BufferedImage.TYPE_INT_RGB));
    image.setTag(TagW.SOPInstanceUID, sop);
    return image;
  }

  static KOSpecialElement ko(String... sops) {
    Attributes dcm = new Attributes();
    dcm.setString(Tag.SOPClassUID, VR.UI, UID.KeyObjectSelectionDocumentStorage);
    Sequence seriesSeq = dcm.newSequence(Tag.ReferencedSeriesSequence, 1);
    Attributes seriesItem = new Attributes();
    Sequence refs = seriesItem.newSequence(Tag.ReferencedSOPSequence, sops.length);
    for (String sop : sops) {
      Attributes item = new Attributes();
      item.setString(Tag.ReferencedSOPInstanceUID, VR.UI, sop);
      refs.add(item);
    }
    seriesSeq.add(seriesItem);
    return new KOSpecialElement(new DicomMediaIO(dcm, UID.ExplicitVRLittleEndian));
  }
}
