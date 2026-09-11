/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.core.api.gui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.util.Hashtable;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.weasis.core.api.explorer.DataExplorerView;
import org.weasis.core.api.explorer.DataExplorerViewFactory;
import org.weasis.core.api.explorer.DicomImportFactory;
import org.weasis.core.api.explorer.ImportDicom;
import org.weasis.core.api.explorer.model.DataExplorerModel;
import org.weasis.core.api.gui.util.AbstractItemDialogPage;
import org.weasis.core.api.service.UICore;
import org.weasis.core.api.service.WProperties;
import org.weasis.core.ui.pref.GeneralSetting;
import org.weasis.core.ui.pref.ProxyPrefFactory;
import org.weasis.core.ui.pref.ProxyPrefView;

class InsertableFactoryRegistrationTest {

  @Test
  void preferencesFactoryCreatesPageAndRegistersOnUiCore() {
    UICore core = new UICore();
    ProxyPrefFactory factory = new ProxyPrefFactory();
    core.registerPreferencesPageFactory(factory);
    assertTrue(core.getPreferencesPageFactories().contains(factory));
    AbstractItemDialogPage page = factory.createInstance(new Hashtable<>());
    assertTrue(page instanceof ProxyPrefView);
    assertTrue(factory.isComponentCreatedByThisFactory(page));
    assertEquals(Insertable.Type.PREFERENCES, factory.getType());
    assertEquals(Insertable.Type.PREFERENCES, page.getType());
  }

  @Test
  void insertableFactoryRoutesExplorerFactory() {
    UICore core = new UICore();
    DataExplorerViewFactory factory =
        new DataExplorerViewFactory() {
          @Override
          public DataExplorerView createInstance(Hashtable<String, Object> properties) {
            return new DataExplorerView() {
              @Override
              public DataExplorerModel getDataExplorerModel() {
                return null;
              }

              @Override
              public void dispose() {}

              @Override
              public String getComponentName() {
                return "stub";
              }

              @Override
              public int getComponentPosition() {
                return 0;
              }

              @Override
              public void setComponentPosition(int position) {}

              @Override
              public boolean isComponentEnabled() {
                return true;
              }

              @Override
              public void setComponentEnabled(boolean enabled) {}
            };
          }

          @Override
          public void dispose(Insertable component) {}

          @Override
          public boolean isComponentCreatedByThisFactory(Insertable component) {
            return component instanceof DataExplorerView;
          }
        };
    core.registerInsertableFactory(factory);
    assertTrue(core.getExplorerFactories().contains(factory));
    assertEquals(Insertable.Type.EXPLORER, factory.getType());
  }

  @Test
  void dicomImportFactoryRegistersIndependently() {
    UICore core = new UICore();
    DicomImportFactory factory =
        properties ->
            new ImportDicom() {
              @Override
              public String getTitle() {
                return "DICOM";
              }

              @Override
              public void importFiles(List<File> files, String zipPassword) {}
            };
    core.registerDicomImportFactory(factory);
    assertTrue(core.getDicomImportFactories().contains(factory));
    core.unregisterDicomImportFactory(factory);
    assertTrue(core.getDicomImportFactories().isEmpty());
  }

  @Test
  void insertableUtilPersistsVisibilityAndPosition() {
    WProperties prefs = new WProperties();
    GeneralSetting page = new GeneralSetting();
    page.setComponentPosition(42);
    page.setComponentEnabled(false);
    InsertableUtil.savePreferences(page, prefs, "general");
    GeneralSetting loaded = new GeneralSetting();
    InsertableUtil.applyPreferences(loaded, prefs, "general", true);
    assertFalse(loaded.isComponentEnabled());
    assertEquals(42, loaded.getComponentPosition());
  }
}
