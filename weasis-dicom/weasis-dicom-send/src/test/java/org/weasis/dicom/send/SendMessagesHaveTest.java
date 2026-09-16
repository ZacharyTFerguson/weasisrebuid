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

import java.util.Locale;
import org.junit.jupiter.api.Test;

class SendMessagesHaveTest {

  @Test
  void documentedBundleHonorsFrench() {
    assertEquals("Send", Messages.getString("send", Locale.ENGLISH));
    assertEquals("Envoyer", Messages.getString("send", Locale.FRENCH));
    assertEquals("missing.key", Messages.getString("missing.key", Locale.ENGLISH));
  }
}
