/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.core.api.i18n;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Properties;
import org.junit.jupiter.api.Test;

class LocaleCoverageTest {

  @Test
  void hidesLocalesUnder30PercentAndKeepsDefaults() {
    Properties props = new Properties();
    props.setProperty("en", "100");
    props.setProperty("fr", "95");
    props.setProperty("zh_CN", "31");
    props.setProperty("ja", "29");
    props.setProperty("ko", "12");
    LocaleCoverage coverage = LocaleCoverage.fromProperties(props);
    assertTrue(coverage.isOffered("en"));
    assertTrue(coverage.isOffered("zh_CN"));
    assertFalse(coverage.isOffered("ja"));
    assertEquals(3, coverage.offered().size());
    assertEquals(2, coverage.hidden().size());
    assertEquals(LocaleCoverage.DEFAULT_LANG, "en");
    assertEquals(LocaleCoverage.DEFAULT_FORMAT, "system");
    assertEquals(30, LocaleCoverage.MIN_PERCENT);
  }

  @Test
  void defaultCatalogHidesJaKoRu() {
    LocaleCoverage coverage = LocaleCoverage.loadDefault();
    assertTrue(coverage.isOffered("en"));
    assertTrue(coverage.isOffered("fr"));
    assertTrue(coverage.isOffered("pt_BR"));
    assertFalse(coverage.isOffered("ja"));
    assertFalse(coverage.isOffered("ko"));
    assertFalse(coverage.isOffered("ru"));
  }
}
