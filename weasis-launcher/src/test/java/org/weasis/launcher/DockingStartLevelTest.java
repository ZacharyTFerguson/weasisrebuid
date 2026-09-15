/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.launcher;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;
import java.util.Map;
import org.junit.jupiter.api.Test;

/** WP-6: Docking Frames OSGi jar at felix.auto.start.10. */
class DockingStartLevelTest {

  @Test
  void startLevel10ListsDockingFrames() throws Exception {
    Path base = Mx03ShippingPrefsTest.moduleRoot().resolve("conf/base.json");
    ConfigData data =
        ConfigData.load(base, Map.of("app.version", "4.7.3", "dockingframes.version", "1.1.7"));
    String start10 = data.value("felix.auto.start.10");
    assertTrue(start10.contains("docking-frames"));
    assertTrue(start10.contains("org/weasis/thirdparty/docking-frames"));
  }
}
