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

import java.util.Objects;

public class Feature<T> implements KeyActionValue {
  private final String title;
  private final int keyCode;
  private final int modifier;
  private final T defaultValue;

  public Feature(String title, T defaultValue) {
    this(title, 0, 0, defaultValue);
  }

  public Feature(String title, int keyCode, T defaultValue) {
    this(title, keyCode, 0, defaultValue);
  }

  public Feature(String title, int keyCode, int modifier, T defaultValue) {
    this.title = Objects.requireNonNull(title);
    this.keyCode = keyCode;
    this.modifier = modifier;
    this.defaultValue = defaultValue;
  }

  public String getTitle() {
    return title;
  }

  public String cmd() {
    return title;
  }

  public T getDefaultValue() {
    return defaultValue;
  }

  public boolean isAction(String command) {
    return title.equalsIgnoreCase(command);
  }

  @Override
  public int getKeyCode() {
    return keyCode;
  }

  @Override
  public int getModifier() {
    return modifier;
  }

  @Override
  public String toString() {
    return title;
  }
}
