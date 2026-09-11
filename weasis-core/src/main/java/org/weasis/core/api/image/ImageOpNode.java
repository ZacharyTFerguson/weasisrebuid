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

/** One node in an {@link OpManager} graph. Payload is an opaque image object until WP-2. */
public interface ImageOpNode {

  String INPUT_IMG = "op.input";
  String OUTPUT_IMG = "op.output";

  String getName();

  void setName(String name);

  boolean isEnabled();

  void setEnabled(boolean enabled);

  void setParam(String key, Object value);

  Object getParam(String key);

  Object getParam(String key, Object defaultValue);

  void removeParam(String key);

  void process() throws Exception;

  void clearIOCache();
}
