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

import org.weasis.dicom.explorer.imp.DicomImportFactory;

/**
 * Clone-only alias for {@link DicomImportFactory} (Weasis 4.7.3 path). Constants stay here so
 * existing import dialog call sites keep compiling.
 */
public class LocalImportFactory extends DicomImportFactory {

  public static final String PAGE_LOCAL = DicomImportFactory.PAGE_LOCAL;
  public static final String PAGE_CD = DicomImportFactory.PAGE_CD;
  public static final String PAGE_ZIP = DicomImportFactory.PAGE_ZIP;
  public static final String PAGE_DIR = DicomImportFactory.PAGE_DIR;
}
