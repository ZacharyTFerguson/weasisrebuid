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

import java.awt.BorderLayout;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;
import javax.swing.JPanel;
import org.weasis.acquire.explorer.gui.central.meta.panel.imp.AcquireGlobalMetaPanel;
import org.weasis.acquire.explorer.gui.central.meta.panel.imp.AcquireImageMetaPanel;
import org.weasis.acquire.explorer.gui.central.meta.panel.imp.AcquireSeriesMetaPanel;

/** Photo-editor metadata: global / series / image display-edit-required tables. */
public class MetadataPanel extends JPanel {

  private AcquireGlobalMetaPanel global;
  private AcquireSeriesMetaPanel series;
  private AcquireImageMetaPanel image;

  public MetadataPanel() {
    super(new BorderLayout());
    bind(new Properties(), new LinkedHashMap<>(), new LinkedHashMap<>(), new LinkedHashMap<>());
  }

  public void bind(
      Properties prefs,
      Map<String, String> globalValues,
      Map<String, String> seriesValues,
      Map<String, String> imageValues) {
    removeAll();
    global = new AcquireGlobalMetaPanel(prefs, globalValues);
    series = new AcquireSeriesMetaPanel(prefs, seriesValues);
    image = new AcquireImageMetaPanel(prefs, imageValues);
    JPanel stack = new JPanel(new BorderLayout());
    stack.add(global, BorderLayout.NORTH);
    stack.add(series, BorderLayout.CENTER);
    stack.add(image, BorderLayout.SOUTH);
    add(stack, BorderLayout.CENTER);
  }

  public AcquireGlobalMetaPanel global() {
    return global;
  }

  public AcquireSeriesMetaPanel series() {
    return series;
  }

  public AcquireImageMetaPanel image() {
    return image;
  }

  public boolean requiredComplete() {
    return global.requiredComplete() && series.requiredComplete() && image.requiredComplete();
  }
}
