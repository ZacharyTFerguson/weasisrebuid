/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.dicom.explorer;

import javax.swing.JLabel;
import javax.swing.SwingConstants;
import org.weasis.core.api.media.data.MediaElement;
import org.weasis.core.ui.editor.image.ViewerPlugin;

/** Placeholder OS-handler viewer. Live Desktop open is GUI Pass, not this WP. */
public class MimeSystemAppViewer extends ViewerPlugin<MediaElement> {

  public MimeSystemAppViewer() {
    super("System application");
    add(new JLabel("OS handler", SwingConstants.CENTER));
  }
}
