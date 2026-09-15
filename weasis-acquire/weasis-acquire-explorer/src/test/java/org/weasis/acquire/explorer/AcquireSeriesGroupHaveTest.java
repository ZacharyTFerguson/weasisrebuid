/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.acquire.explorer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.weasis.acquire.explorer.core.bean.DefaultTaggable;
import org.weasis.acquire.explorer.core.bean.Global;
import org.weasis.acquire.explorer.core.bean.SeriesGroup;
import org.weasis.acquire.explorer.gui.dialog.AcquireNewSeriesDialog;
import org.weasis.core.api.media.data.TagW;

class AcquireSeriesGroupHaveTest {

  @Test
  void seriesGroupHoldsNamedMediaAndNotifiesListener() {
    SeriesGroup group = new SeriesGroup(SeriesGroup.Type.NAME, "chest");
    assertEquals(SeriesGroup.Type.NAME, group.getType());
    assertEquals("chest", group.getName());
    assertEquals("chest", group.getTagValue(TagW.SeriesDescription));

    AcquireImageInfo still = new AcquireImageInfo();
    AcquireMediaInfo other = new AcquireMediaInfo();
    AtomicInteger events = new AtomicInteger();
    group.addSeriesListener(series -> events.incrementAndGet());
    group.add(still);
    group.add(other);
    group.add(still);

    assertEquals(2, group.getMedia().size());
    assertEquals(1, group.getImages().size());
    assertSame(group, still.getSeriesGroup());
    assertSame(still, group.getImages().get(0));
    assertEquals(2, events.get());
    assertTrue(group.remove(other));
    assertEquals(1, group.getMedia().size());
    assertEquals(3, events.get());
  }

  @Test
  void newSeriesDialogCreatesNameGroupingSeries() {
    AcquireImageInfo a = new AcquireImageInfo();
    AcquireImageInfo b = new AcquireImageInfo();
    AcquireNewSeriesDialog dialog = new AcquireNewSeriesDialog();
    dialog.setSeriesName(" hand ");
    SeriesGroup group = dialog.createSeries(List.of(a, b));
    assertEquals(SeriesGroup.Type.NAME, group.getType());
    assertEquals("hand", group.getName());
    assertEquals(2, group.getImages().size());
    assertEquals("seriesName", dialog.nameField().getName());
  }

  @Test
  void mediaImporterFactoryImportsStillsIntoManagerAndSeries(@TempDir Path dir) throws Exception {
    Path png = file(dir, "a.png");
    Path txt = file(dir, "notes.txt");
    Path jpg = file(dir, "b.jpg");
    AcquireManager manager = new AcquireManager();
    AcquireNewSeriesDialog dialog = new AcquireNewSeriesDialog();
    dialog.setSeriesName("camera");
    SeriesGroup series = dialog.createSeries();
    MediaImporterFactory factory = new MediaImporterFactory();
    List<AcquireImageInfo> imported =
        factory.importStills(manager, series, List.of(png, txt, jpg));

    assertEquals(2, imported.size());
    assertEquals(2, manager.getImages().size());
    assertEquals(1, manager.getSeriesGroups().size());
    assertSame(series, manager.getSeriesGroups().get(0));
    assertEquals(2, series.getImages().size());
    assertTrue(manager.getImages().stream().allMatch(AcquireImageInfo::toPublish));
    assertEquals(AcquireImageStatus.TO_PUBLISH, imported.get(0).getStatus());

    imported.get(0).markPublished();
    assertEquals(AcquireImageStatus.PUBLISHED, imported.get(0).getStatus());
    assertFalse(imported.get(0).toPublish());
    assertTrue(imported.get(1).toPublish());

    MediaImporterFactory halted = new MediaImporterFactory();
    halted.stop();
    assertTrue(halted.isStopped());
    assertTrue(halted.importStills(manager, List.of(png)).isEmpty());
  }

  @Test
  void globalAndDefaultTaggableStorePatientLevelTags() {
    List<String> seen = new ArrayList<>();
    DefaultTaggable tags = new DefaultTaggable();
    tags.addPropertyChangeListener(evt -> seen.add(String.valueOf(evt.getNewValue())));
    tags.setTag(TagW.PatientID, "ID-9");
    assertEquals("ID-9", tags.getTagValue(TagW.PatientID));
    assertEquals(List.of("ID-9"), seen);

    PatientDemographics demo =
        new PatientDemographics("DOE^JANE", "P-1", "19700101", "F", "ACC-7");
    AcquireManager manager = new AcquireManager();
    manager.setDemographics(demo);
    Global global = manager.getGlobal();
    assertEquals("DOE^JANE", global.getTagValue(TagW.PatientName));
    assertEquals("P-1", global.getTagValue(TagW.PatientID));
    assertEquals("19700101", global.getTagValue(TagW.PatientBirthDate));
    assertEquals("F", global.getTagValue(TagW.PatientSex));
    assertEquals("ACC-7", global.getTagValue(TagW.AccessionNumber));
    assertTrue(global.hasPatientTags());

    manager.loadPatientContext("");
    assertFalse(manager.getGlobal().hasPatientTags());
  }

  static Path file(Path dir, String name) throws Exception {
    Path path = dir.resolve(name);
    Files.writeString(path, name);
    return path;
  }
}
