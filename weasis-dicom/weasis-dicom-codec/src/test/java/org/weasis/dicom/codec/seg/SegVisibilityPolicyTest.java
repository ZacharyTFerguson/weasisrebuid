/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.dicom.codec.seg;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.Test;

class SegVisibilityPolicyTest {

  @Test
  void hideAllGreysOtherRules() {
    SegVisibilityPolicy p = new SegVisibilityPolicy();
    assertTrue(p.keywordRulesEnabled());
    assertTrue(p.hideFromCountEnabled());
    p.setHideAll(true);
    assertFalse(p.keywordRulesEnabled());
    assertFalse(p.hideFromCountEnabled());
    assertFalse(p.hiddenByKeyword("table removal", null, null, List.of()));
  }

  @Test
  void keywordMatchesSeriesOrEverySegment() {
    SegVisibilityPolicy p = new SegVisibilityPolicy();
    assertTrue(p.hiddenByKeyword("Table_Removal", null, null, List.of()));
    assertTrue(p.hiddenByKeyword(null, "couch", null, List.of()));
    assertTrue(p.hiddenByKeyword("CT", "x", "y", List.of("table-top", "tabletop", "Table Top")));
    assertFalse(p.hiddenByKeyword("CT", "x", "y", List.of("tabletop", "lung")));
    p.restoreDefaults();
    assertEquals(3, p.hideCount());
    assertFalse(p.hideAll());
  }
}
