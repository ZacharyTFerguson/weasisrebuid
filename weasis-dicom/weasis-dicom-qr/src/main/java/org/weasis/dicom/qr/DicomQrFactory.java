/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.dicom.qr;

import java.util.Hashtable;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.weasis.core.api.explorer.DicomImportFactory;
import org.weasis.core.api.explorer.ImportDicom;
import org.weasis.core.api.service.UICore;

/** DIMSE C-FIND/MOVE/GET and DICOMweb QIDO factory; File &gt; Import DICOM Q/R page. */
@Component(service = DicomImportFactory.class, immediate = true)
public class DicomQrFactory implements DicomImportFactory {

  public enum Verb {
    C_FIND,
    C_MOVE,
    C_GET,
    QIDO_RS,
    WADO_URI,
    WADO_RS
  }

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
    return new DicomQrView();
  }

  public DicomQrView newView() {
    return new DicomQrView();
  }

  public boolean supportsCFind() {
    return true;
  }

  public boolean supports(Verb verb) {
    return verb != null;
  }

  public RsQuery newRsQuery() {
    return new RsQuery();
  }

  public SearchParameters newSearchParameters() {
    return new SearchParameters();
  }

  public RetrieveContext newRetrieveContext() {
    return new RetrieveContext();
  }
}
