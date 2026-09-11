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

/** Injects MPR/MIP-built series into the current Explorer study. */
public final class DerivedSeriesBuilder {

  private final List<String> studySeries = new ArrayList<>();

  public List<String> studySeries() {
    return List.copyOf(studySeries);
  }

  public String buildCurrentView(MprView view) {
    String uid = "2.25." + Math.abs(view.plane().hashCode() * 1_000_003L);
    studySeries.add(uid);
    return uid;
  }

  public List<String> buildThreePlanes(MprContainer container) {
    List<String> uids = new ArrayList<>();
    for (MprView view : container.panes()) {
      String uid = buildCurrentView(view);
      container.addDerived(uid);
      uids.add(uid);
    }
    return uids;
  }
}
