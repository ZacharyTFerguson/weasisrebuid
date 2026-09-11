/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.core.api.image;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/** Parameter bag + enable flag. Subclasses implement {@link #processEnabled()}. */
public abstract class AbstractOp implements ImageOpNode {

  private String name;
  private boolean enabled = true;
  private final Map<String, Object> params = new ConcurrentHashMap<>();

  protected AbstractOp(String name) {
    this.name = name;
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public void setName(String name) {
    this.name = name;
  }

  @Override
  public boolean isEnabled() {
    return enabled;
  }

  @Override
  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
  }

  @Override
  public void setParam(String key, Object value) {
    if (key == null) {
      return;
    }
    if (value == null) {
      params.remove(key);
    } else {
      params.put(key, value);
    }
  }

  @Override
  public Object getParam(String key) {
    return params.get(key);
  }

  @Override
  public Object getParam(String key, Object defaultValue) {
    return params.getOrDefault(key, defaultValue);
  }

  @Override
  public void removeParam(String key) {
    params.remove(key);
  }

  @Override
  public void clearIOCache() {
    params.remove(INPUT_IMG);
    params.remove(OUTPUT_IMG);
  }

  @Override
  public final void process() throws Exception {
    if (!enabled) {
      setParam(OUTPUT_IMG, getParam(INPUT_IMG));
      return;
    }
    processEnabled();
  }

  /** Copy input to output unless a subclass paints. */
  protected void processEnabled() throws Exception {
    setParam(OUTPUT_IMG, getParam(INPUT_IMG));
  }
}
