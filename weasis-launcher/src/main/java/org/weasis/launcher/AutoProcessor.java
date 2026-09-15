/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */

package org.weasis.launcher;

import org.osgi.framework.launch.Framework;

public class AutoProcessor {
  public static void process(Framework framework, ConfigData config) throws Exception {
    if (framework == null || config == null) {
      return;
    }
    BundleInstaller.install(framework, config);
    BundleInstaller.raiseStartLevel(framework, config);
  }
}
