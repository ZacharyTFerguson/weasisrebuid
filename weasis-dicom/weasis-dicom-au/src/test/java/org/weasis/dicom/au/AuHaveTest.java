/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.dicom.au;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class AuHaveTest {

  @Test
  void playScrubVolumeAndExportFormats() {
    AuPlayer p = new AuPlayer();
    p.play();
    assertTrue(p.playing());
    p.pause();
    assertFalse(p.playing());
    p.scrub(1.5);
    assertEquals(1.5, p.positionSeconds(), 1e-9);
    p.setVolume(0.25);
    assertEquals(0.25, p.volume(), 1e-9);
    assertEquals(2, AuPlayer.ExportFormat.values().length);
  }
}
