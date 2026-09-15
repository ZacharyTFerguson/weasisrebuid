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

import org.weasis.core.api.image.AbstractOp;

/** DICOM 60xx overlay bits (ARCHITECTURE §5.1). */
public class OverlayOp extends AbstractOp {
  public static final String P_IMAGE_ELEMENT = "dicom.image";
  public static final String P_ENABLED = "overlay.enabled";

  public OverlayOp() {
    super("op.overlay.dicom");
    setParam(P_ENABLED, Boolean.TRUE);
  }
}
