/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.acquire.explorer.dicom;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;
import java.util.Properties;
import org.weasis.acquire.explorer.AcquireImageInfo;
import org.weasis.acquire.explorer.AcquireManager;
import org.weasis.acquire.explorer.EncapToDicom;
import org.weasis.acquire.explorer.PatientDemographics;
import org.weasis.acquire.explorer.StillFormats;
import org.weasis.acquire.explorer.StillToDicom;
import org.weasis.acquire.explorer.VideoToDicom;

/** Converts imported images into DICOM Secondary Capture / encapsulated objects (WP-12). */
public class Transform2Dicom {

  public int dicomize(AcquireManager manager, Path destDir) {
    return dicomize(manager, destDir, new Properties());
  }

  public int dicomize(AcquireManager manager, Path destDir, Properties prefs) {
    if (manager == null || destDir == null) {
      return 0;
    }
    try {
      Files.createDirectories(destDir);
    } catch (IOException e) {
      return 0;
    }
    PatientDemographics demo = manager.getDemographics();
    int n = 0;
    for (AcquireImageInfo image : manager.getImages()) {
      if (image == null || image.getFile() == null) {
        continue;
      }
      Path src = image.getFile();
      try {
        Path out = destDir.resolve(baseName(src) + ".dcm");
        if (StillFormats.isStill(src)) {
          StillToDicom.convert(src, out, demo);
          n++;
        } else if (isEncap(src)) {
          EncapToDicom.convert(src, out, demo);
          n++;
        } else if (isVideo(src)) {
          VideoToDicom.convert(
              src, out, guessMime(src), "", "", prefs == null ? new Properties() : prefs, demo);
          n++;
        }
      } catch (Exception ignored) {
        // skip unreadable sources; GUI would surface errors in headed Pass
      }
    }
    return n;
  }

  static String baseName(Path src) {
    String name = src.getFileName().toString();
    int dot = name.lastIndexOf('.');
    return dot < 0 ? name : name.substring(0, dot);
  }

  static boolean isEncap(Path src) {
    String lower = src.getFileName().toString().toLowerCase(Locale.ROOT);
    return lower.endsWith(".pdf") || lower.endsWith(".stl");
  }

  static boolean isVideo(Path src) {
    String lower = src.getFileName().toString().toLowerCase(Locale.ROOT);
    return lower.endsWith(".mpg")
        || lower.endsWith(".mpeg")
        || lower.endsWith(".mp4")
        || lower.endsWith(".mov")
        || lower.endsWith(".hevc");
  }

  static String guessMime(Path src) {
    String lower = src.getFileName().toString().toLowerCase(Locale.ROOT);
    if (lower.endsWith(".mpg") || lower.endsWith(".mpeg")) {
      return "video/mpeg";
    }
    if (lower.endsWith(".hevc")) {
      return "video/hevc";
    }
    return "video/mp4";
  }
}
