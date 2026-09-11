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

/** Plain-JAR entry point. Native / jpackage starts this class. Not an OSGi bundle. */
public final class AppLauncher extends WeasisLauncher {

  public static void main(String[] args) throws Exception {
    new AppLauncher().launch(args);
  }
}
