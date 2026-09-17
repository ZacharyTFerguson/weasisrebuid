/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.dicom.viewer2d.mpr;

/** Derived orthogonal MPR series built from {@link VolImageIO} into {@link RawImageIO}. */
public class DerivedStack extends AbstractStack {

  public DerivedStack() {}

  public DerivedStack(Volume volume) {
    super(volume);
  }

  public RawImageIO rawSlice(MprAxis axis, int index) {
    return new VolImageIO(getVolume(), axis, index).toRaw();
  }
}
