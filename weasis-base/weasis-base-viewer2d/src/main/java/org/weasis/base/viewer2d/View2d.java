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

import java.io.File;
import java.io.IOException;
import org.weasis.core.api.media.MimeInspector;
import org.weasis.core.api.media.data.ImageElement;
import org.weasis.core.api.media.data.MediaElement;
import org.weasis.core.api.media.data.MediaReader;
import org.weasis.core.ui.editor.image.DefaultView2d;
import org.weasis.imageio.codec.ImageioCodec;

/** Non-DICOM 2D canvas. */
public class View2d extends DefaultView2d<MediaElement> {

  public void load(File file) throws IOException {
    if (file == null || !file.isFile()) {
      throw new IOException("image file");
    }
    MediaReader reader =
        new ImageioCodec().getMediaIO(file.toURI(), MimeInspector.getMimeType(file), null);
    if (reader == null) {
      throw new IOException("unsupported image");
    }
    if (reader.getPreview() instanceof ImageElement image && image.getImage() != null) {
      setSourceImage(image.getImage());
      return;
    }
    throw new IOException("unreadable image");
  }
}
