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

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.file.Path;
import org.junit.jupiter.api.Test;

class IsoWriterTest {

  @Test
  void writesDestinationPath() {
    IsoWriter w = new IsoWriter();
    w.add(Path.of("/tmp/a.dcm"));
    assertEquals(1, w.files().size());
    assertEquals(Path.of("/out.iso"), w.writeIso(Path.of("/out.iso")));
  }
}
