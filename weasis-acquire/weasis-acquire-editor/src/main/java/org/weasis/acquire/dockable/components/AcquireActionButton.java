/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.acquire.dockable.components;

import javax.swing.JToggleButton;

/** Exclusive edition-tool toggle (rectify / contrast / annotate / calibrate / metadata). */
public class AcquireActionButton extends JToggleButton {

  public static final String RECTIFY = "rectify";
  public static final String CONTRAST = "contrast";
  public static final String ANNOTATE = "annotate";
  public static final String CALIBRATE = "calibrate";
  public static final String METADATA = "metadata";

  private final String actionId;

  public AcquireActionButton(String actionId) {
    super(actionId == null || actionId.isBlank() ? RECTIFY : actionId);
    this.actionId = actionId == null || actionId.isBlank() ? RECTIFY : actionId;
    setName(this.actionId);
  }

  public String actionId() {
    return actionId;
  }
}
