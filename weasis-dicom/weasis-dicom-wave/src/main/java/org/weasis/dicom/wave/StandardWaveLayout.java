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

import java.util.ArrayList;
import java.util.List;

/** Standard 2/4/12-lead ECG page using available channel labels. */
public class StandardWaveLayout extends WaveLayout {

  public StandardWaveLayout(Format format, List<Lead> leads) {
    super(format, leads);
  }

  public static StandardWaveLayout of(Format format, List<ChannelDefinition> channels) {
    Format page = format == null ? Format.forChannelCount(size(channels)) : format;
    List<Lead> available = new ArrayList<>();
    if (channels != null) {
      for (ChannelDefinition ch : channels) {
        available.add(ch.lead() == Lead.UNKNOWN ? Lead.UNKNOWN : ch.lead());
      }
    }
    List<Lead> ordered = new ArrayList<>();
    if (page == Format.DEFAULT && available.size() >= 12) {
      for (Lead lead : twelveLeadOrder()) {
        if (available.contains(lead)) {
          ordered.add(lead);
        }
      }
    }
    if (ordered.isEmpty()) {
      int limit = Math.min(page.leadCount(), available.size());
      ordered.addAll(available.subList(0, limit));
    }
    return new StandardWaveLayout(page, ordered);
  }

  static int size(List<ChannelDefinition> channels) {
    return channels == null ? 0 : channels.size();
  }
}
