/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.core.internal.cv;

import java.net.URI;
import java.util.Hashtable;
import org.weasis.core.api.media.data.Codec;
import org.weasis.core.api.media.data.MediaReader;

/** Host-bundle codec marker; native bits live in the OpenCV fragment. */
public class NativeOpenCVCodec implements Codec {
  @Override
  public String getCodecName() {
    return "NativeOpenCVCodec";
  }

  @Override
  public String[] getReaderMIMETypes() {
    return new String[] {"image/x-portable-anymap"};
  }

  @Override
  public String[] getReaderExtensions() {
    return new String[] {"pnm", "pgm", "ppm"};
  }

  @Override
  public String[] getWriterMIMETypes() {
    return new String[0];
  }

  @Override
  public String[] getWriterExtensions() {
    return new String[0];
  }

  @Override
  public boolean isMimeTypeSupported(String mimeType) {
    return mimeType != null && mimeType.startsWith("image/");
  }

  @Override
  public MediaReader getMediaIO(URI media, String mimeType, Hashtable<String, Object> properties) {
    return null;
  }
}
