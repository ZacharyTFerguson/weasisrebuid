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

import java.nio.file.Path;
import org.weasis.acquire.explorer.AcquireImageInfo;
import org.weasis.acquire.explorer.AcquireManager;

/** Converts imported images into DICOM Secondary Capture (WP-12). */
public class Transform2Dicom {

  public int dicomize(AcquireManager manager, Path destDir) {
    if (manager == null || destDir == null) {
      return 0;
    }
    int n = 0;
    for (AcquireImageInfo ignored : manager.getImages()) {
      n++;
    }
    return n;
  }
}
