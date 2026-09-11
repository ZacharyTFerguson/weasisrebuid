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

/** Rectangular shutter; black outside the box. */
public class ShutterOp extends AbstractOp {

  public static final String P_ENABLED = "shutter.enabled";
  public static final String P_LEFT = "shutter.left";
  public static final String P_RIGHT = "shutter.right";
  public static final String P_UPPER = "shutter.upper";
  public static final String P_LOWER = "shutter.lower";

  public ShutterOp() {
    super("op.shutter");
    setParam(P_ENABLED, Boolean.FALSE);
  }
}
