/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.dicom.explorer;

import java.util.Hashtable;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.weasis.core.api.explorer.DataExplorerView;
import org.weasis.core.api.explorer.DataExplorerViewFactory;
import org.weasis.core.api.gui.Insertable;
import org.weasis.core.api.gui.InsertableUtil;
import org.weasis.core.api.service.UICore;

@Component(service = DataExplorerViewFactory.class, immediate = true)
public class DicomExplorerFactory implements DataExplorerViewFactory {

  @Activate
  public void activate() {
    if (!InsertableUtil.isFactoryEnabled(DicomExplorerFactory.class)) {
      return;
    }
    UICore core = UICore.getInstance();
    core.registerExplorerFactory(this);
    core.getSystemPreferences()
        .putIntProperty(
            org.weasis.dicom.explorer.SeriesDownloadManager.PREF_SERIES,
            org.weasis.dicom.explorer.SeriesDownloadManager.CONCURRENT_SERIES);
    core.getSystemPreferences()
        .putIntProperty(
            org.weasis.dicom.explorer.SeriesDownloadManager.PREF_IMAGES,
            org.weasis.dicom.explorer.SeriesDownloadManager.CONCURRENT_DOWNLOADS_IN_SERIES);
  }

  @Deactivate
  public void deactivate() {
    UICore.getInstance().unregisterInsertableFactory(this);
  }

  @Override
  public DataExplorerView createInstance(Hashtable<String, Object> properties) {
    DicomModel model = LocalPersistence.getDicomModel();
    if (properties != null && properties.get("model") instanceof DicomModel persisted) {
      model = persisted;
    }
    return new DicomExplorer(model);
  }

  @Override
  public void dispose(Insertable component) {
    if (component instanceof DicomExplorer explorer) {
      explorer.dispose();
    }
  }

  @Override
  public boolean isComponentCreatedByThisFactory(Insertable component) {
    return component instanceof DicomExplorer;
  }
}
