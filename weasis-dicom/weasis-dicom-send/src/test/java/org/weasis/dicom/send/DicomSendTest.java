/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.dicom.send;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.Test;
import org.weasis.dicom.qr.DicomNodeList;

class DicomSendTest {

  @Test
  void storeVsStowFromSameNodeList() {
    DicomNodeList list = new DicomNodeList();
    list.add(new DicomNodeList.Node("pacs", "WEASIS", "127.0.0.1", 11112, false));
    list.add(new DicomNodeList.Node("web", "WEB", "example", 443, true));
    DicomSend send = new DicomSend();
    assertEquals(DicomSend.Protocol.C_STORE, send.protocolFor(list.nodes().get(0)));
    assertEquals(DicomSend.Protocol.STOW_RS, send.protocolFor(list.nodes().get(1)));
    assertFalse(send.failsOnlyBecauseUrlReadTimeout(60_000, 1000));
  }
}
