/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.dicom.codec.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.geom.Ellipse2D;
import java.awt.image.DataBufferByte;
import org.dcm4che3.data.Attributes;
import org.dcm4che3.data.Sequence;
import org.dcm4che3.data.Tag;
import org.dcm4che3.data.VR;
import org.dcm4che3.util.UIDUtils;
import org.junit.jupiter.api.Test;
import org.weasis.dicom.codec.WindowLevelPainter;

/**
 * Why stored PixelData + rescale: HU lives in modality space, not VOI-painted 0–255.
 *
 * <p>Why fail-closed: no BufferedImage input; ModalityLUTSequence present → refuse (slope-only would
 * lie).
 *
 * <p>Why not copy Weasis: WP-5 PixelStatistics reads painted grey — we sample only the dataset.
 */
class RoiStatisticsTest {

  @Test
  void ellipseMeanIsModalityValueNotPaintedGrey() {
    Attributes dcm = ctSmall();
    dcm.setDouble(Tag.RescaleIntercept, VR.DS, -1024);
    dcm.setString(Tag.RescaleType, VR.LO, "HU");
    int[] px = dcm.getInts(Tag.PixelData);
    for (int i = 0; i < px.length; i++) {
      px[i] = 24;
    }
    Ellipse2D roi = new Ellipse2D.Double(0, 0, 4, 4);
    RoiStatistics.RoiStats stats = RoiStatistics.ellipse(dcm, roi).orElseThrow();
    assertEquals(-1000.0, stats.mean(), 1e-6);

    var painted = WindowLevelPainter.paintMonochrome2(dcm, 400, 40);
    byte[] grey = ((DataBufferByte) painted.getRaster().getDataBuffer()).getData();
    double paintedMean = meanPaintedGrey(grey);
    assertNotEquals(stats.mean(), paintedMean, 1.0);
    assertTrue(paintedMean >= 0 && paintedMean <= 255);
  }

  @Test
  void ellipseMeanUsesSlope() {
    Attributes dcm = ctSmall();
    dcm.setDouble(Tag.RescaleSlope, VR.DS, 2);
    dcm.setDouble(Tag.RescaleIntercept, VR.DS, -1024);
    int[] px = dcm.getInts(Tag.PixelData);
    px[5] = 10;
    Ellipse2D roi = new Ellipse2D.Double(1, 1, 2, 2);
    RoiStatistics.RoiStats stats = RoiStatistics.ellipse(dcm, roi).orElseThrow();
    assertEquals(2 * 10 - 1024, stats.mean(), 1e-6);
  }

  @Test
  void paddingPixelsAreExcludedAndCounted() {
    Attributes dcm = ctSmall();
    dcm.setInt(Tag.PixelPaddingValue, VR.US, 0);
    int[] px = dcm.getInts(Tag.PixelData);
    px[0] = 0;
    px[1] = 100;
    px[2] = 100;
    px[3] = 100;
    Ellipse2D roi = new Ellipse2D.Double(0, 0, 2, 2);
    RoiStatistics.RoiStats stats = RoiStatistics.ellipse(dcm, roi).orElseThrow();
    assertEquals(1, stats.excluded());
    assertEquals(3, stats.n());
    assertEquals(100.0, stats.mean(), 1e-6);
  }

  @Test
  void roiUnitIsRescaleTypeNotAlwaysHu() {
    Attributes ct = ctSmall();
    ct.setString(Tag.RescaleType, VR.LO, "HU");
    assertEquals("HU", RoiStatistics.ellipse(ct, fullEllipse()).orElseThrow().unit());

    Attributes dx = ctSmall();
    dx.setString(Tag.Modality, VR.CS, "DX");
    dx.setString(Tag.RescaleType, VR.LO, "US");
    assertEquals("US", RoiStatistics.ellipse(dx, fullEllipse()).orElseThrow().unit());

    Attributes bare = ctSmall();
    bare.removeTag(Tag.RescaleType);
    assertEquals("stored", RoiStatistics.ellipse(bare, fullEllipse()).orElseThrow().unit());
  }

  @Test
  void modalityLutSequence_refusesMean() {
    Attributes dcm = ctSmall();
    Sequence seq = dcm.newSequence(Tag.ModalityLUTSequence, 1);
    seq.add(new Attributes());
    assertTrue(RoiStatistics.ellipse(dcm, fullEllipse()).isEmpty());
  }

  @Test
  void handCountedDiskMeanOnFourByFour() {
    Attributes dcm = ctSmall();
    int[] px = dcm.getInts(Tag.PixelData);
    px[5] = 10;
    px[6] = 30;
    px[9] = 50;
    px[10] = 70;
    Ellipse2D roi = new Ellipse2D.Double(0.5, 0.5, 3, 3);
    RoiStatistics.RoiStats stats = RoiStatistics.ellipse(dcm, roi).orElseThrow();
    assertEquals(4, stats.n());
    assertEquals(40.0, stats.mean(), 1e-6);
  }

  @Test
  void formatMeanLabelUsesRoiStatsNotPaintedGrey() {
    Attributes dcm = ctSmall();
    dcm.setDouble(Tag.RescaleIntercept, VR.DS, -1024);
    dcm.setString(Tag.RescaleType, VR.LO, "HU");
    int[] px = dcm.getInts(Tag.PixelData);
    for (int i = 0; i < px.length; i++) {
      px[i] = 24;
    }
    RoiStatistics.RoiStats stats = RoiStatistics.ellipse(dcm, fullEllipse()).orElseThrow();
    String label = RoiStatistics.formatMeanLabel(stats);
    assertTrue(label.contains("-1000"));
    assertTrue(label.contains("HU"));
    assertTrue(label.contains("n=16"));
  }

  @Test
  void emptyEllipseGivesEmpty() {
    Attributes dcm = ctSmall();
    assertTrue(RoiStatistics.ellipse(dcm, new Ellipse2D.Double(10, 10, 1, 1)).isEmpty());
  }

  static Attributes ctSmall() {
    Attributes dcm = new Attributes();
    dcm.setString(Tag.SOPInstanceUID, VR.UI, UIDUtils.createUID("2.25"));
    dcm.setString(Tag.PhotometricInterpretation, VR.CS, "MONOCHROME2");
    dcm.setInt(Tag.Rows, VR.US, 4);
    dcm.setInt(Tag.Columns, VR.US, 4);
    dcm.setInt(Tag.PixelRepresentation, VR.US, 0);
    dcm.setInt(Tag.BitsAllocated, VR.US, 16);
    dcm.setInt(Tag.PixelData, VR.OW, new int[16]);
    return dcm;
  }

  static Ellipse2D fullEllipse() {
    return new Ellipse2D.Double(0, 0, 4, 4);
  }

  static double meanPaintedGrey(byte[] samples) {
    double sum = 0;
    for (byte b : samples) {
      sum += b & 0xFF;
    }
    return sum / samples.length;
  }
}
