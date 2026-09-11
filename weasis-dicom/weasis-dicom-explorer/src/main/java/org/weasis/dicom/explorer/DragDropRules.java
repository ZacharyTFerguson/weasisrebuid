/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.dicom.explorer;

import java.io.File;
import java.nio.file.Files;
import java.util.Locale;
import org.weasis.core.api.media.MimeInspector;

/** Local drag-drop: DICOM file, folder, ZIP, DICOMDIR. Others rejected. */
public final class DragDropRules {

  public enum Kind {
    DICOM_FILE,
    FOLDER,
    ZIP,
    DICOMDIR,
    REJECT
  }

  private DragDropRules() {}

  public static Kind classify(File file) {
    if (file == null || !file.exists()) {
      return Kind.REJECT;
    }
    if (file.isDirectory()) {
      return Kind.FOLDER;
    }
    String name = file.getName().toLowerCase(Locale.ROOT);
    if ("dicomdir".equals(name)) {
      return Kind.DICOMDIR;
    }
    if (name.endsWith(".zip")) {
      return Kind.ZIP;
    }
    String mime = MimeInspector.getMimeType(file);
    if (MimeInspector.DICOM_MIME.equals(mime) || looksLikeDicom(file)) {
      return Kind.DICOM_FILE;
    }
    return Kind.REJECT;
  }

  public static boolean looksLikeDicom(File file) {
    if (file == null || !file.isFile()) {
      return false;
    }
    try (var in = Files.newInputStream(file.toPath())) {
      byte[] buf = in.readNBytes(132);
      if (buf.length < 132) {
        return false;
      }
      return buf[128] == 'D' && buf[129] == 'I' && buf[130] == 'C' && buf[131] == 'M';
    } catch (Exception e) {
      return false;
    }
  }
}
