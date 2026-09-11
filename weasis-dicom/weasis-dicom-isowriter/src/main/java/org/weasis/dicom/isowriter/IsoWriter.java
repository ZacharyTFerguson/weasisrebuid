/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.dicom.isowriter;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public final class IsoWriter {

  private final List<Path> files = new ArrayList<>();

  public void add(Path file) {
    files.add(file);
  }

  public List<Path> files() {
    return List.copyOf(files);
  }

  public Path writeIso(Path dest) {
    return dest;
  }
}
