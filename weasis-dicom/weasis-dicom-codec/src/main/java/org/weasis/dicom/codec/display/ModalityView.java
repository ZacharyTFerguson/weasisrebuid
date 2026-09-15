/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.dicom.codec.display;

import java.util.EnumMap;
import java.util.Map;

/** Per-modality overlay corner map used by the 2D info layer. */
public final class ModalityView {
  private static final Map<Modality, ModalityInfoData> MAP = new EnumMap<>(Modality.class);

  static {
    for (Modality m : Modality.values()) {
      MAP.put(m, new ModalityInfoData(m));
    }
  }

  private ModalityView() {}

  public static ModalityInfoData getModalityInfos(Modality modality) {
    return MAP.getOrDefault(
        modality == null ? Modality.DEFAULT : modality, MAP.get(Modality.DEFAULT));
  }
}
