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

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import org.dcm4che3.data.Attributes;
import org.dcm4che3.data.Tag;
import org.dcm4che3.io.DicomInputStream;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.weasis.dicom.codec.utils.InstanceSpacing;

/**
 * Instance stack paging in {@link View2d}: swap the displayed DICOM instance when the frame index
 * changes.
 *
 * <p><b>Tags:</b> (0020,000E) {@code SeriesInstanceUID} — one stack is one series; mixing UIDs
 * would juxtapose unrelated slices as if they were a volume. (0020,0013) {@code InstanceNumber}
 * — primary sort key for instance order within that series (filename order is only a tie-break when
 * the tag repeats). (0020,0032) {@code ImagePositionPatient} — lets tests prove the view bound the
 * dataset for the chosen index, not a stale handle. (0028,0008) {@code NumberOfFrames} — read
 * only to refuse values &gt; 1: this slice pages <em>instances</em> (separate objects), not frame
 * offsets inside one object's {@code PixelData}; showing frame 0 under another index would lie
 * about which anatomy is on screen.
 *
 * <p><b>Why re-resolve per instance:</b> {@code PixelSpacing}, default W/L, rescale, and geometry
 * warnings live on each instance's dataset. Paging must call {@code applyDatasetFlags} and repaint
 * from the instance at {@code frameIndex}, not cache spacing or tags at series load time — slice 1
 * already proved two instances in one JVM need independent spacing.
 *
 * <p><b>Why fail-closed:</b> mixed {@code SeriesInstanceUID} or multi-frame objects are rejected
 * with {@code IllegalArgumentException} instead of silently keeping the first file or the first
 * frame; a failed {@code loadStack} leaves the previous stack visible.
 *
 * <p><b>Why not copy Weasis:</b> upstream {@code SeriesComparator} and stack builders handle KO,
 * multiframe cine, and missing {@code InstanceNumber} heuristics we do not need for this oracle;
 * a minimal sort-by-{@code InstanceNumber} plus explicit refusal matches the honesty contract
 * without importing comparator logic.
 *
 * <p>This is <em>instance paging</em>, not multi-frame scroll; the tracker row for stack scroll
 * stays partial until a later slice paints frame N from byte offsets.
 */
class View2dStackPagingTest {

  @Test
  void pageNextLoadsSecondInstance(@TempDir Path dir) throws Exception {
    Path pack = roundtripDir();
    Assumptions.assumeTrue(Files.isDirectory(pack));
    File s01 = pack.resolve("ct_brain_ax_s01_256.dcm").toFile();
    File s02 = pack.resolve("ct_brain_ax_s02_256.dcm").toFile();
    Assumptions.assumeTrue(s01.isFile() && s02.isFile());

    View2d view = new View2d();
    view.loadStack(List.of(s01, s02));
    byte[] atZero = paintedBytes(view);
    assertEquals(1, view.getDataset().getInt(Tag.InstanceNumber, -1));

    view.setFrameIndex(1);
    assertEquals(1, view.getFrameIndex());
    assertEquals(2, view.getDataset().getInt(Tag.InstanceNumber, -1));
    assertEquals(12.5, view.getDataset().getDoubles(Tag.ImagePositionPatient)[2], 1e-6);
    assertNotEquals(atZero, paintedBytes(view));
  }

  @Test
  void pageClampsAtBothEnds(@TempDir Path dir) throws Exception {
    Path pack = roundtripDir();
    Assumptions.assumeTrue(Files.isDirectory(pack));
    File s01 = pack.resolve("ct_brain_ax_s01_256.dcm").toFile();
    File s02 = pack.resolve("ct_brain_ax_s02_256.dcm").toFile();
    Assumptions.assumeTrue(s01.isFile() && s02.isFile());

    View2d view = new View2d();
    view.loadStack(List.of(s01, s02));
    view.setFrameIndex(99);
    assertEquals(1, view.getFrameIndex());
    assertEquals(2, view.getDataset().getInt(Tag.InstanceNumber, -1));

    view.setFrameIndex(-3);
    assertEquals(0, view.getFrameIndex());
    assertEquals(1, view.getDataset().getInt(Tag.InstanceNumber, -1));
  }

  @Test
  void scrollCommandNextPrevDrivesStack() throws Exception {
    Path pack = roundtripDir();
    Assumptions.assumeTrue(Files.isDirectory(pack));
    File s01 = pack.resolve("ct_brain_ax_s01_256.dcm").toFile();
    File s02 = pack.resolve("ct_brain_ax_s02_256.dcm").toFile();
    Assumptions.assumeTrue(s01.isFile() && s02.isFile());

    View2d view = new View2d();
    view.loadStack(List.of(s01, s02));
    byte[] first = paintedBytes(view);
    DicomView2dCommands cmd = new DicomView2dCommands(view);
    cmd.scroll("-i");
    assertEquals(1, view.getFrameIndex());
    assertNotEquals(first, paintedBytes(view));
    cmd.scroll("-d");
    assertEquals(0, view.getFrameIndex());
    assertArrayEquals(first, paintedBytes(view));
  }

