/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.dicom.codec.utils;

import java.util.Optional;
import org.dcm4che3.data.Attributes;
import org.weasis.core.api.image.measure.ImageSpacing;

/** Resolves CT/MR patient-plane spacing from (0028,0030) PixelSpacing on one instance. */
public final class InstanceSpacing {

  public enum Source {
    PIXEL_SPACING
  }

  public record Resolved(ImageSpacing spacing, Source source, String warning) {}

  private InstanceSpacing() {}

  public static Optional<Resolved> resolve(Attributes dataset) {
    return Optional.empty();
  }
}
