/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.dicom.viewer2d.mpr;

import java.util.ArrayList;
import java.util.List;

/** Three-pane MPR tab. Inherited fusion snapshot is applied once in WP-8, not here. */
public final class MprContainer {

  private final MprController controller;
  private final List<String> derivedSeries = new ArrayList<>();

  public MprContainer(MprController controller) {
    this.controller = controller;
  }

  public MprController controller() {
    return controller;
  }

  public List<MprView> panes() {
    return controller.views();
  }

  public List<String> derivedSeries() {
    return List.copyOf(derivedSeries);
  }

  public void addDerived(String seriesUid) {
    derivedSeries.add(seriesUid);
  }
}
