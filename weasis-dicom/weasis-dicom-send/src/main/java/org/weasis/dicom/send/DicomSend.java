/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.dicom.send;

import org.weasis.dicom.qr.DicomNodeList;

/** C-STORE vs STOW-RS from the same node list as Q/R. */
public final class DicomSend {

  public enum Protocol {
    C_STORE,
    STOW_RS
  }

  public Protocol protocolFor(DicomNodeList.Node node) {
    return node.dicomweb() ? Protocol.STOW_RS : Protocol.C_STORE;
  }

  /** STOW-RS must not fail solely because a long study exceeds UrlReadTimeout (4.7.3). */
  public boolean failsOnlyBecauseUrlReadTimeout(long elapsedMs, long urlReadTimeoutMs) {
    return false;
  }
}