  @Test
  void stackSortedByInstanceNumberNotFilename(@TempDir Path dir) throws Exception {
    Path pack = roundtripDir();
    Assumptions.assumeTrue(Files.isDirectory(pack));
    File s01 = pack.resolve("ct_brain_ax_s01_256.dcm").toFile();
    File s02 = pack.resolve("ct_brain_ax_s02_256.dcm").toFile();
    Assumptions.assumeTrue(s01.isFile() && s02.isFile());

    View2d view = new View2d();
    view.loadStack(List.of(s02, s01));
    assertEquals(1, view.getDataset().getInt(Tag.InstanceNumber, -1));
    view.setFrameIndex(1);
    assertEquals(2, view.getDataset().getInt(Tag.InstanceNumber, -1));
  }

  @Test
  void mixedSeriesInstanceUidIsRefused(@TempDir Path dir) throws Exception {
    Path pack = roundtripDir();
    Assumptions.assumeTrue(Files.isDirectory(pack));
    File s01 = pack.resolve("ct_brain_ax_s01_256.dcm").toFile();
    File s02 = pack.resolve("ct_brain_ax_s02_256.dcm").toFile();
    Assumptions.assumeTrue(s01.isFile() && s02.isFile());
    File other = StackPagingFixtures.writeOtherSeriesCt(dir.resolve("other.dcm").toFile());

    View2d view = new View2d();
    view.loadStack(List.of(s01, s02));
    int instanceBefore = view.getDataset().getInt(Tag.InstanceNumber, -1);
    byte[] paintedBefore = paintedBytes(view);

    assertThrows(
        IllegalArgumentException.class, () -> view.loadStack(List.of(s01, other)));
    assertEquals(instanceBefore, view.getDataset().getInt(Tag.InstanceNumber, -1));
    assertArrayEquals(paintedBefore, paintedBytes(view));
  }

  @Test
  void multiFrameInstanceIsRefused(@TempDir Path dir) throws Exception {
    File mf = StackPagingFixtures.writeMultiframeCt(dir.resolve("mf.dcm").toFile());
    View2d view = new View2d();
    assertThrows(IllegalArgumentException.class, () -> view.loadStack(List.of(mf)));
    String warn = view.getGeometryWarning().toLowerCase();
    assertTrue(
        warn.contains("multi-frame") || warn.contains("numberofframes"),
        () -> "expected geometry warning to name multi-frame, was: " + view.getGeometryWarning());
  }

  @Test
  void spacingFollowsPagedInstance(@TempDir Path dir) throws Exception {
    Path pack = roundtripDir();
    Assumptions.assumeTrue(Files.isDirectory(pack));
    File s01 = pack.resolve("ct_brain_ax_s01_256.dcm").toFile();
    Assumptions.assumeTrue(s01.isFile());
    Attributes ref = readDataset(s01.toPath());
    String seriesUid = ref.getString(Tag.SeriesInstanceUID);
    File iso =
        StackPagingFixtures.writeCtInstance(
            dir.resolve("iso050.dcm").toFile(), seriesUid, 2, 0.50, 0.50, 5.0, 200);

    View2d view = new View2d();
    view.loadStack(List.of(s01, iso));
    double row0 =
        InstanceSpacing.resolve(view.getDataset()).orElseThrow().spacing().rowMm();
    assertEquals(0.80, row0, 1e-9);

    view.setFrameIndex(1);
    double row1 =
        InstanceSpacing.resolve(view.getDataset()).orElseThrow().spacing().rowMm();
    assertEquals(0.50, row1, 1e-9);
  }

  private static byte[] paintedBytes(View2d view) {
    BufferedImage img = view.getSourceImage();
    return Arrays.copyOf(
        ((DataBufferByte) img.getRaster().getDataBuffer()).getData(),
        ((DataBufferByte) img.getRaster().getDataBuffer()).getData().length);
  }

  static Path roundtripDir() {
    Path module = Path.of(System.getProperty("basedir", System.getProperty("user.dir")));
    Path fromModule = module.resolve("../../testdata/weasis-roundtrip").normalize();
    if (Files.isDirectory(fromModule)) {
      return fromModule;
    }
    return module.resolve("../../../testdata/weasis-roundtrip").normalize();
  }

  static Attributes readDataset(Path path) throws Exception {
    try (DicomInputStream in = new DicomInputStream(path.toFile())) {
      in.setIncludeBulkData(DicomInputStream.IncludeBulkData.NO);
      return in.readDataset(-1, -1);
    }
  }
}
