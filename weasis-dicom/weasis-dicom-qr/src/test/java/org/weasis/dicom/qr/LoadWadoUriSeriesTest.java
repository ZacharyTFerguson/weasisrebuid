/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.dicom.qr;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class LoadWadoUriSeriesTest {

  @Test
  void wadoUriQueryString() {
    String uri =
        new LoadWadoUriSeries()
            .buildWadoUri("http://localhost/wado", "1.2.3", "1.2.4", "1.2.5", "application/dicom");
    assertTrue(uri.contains("requestType=WADO"));
    assertTrue(uri.contains("studyUID=1.2.3"));
    assertTrue(uri.contains("objectUID=1.2.5"));
  }
}
