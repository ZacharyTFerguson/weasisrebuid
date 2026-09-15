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

/**
 * DIMSE node identity: called AE Title, host, and port. Default calling target is dcm4chee ({@code
 * DCM4CHEE:11112}).
 */
public class AbstractDicomNode {

  public static final String DEFAULT_AE_TITLE = "DCM4CHEE";
  public static final String DEFAULT_HOSTNAME = "localhost";
  public static final int DEFAULT_PORT = 11112;

  private String description;
  private String aeTitle;
  private String hostname;
  private int port;

  public AbstractDicomNode() {
    this(DEFAULT_AE_TITLE, DEFAULT_AE_TITLE, DEFAULT_HOSTNAME, DEFAULT_PORT);
  }

  public AbstractDicomNode(String description, String aeTitle, String hostname, int port) {
    setDescription(description);
    setAeTitle(aeTitle);
    setHost(hostname);
    setPort(port);
  }

  public String description() {
    return description;
  }

  public void setDescription(String description) {
    this.description =
        description == null || description.isBlank() ? DEFAULT_AE_TITLE : description.trim();
  }

  public String aeTitle() {
    return aeTitle;
  }

  public void setAeTitle(String aeTitle) {
    this.aeTitle = aeTitle == null || aeTitle.isBlank() ? DEFAULT_AE_TITLE : aeTitle.trim();
  }

  public String host() {
    return hostname;
  }

  public void setHost(String hostname) {
    this.hostname = hostname == null || hostname.isBlank() ? DEFAULT_HOSTNAME : hostname.trim();
  }

  public int port() {
    return port;
  }

  public void setPort(int port) {
    this.port = port <= 0 || port > 65535 ? DEFAULT_PORT : port;
  }

  public String endpoint() {
    return aeTitle + "@" + hostname + ":" + port;
  }

  public boolean dicomWeb() {
    return false;
  }

  @Override
  public String toString() {
    return description() + " (" + endpoint() + ")";
  }
}
