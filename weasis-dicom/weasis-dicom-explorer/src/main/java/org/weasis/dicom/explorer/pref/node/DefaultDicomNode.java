/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.dicom.explorer.pref.node;

/** DIMSE node (calling/called AET, host, port). */
public final class DefaultDicomNode {

  private final String description;
  private final String aeTitle;
  private final String host;
  private final int port;

  public DefaultDicomNode(String description, String aeTitle, String host, int port) {
    this.description = description;
    this.aeTitle = aeTitle;
    this.host = host;
    this.port = port;
  }

  public String description() {
    return description;
  }

  public String aeTitle() {
    return aeTitle;
  }

  public String host() {
    return host;
  }

  public int port() {
    return port;
  }

  public boolean dicomWeb() {
    return false;
  }
}
