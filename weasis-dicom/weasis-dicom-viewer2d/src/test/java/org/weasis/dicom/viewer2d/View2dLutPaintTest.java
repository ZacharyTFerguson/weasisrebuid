/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.dicom.viewer2d;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.image.BufferedImage;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class View2dLutPaintTest {

  @Test
  void windowLevelPaintsMonochrome2(@TempDir Path dir) throws Exception {
    Path file = dir.resolve("ct.dcm");
    TestCt.write(file.toFile(), 8, 40, 400);
    View2d view = new View2d();
    view.load(file.toFile());
    BufferedImage img = view.getSourceImage();
    assertEquals(8, img.getWidth());
    int center = img.getRaster().getSample(4, 4, 0);
    int black = img.getRaster().getSample(0, 0, 0);
    assertTrue(center > black, "W/L should map higher stored values brighter on MONOCHROME2");
    view.setWindowLevel(1, 40);
    BufferedImage narrow = view.getSourceImage();
    assertEquals(8, narrow.getWidth());
  }
}
