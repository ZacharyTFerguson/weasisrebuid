/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.dicom.wave;

/** Headless millivolt dump used by File &gt; Print ECG Have. */
public class DefaultPrinter {

  public String print(WaveDataReadable data) {
    if (data == null || data.channelCount() == 0) {
      return "";
    }
    StringBuilder builder = new StringBuilder();
    for (ChannelDefinition def : data.channelDefinitions()) {
      builder.append(def.label()).append(':');
      for (int i = 0; i < data.sampleCount(); i++) {
        if (i > 0) {
          builder.append(',');
        }
        builder.append(data.millivolt(def.index(), i));
      }
      builder.append('\n');
    }
    return builder.toString();
  }
}
