/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.core.api.vol;

/**
 * Volume / MPR primitives live here in later WPs. WP-1 only publishes the package so clones can
 * tick {@code org.weasis.core.api.vol}.
 */
public final class VolumeSpace {

  private VolumeSpace() {}

  /** Isotropic slice-space origin; volume center is {@code (halfSlice)³} in later WPs. */
  public static double halfSlice(int sliceCount) {
    return sliceCount / 2.0;
  }
}
