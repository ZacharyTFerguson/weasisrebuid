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
import org.weasis.core.api.explorer.DicomImportFactory;
import org.weasis.core.api.explorer.ImportDicom;
import org.weasis.core.api.service.UICore;

@Component(service = DicomImportFactory.class, immediate = true)
public class LocalImportFactory implements DicomImportFactory {

  public static final String PAGE_LOCAL = "DICOM";
  public static final String PAGE_CD = "DICOM CD";
  public static final String PAGE_ZIP = "ZIP";
  public static final String PAGE_DIR = "DICOMDIR";

  @Activate
  public void activate() {
    UICore.getInstance().registerDicomImportFactory(this);
  }

  @Deactivate
  public void deactivate() {
    UICore.getInstance().unregisterDicomImportFactory(this);
  }

  @Override
  public ImportDicom createDicomImportPage(Hashtable<String, Object> properties) {
    String title = PAGE_LOCAL;
    if (properties != null && properties.get("title") instanceof String s) {
      title = s;
    }
    DicomModel model = new DicomModel();
    if (properties != null && properties.get("model") instanceof DicomModel m) {
      model = m;
    }
    return new ImportDicomPage(title, 0, model, new SkipUnsupportedSopNotifier());
  }
}
