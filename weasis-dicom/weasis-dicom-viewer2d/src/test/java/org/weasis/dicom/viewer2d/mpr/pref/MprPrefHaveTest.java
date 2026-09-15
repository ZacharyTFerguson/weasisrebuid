/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.dicom.viewer2d.mpr.pref;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import javax.swing.JLabel;
import org.junit.jupiter.api.Test;
import org.weasis.core.api.gui.Insertable;
import org.weasis.core.api.gui.util.AbstractItemDialogPage;
import org.weasis.core.ui.pref.PreferenceDialog;
import org.weasis.core.ui.pref.ShellPrefPage;

class MprPrefHaveTest {

  @Test
  void factoryCreatesTitleOnlyMprPage() {
    MprPrefFactory factory = new MprPrefFactory();
    AbstractItemDialogPage page = factory.createInstance(null);
    assertInstanceOf(MprPrefView.class, page);
    assertEquals(MprPrefView.class, page.getClass());
    assertEquals(Insertable.Type.PREFERENCES, factory.getType());
    assertTrue(factory.isComponentCreatedByThisFactory(page));
    assertEquals(MprPrefView.TITLE, page.getTitle());
    assertEquals(1, page.getComponentCount());
    assertInstanceOf(JLabel.class, page.getComponent(0));
    assertEquals(MprPrefView.TITLE, ((JLabel) page.getComponent(0)).getText());
  }

  @Test
  void preferenceTreeKeepsCoreMprStubAndFixtureFactoryPage() {
    List<AbstractItemDialogPage> pages =
        PreferenceDialog.instantiatePages(
            List.of(new org.weasis.core.ui.pref.ViewerPrefFactory(), new MprPrefFactory()));
    AbstractItemDialogPage viewer =
        pages.stream().filter(p -> "Viewer".equals(p.getTitle())).findFirst().orElseThrow();
    AbstractItemDialogPage coreMpr =
        viewer.getSubPages().stream()
            .filter(p -> MprPrefView.TITLE.equals(p.getTitle()))
            .findFirst()
            .orElseThrow();
    assertEquals(ShellPrefPage.class, coreMpr.getClass());
    assertTrue(pages.stream().anyMatch(MprPrefView.class::isInstance));
    MprPrefView fixture =
        (MprPrefView)
            pages.stream().filter(MprPrefView.class::isInstance).findFirst().orElseThrow();
    assertEquals(MprPrefView.TITLE, fixture.getTitle());
    fixture.closeAdditionalWindow();
  }
}
