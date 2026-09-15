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

import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLaf;

/**
 * Recommended Weasis look and feel ({@code weasis.theme}). Core Dark, independently of nroduit
 * theme JSON.
 */
public class FlatWeasisTheme extends FlatDarkLaf {

  public static final String NAME = "Weasis";

  public static boolean setup() {
    return FlatLaf.setup(new FlatWeasisTheme());
  }

  @Override
  public String getName() {
    return NAME;
  }

  @Override
  public String getID() {
    return NAME;
  }

  @Override
  public String getDescription() {
    return "Weasis Core Dark";
  }
}
