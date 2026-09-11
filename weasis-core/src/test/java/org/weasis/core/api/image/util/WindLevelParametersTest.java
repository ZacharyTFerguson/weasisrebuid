/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.core.api.image.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class WindLevelParametersTest {

  @Test
  void lowerUpperFromWindowLevel() {
    WindLevelParameters p = new WindLevelParameters(100, 40);
    assertEquals(-10.0, p.getLower(), 1e-9);
    assertEquals(90.0, p.getUpper(), 1e-9);
  }
}
