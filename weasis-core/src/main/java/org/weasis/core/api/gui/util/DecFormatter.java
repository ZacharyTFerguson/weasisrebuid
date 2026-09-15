/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */

package org.weasis.core.api.gui.util;

import java.text.DecimalFormat;
import java.util.Locale;

public final class DecFormatter {
  private DecFormatter() {}

  public static String oneDecimal(double v) {
    return new DecimalFormat("0.0").format(v);
  }

  public static String twoDecimal(double v) {
    return new DecimalFormat("0.00").format(v);
  }

  public static String percent(double v) {
    return new DecimalFormat("0.#%").format(v);
  }

  public static String scientific(double v) {
    return String.format(Locale.US, "%g", v);
  }
}
