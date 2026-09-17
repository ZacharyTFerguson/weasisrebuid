/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.acquire.explorer.gui.central;

import java.awt.BorderLayout;
import org.weasis.core.api.media.data.MediaElement;
import org.weasis.core.ui.editor.image.ViewerPlugin;

/** Central dicomizer album pane hosting {@link AcquireTabPanel}. */
public class ImageGroupPane extends ViewerPlugin<MediaElement> {

  public static final String NAME = "Album";

  private final AcquireTabPanel tabPanel = new AcquireTabPanel();

  public ImageGroupPane() {
    super(NAME);
    add(tabPanel, BorderLayout.CENTER);
  }

  public AcquireTabPanel tabPanel() {
    return tabPanel;
  }
}
