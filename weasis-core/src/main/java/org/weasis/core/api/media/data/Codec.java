/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.core.api.media.data;

import java.net.URI;
import java.util.Hashtable;

/**
 * Decode/encode plugin. Registered as {@code @Component(service = Codec.class)} — not a factory.
 */
public interface Codec {

  String getCodecName();

  String[] getReaderMIMETypes();

  String[] getReaderExtensions();

  String[] getWriterMIMETypes();

  String[] getWriterExtensions();

  boolean isMimeTypeSupported(String mimeType);

  MediaReader getMediaIO(URI media, String mimeType, Hashtable<String, Object> properties);
}
