/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.acquire.explorer;

import java.time.Duration;
import java.time.Instant;

/**
 * EXIF → DICOM: Orientation; Image Description → Image Comments; Manufacturer; Camera Model →
 * Manufacturer Model Name; Date/Time Original else Date/Time → ContentDate/Time. Invalid dates
 * (future +1 day or older than 30 years) fall back to file last-modified.
 */
public final class ExifMapper {

  public record ExifTags(
      Integer orientation,
      String imageDescription,
      String manufacturer,
      String cameraModel,
      Instant dateTimeOriginal,
      Instant dateTime) {}

  public record DicomMapped(
      Integer orientation,
      String imageComments,
      String manufacturer,
      String manufacturerModelName,
      Instant contentDateTime) {}

  private ExifMapper() {}

  public static DicomMapped map(ExifTags tags, Instant fileLastModified, Instant now) {
    ExifTags t = tags == null ? new ExifTags(null, null, null, null, null, null) : tags;
    Instant dt = t.dateTimeOriginal() != null ? t.dateTimeOriginal() : t.dateTime();
    Instant clock = now == null ? Instant.now() : now;
    Instant file = fileLastModified == null ? clock : fileLastModified;
    Instant content;
    if (dt == null) {
      content = file;
    } else if (dt.isAfter(clock.plus(Duration.ofDays(1)))
        || dt.isBefore(clock.minus(Duration.ofDays(365L * 30L)))) {
      content = file;
    } else {
      content = dt;
    }
    return new DicomMapped(
        t.orientation(), t.imageDescription(), t.manufacturer(), t.cameraModel(), content);
  }
}
