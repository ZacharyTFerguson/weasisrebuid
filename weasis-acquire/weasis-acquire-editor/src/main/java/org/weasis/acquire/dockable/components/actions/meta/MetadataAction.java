/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.acquire.dockable.components.actions.meta;

import java.util.Map;
import java.util.Properties;

/** Edition-tool metadata action: required tags gate publish. */
public class MetadataAction {

  private final MetadataPanel panel = new MetadataPanel();

  public MetadataPanel panel() {
    return panel;
  }

  public void bind(
      Properties prefs,
      Map<String, String> globalValues,
      Map<String, String> seriesValues,
      Map<String, String> imageValues) {
    panel.bind(prefs, globalValues, seriesValues, imageValues);
  }

  public boolean requiredComplete() {
    return panel.requiredComplete();
  }
}
