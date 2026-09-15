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
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class SendDicomFactoryTest {

  @Test
  void stowAvailableAndProtocolSelection() {
    SendDicomFactory factory = new SendDicomFactory();
    assertTrue(factory.stow().isAvailable());
    assertEquals(SendDicomFactory.Protocol.STOW_RS, factory.protocolFor(true));
    assertEquals(SendDicomFactory.Protocol.C_STORE, factory.protocolFor(false));
  }
}
