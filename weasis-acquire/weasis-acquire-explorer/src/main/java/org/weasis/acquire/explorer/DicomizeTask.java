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
import java.util.Properties;
import org.weasis.acquire.explorer.dicom.Transform2Dicom;

/** Background-friendly wrapper around {@link Transform2Dicom}. */
public class DicomizeTask {

  private final AcquireManager manager;
  private final Path destination;
  private final Properties preferences;

  public DicomizeTask(AcquireManager manager, Path destination, Properties preferences) {
    this.manager = manager;
    this.destination = destination;
    this.preferences = preferences == null ? new Properties() : preferences;
  }

  public int run() {
    return new Transform2Dicom().dicomize(manager, destination, preferences);
  }
}
