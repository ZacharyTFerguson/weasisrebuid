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

import java.nio.file.Path;
import java.util.Locale;
import java.util.Set;

/** Stills: TIFF BMP GIF JPEG PNG RAS HDR PNM. */
public final class StillFormats {

  public static final Set<String> EXTENSIONS =
      Set.of(
          "tif", "tiff", "bmp", "gif", "jpg", "jpeg", "png", "ras", "hdr", "pnm", "pgm", "ppm",
          "pbm");

  private StillFormats() {}

  public static boolean isStill(Path path) {
    if (path == null) {
      return false;
    }
    String name = path.getFileName().toString().toLowerCase(Locale.ROOT);
    int dot = name.lastIndexOf('.');
    if (dot < 0) {
      return false;
    }
    return EXTENSIONS.contains(name.substring(dot + 1));
  }
}
