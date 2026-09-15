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

import java.util.List;

/** Chooses a {@link StandardWaveLayout} for the current format and channels. */
public class WaveLayoutManager {

  public WaveLayout layout(Format format, List<ChannelDefinition> channels) {
    return StandardWaveLayout.of(format, channels);
  }

  public WaveLayout layout(WaveDataReadable data) {
    if (data == null) {
      return StandardWaveLayout.of(Format.TWO, List.of());
    }
    Format format = Format.forChannelCount(data.channelCount());
    return StandardWaveLayout.of(format, data.channelDefinitions());
  }
}
