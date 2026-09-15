/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.core.ui.editor.image;

import org.weasis.core.api.image.OpManager;
import org.weasis.core.api.image.SimpleOpManager;
import org.weasis.core.api.media.data.MediaElement;

/** Image viewer with a display op chain. */
public abstract class ImageViewerPlugin<E extends MediaElement> extends ViewerPlugin<E> {

  private final SimpleOpManager displayOp;

  protected ImageViewerPlugin(String pluginName) {
    super(pluginName);
    this.displayOp = SimpleOpManager.view2dChain();
  }

  public OpManager getDisplayOpManager() {
    return displayOp;
  }

  public void setLayoutCount(int n) {}

  public int getLayoutCount() {
    return 1;
  }

  public void resetDisplay() {}
}
