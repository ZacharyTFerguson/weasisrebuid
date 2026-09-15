/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.base.viewer2d;

import org.weasis.core.api.media.data.MediaElement;
import org.weasis.core.ui.editor.image.ImageViewerPlugin;

public class View2dContainer extends ImageViewerPlugin<MediaElement> {

  public static final String NAME = "Image 2D";

  private final View2d view2d = new View2d();

  public View2dContainer() {
    super(NAME);
    add(view2d);
  }

  public View2d getView2d() {
    return view2d;
  }
}
