/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.dicom.rt;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.weasis.core.api.service.UICore;
import org.weasis.dicom.codec.DicomMime;

class RtDisplayToolFactoryTest {

  @Test
  void registersFactory() {
    RtDisplayToolFactory factory = new RtDisplayToolFactory();
    factory.activate();
    assertTrue(factory.canReadMimeType(DicomMime.RT_DICOM));
    assertTrue(UICore.getInstance().getViewerFactory(DicomMime.RT_DICOM).isPresent());
    factory.deactivate();
  }
}
