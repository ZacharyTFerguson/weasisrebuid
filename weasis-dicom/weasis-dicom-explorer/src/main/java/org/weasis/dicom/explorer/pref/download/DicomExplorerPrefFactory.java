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
import org.osgi.service.component.annotations.Component;
import org.weasis.core.api.gui.Insertable;
import org.weasis.core.api.gui.InsertableUtil;
import org.weasis.core.api.gui.PreferencesPageFactory;
import org.weasis.core.api.gui.util.AbstractItemDialogPage;
import org.weasis.core.ui.pref.AbstractPreferencesPageFactory;

/** File &gt; Preferences &gt; DICOM Explorer (MX-10 / MX-11). Disabled on the Dicomizer profile. */
@Component(service = PreferencesPageFactory.class)
public class DicomExplorerPrefFactory extends AbstractPreferencesPageFactory {

  @Override
  public void activate() {
    if (!InsertableUtil.isFactoryEnabled(DicomExplorerPrefFactory.class)) {
      return;
    }
    super.activate();
  }

  @Override
  public AbstractItemDialogPage createInstance(Hashtable<String, Object> properties) {
    if (!InsertableUtil.isFactoryEnabled(DicomExplorerPrefFactory.class)) {
      return null;
    }
    return new DicomExplorerPrefView();
  }

  @Override
  public boolean isComponentCreatedByThisFactory(Insertable component) {
    return component instanceof DicomExplorerPrefView;
  }
}
