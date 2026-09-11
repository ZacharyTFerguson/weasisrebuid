/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.base.viewer2d;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Hashtable;
import org.junit.jupiter.api.Test;

class BaseViewer2dFactoryTest {

  @Test
  void readsNonDicomImageMimeAndImageGet() {
    BaseViewer2dFactory factory = new BaseViewer2dFactory();
    assertTrue(factory.canReadMimeType("image/jpeg"));
    assertTrue(factory.isViewerCreatedByThisFactory(factory.createSeriesViewer(new Hashtable<>())));
    assertEquals(100, factory.getLevel());
    ImageProtocolCommands cmds = new ImageProtocolCommands();
    assertTrue(cmds.get("-f", "/tmp/a.png").contains("file"));
    assertTrue(cmds.close("-a").contains("all"));
  }
}
