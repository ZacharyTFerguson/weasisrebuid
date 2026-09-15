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

/** Print SCP node: DIMSE AE plus grayscale vs color print management meta SOP. */
public class DicomPrintNode {

  private final DefaultDicomNode node;
  private final boolean color;

  public DicomPrintNode(String description, String aeTitle, String host, int port) {
    this(description, aeTitle, host, port, false);
  }

  public DicomPrintNode(String description, String aeTitle, String host, int port, boolean color) {
    this.node = new DefaultDicomNode(description, aeTitle, host, port);
    this.color = color;
  }

  public DicomPrintNode(DefaultDicomNode node, boolean color) {
    this.node = node;
    this.color = color;
  }

  public DefaultDicomNode asNode() {
    return node;
  }

  public String description() {
    return node == null ? "" : node.description();
  }

  public String aeTitle() {
    return node == null ? "" : node.aeTitle();
  }

  public String host() {
    return node == null ? "" : node.host();
  }

  public int port() {
    return node == null ? 0 : node.port();
  }

  public boolean color() {
    return color;
  }
}
