/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.dicom.explorer.rs;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class RsQueryParamsTest {

  @Test
  void dicomRsStyleUrl() {
    RsQueryParams params = new RsQueryParams();
    params.setUrl("https://example/dicom-web");
    params.setRawQueryParams("PatientID=SYNTH&limit=10");
    assertEquals("SYNTH", params.parseQueryParams().get("PatientID"));
    String built = params.buildQidoStudiesUrl();
    assertTrue(built.contains("/studies?"));
    assertTrue(built.contains("PatientID=SYNTH"));
    params.setQueryExt("&includedefaults=false");
    assertTrue(params.buildQidoStudiesUrl().contains("includedefaults=false"));
  }

  @Test
  void resultSuccessRange() {
    RsQueryResult result = new RsQueryResult();
    result.setHttpStatus(204);
    assertTrue(result.success());
    result.setHttpStatus(404);
    assertTrue(!result.success());
  }
}
