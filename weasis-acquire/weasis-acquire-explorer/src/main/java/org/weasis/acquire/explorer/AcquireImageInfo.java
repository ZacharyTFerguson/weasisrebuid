/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.acquire.explorer;

public class AcquireImageInfo extends AcquireMediaInfo {

  private String comments = "";
  private String anatomicRegionCode = "";
  private String anatomicRegionLabel = "";

  public String getComments() {
    return comments;
  }

  public void setComments(String comments) {
    this.comments = comments == null ? "" : comments;
  }

  public String getAnatomicRegionCode() {
    return anatomicRegionCode;
  }

  public void setAnatomicRegionCode(String anatomicRegionCode) {
    this.anatomicRegionCode = anatomicRegionCode == null ? "" : anatomicRegionCode;
  }

  public String getAnatomicRegionLabel() {
    return anatomicRegionLabel;
  }

  public void setAnatomicRegionLabel(String anatomicRegionLabel) {
    this.anatomicRegionLabel = anatomicRegionLabel == null ? "" : anatomicRegionLabel;
  }
}
