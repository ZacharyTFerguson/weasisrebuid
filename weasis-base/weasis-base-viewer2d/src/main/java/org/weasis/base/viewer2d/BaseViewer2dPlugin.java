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

import javax.swing.JLabel;
import javax.swing.SwingConstants;
import org.weasis.core.api.media.data.MediaElement;
import org.weasis.core.ui.editor.image.ImageViewerPlugin;

public class BaseViewer2dPlugin extends ImageViewerPlugin<MediaElement> {

  public BaseViewer2dPlugin() {
    super("Image viewer");
    add(new JLabel("Non-DICOM 2D", SwingConstants.CENTER));
  }
}
