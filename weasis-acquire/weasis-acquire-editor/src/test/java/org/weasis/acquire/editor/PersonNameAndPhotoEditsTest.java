/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.acquire.editor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import org.junit.jupiter.api.Test;
import org.weasis.acquire.explorer.PatientDemographics;

class PersonNameAndPhotoEditsTest {

  @Test
  void personNameRulesAndNoReformatFromCommand() {
    assertFalse(PersonNameEditor.isValidComponent("Sm^ith"));
    assertFalse(PersonNameEditor.isValidComponent("A=B"));
    assertFalse(PersonNameEditor.isValidComponent("A\\B"));
    assertTrue(PersonNameEditor.isValidComponent("Smith"));
    PersonNameEditor.Components c = new PersonNameEditor.Components("Smith", "John", "", "", "");
    String composed = PersonNameEditor.compose(c);
    assertEquals("Smith^John^^^", composed);
    assertFalse(PersonNameEditor.previewOverLength(composed));
    assertTrue(PersonNameEditor.previewOverLength("x".repeat(65)));
    assertEquals("Smith, John", PersonNameEditor.lexicalDisplay(composed));
    String inbound = "Yamada^Tarou=山田^太郎=やまだ^たろう";
    assertEquals(inbound, PersonNameEditor.applyInbound(inbound, true));
    assertTrue(PersonNameEditor.lexicalDisplay(inbound).startsWith("Yamada, Tarou="));
    PatientDemographics demo = new PatientDemographics(inbound, "SYN-PN-1", "", "", "");
    assertEquals(inbound, demo.patientName());
  }

  @Test
  void photoEditorCropRotateContrast() {
    BufferedImage src = new BufferedImage(4, 2, BufferedImage.TYPE_INT_RGB);
    src.setRGB(0, 0, Color.RED.getRGB());
    src.setRGB(3, 1, Color.BLUE.getRGB());
    BufferedImage rot = PhotoEdits.rotate90(src);
    assertEquals(2, rot.getWidth());
    assertEquals(4, rot.getHeight());
    BufferedImage cropped = PhotoEdits.crop(src, new Rectangle(0, 0, 2, 2));
    assertEquals(2, cropped.getWidth());
    BufferedImage contrast = PhotoEdits.contrast(src, 1.2f);
    assertEquals(src.getWidth(), contrast.getWidth());
  }
}
