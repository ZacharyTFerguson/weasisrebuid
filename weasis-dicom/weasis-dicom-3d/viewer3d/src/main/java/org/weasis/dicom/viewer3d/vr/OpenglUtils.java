/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.dicom.viewer3d.vr;

import org.weasis.dicom.viewer3d.OpenGLInfo;

/** Helpers around {@link OpenGLInfo} for the VR pipeline. Does not call JOGL. */
public final class OpenglUtils {

  private OpenglUtils() {}

  public static OpenGLInfo.Caps probe(String renderer, String version) {
    return OpenGLInfo.describe(renderer, version);
  }

  public static boolean allowVolumeRendering(OpenGLInfo.Caps caps) {
    return caps != null && caps.canRenderVolume();
  }
}
