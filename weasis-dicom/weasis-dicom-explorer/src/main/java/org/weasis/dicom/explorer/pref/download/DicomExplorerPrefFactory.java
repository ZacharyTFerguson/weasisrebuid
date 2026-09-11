/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.dicom.explorer.pref.download;

import java.util.Hashtable;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.weasis.core.api.gui.FactoryEnablement;
import org.weasis.core.api.gui.Insertable;
import org.weasis.core.api.gui.PreferencesPageFactory;
import org.weasis.core.api.gui.util.AbstractItemDialogPage;
import org.weasis.core.api.service.UICore;

@Component(service = PreferencesPageFactory.class, immediate = true)
public class DicomExplorerPrefFactory implements PreferencesPageFactory {

  @Activate
  public void activate() {
    if (!FactoryEnablement.isEnabled(DicomExplorerPrefFactory.class)) {
      return;
    }
    UICore.getInstance().registerPreferencesPageFactory(this);
  }

  @Deactivate
  public void deactivate() {
    UICore.getInstance().unregisterInsertableFactory(this);
  }

  @Override
  public AbstractItemDialogPage createInstance(Hashtable<String, Object> properties) {
    return new DicomExplorerPrefView();
  }

  @Override
  public void dispose(Insertable component) {}

  @Override
  public boolean isComponentCreatedByThisFactory(Insertable component) {
    return component instanceof DicomExplorerPrefView;
  }
}
