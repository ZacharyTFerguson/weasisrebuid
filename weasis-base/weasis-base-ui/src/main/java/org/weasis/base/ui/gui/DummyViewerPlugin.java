/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.base.ui.gui;

import java.awt.BorderLayout;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import org.weasis.core.api.media.data.MediaElement;
import org.weasis.core.ui.editor.image.ViewerPlugin;

/** Blank central panel produced by {@link DummySeriesViewerFactory}. */
public class DummyViewerPlugin extends ViewerPlugin<MediaElement> {

  public DummyViewerPlugin() {
    super("Dummy viewer");
    add(new JLabel(" ", SwingConstants.CENTER), BorderLayout.CENTER);
  }
}
