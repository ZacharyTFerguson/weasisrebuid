/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.dicom.sr;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.weasis.dicom.codec.DicomMime;

class SrHaveTest {

  @Test
  void graphicCatalogAndMime() {
    assertEquals(9, SrGraphicType.values().length);
    SrFactory f = new SrFactory();
    assertTrue(f.canReadMimeType(DicomMime.SR_DICOM));
    assertEquals("SR", f.getModality());
  }
}
