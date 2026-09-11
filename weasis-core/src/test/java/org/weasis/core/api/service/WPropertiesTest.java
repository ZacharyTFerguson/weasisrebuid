/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.core.api.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.Color;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class WPropertiesTest {

  @Test
  void booleanRoundTripAndDefaults() {
    WProperties p = new WProperties();
    assertTrue(p.getBooleanProperty("missing", true));
    assertFalse(p.getBooleanProperty("missing", false));
    p.putBooleanProperty("flag", true);
    assertTrue(p.getBooleanProperty("flag", false));
    p.setProperty("flag", "yes");
    assertTrue(p.getBooleanProperty("flag", false));
    p.setProperty("flag", "0");
    assertFalse(p.getBooleanProperty("flag", true));
    p.setProperty("flag", "not-a-bool");
    assertTrue(p.getBooleanProperty("flag", true));
  }

  @ParameterizedTest
  @CsvSource({"12,12", "not-int,7"})
  void intPropertyFallsBack(String raw, int expected) {
    WProperties p = new WProperties();
    p.setProperty("n", raw);
    assertEquals(expected, p.getIntProperty("n", 7));
  }

  @Test
  void missingIntUsesDefault() {
    assertEquals(7, new WProperties().getIntProperty("n", 7));
  }

  @Test
  void floatAndLongRoundTrip() {
    WProperties p = new WProperties();
    p.putFloatProperty("f", 1.5f);
    assertEquals(1.5f, p.getFloatProperty("f", 0), 1e-6);
    p.putLongProperty("l", 99L);
    assertEquals(99L, p.getLongProperty("l", 0));
    p.setProperty("l", "x");
    assertEquals(3L, p.getLongProperty("l", 3L));
  }

  @Test
  void colorUsesDollarSeparatedRgba() {
    WProperties p = new WProperties();
    Color c = new Color(10, 20, 30, 40);
    p.putColorProperty("c", c);
    Color back = p.getColorProperty("c", Color.BLACK);
    assertEquals(c.getRed(), back.getRed());
    assertEquals(c.getGreen(), back.getGreen());
    assertEquals(c.getBlue(), back.getBlue());
    assertEquals(c.getAlpha(), back.getAlpha());
    p.putColorProperty("gone", null);
    assertNull(p.getProperty("gone"));
    assertEquals(Color.RED, p.getColorProperty("missing", Color.RED));
  }
}
