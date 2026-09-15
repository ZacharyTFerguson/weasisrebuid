/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */

package org.weasis.core.api.util;

import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;

public final class GraphicsInfo {
  private GraphicsInfo() {}

  public static boolean isHeadless() {
    return GraphicsEnvironment.isHeadless();
  }

  public static GraphicsDevice[] devices() {
    if (isHeadless()) {
      return new GraphicsDevice[0];
    }
    return GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices();
  }
}
