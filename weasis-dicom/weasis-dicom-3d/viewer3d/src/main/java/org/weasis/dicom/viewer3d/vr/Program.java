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

public class Program {

  private final String name;
  private final String vertexSource;
  private final String fragmentSource;
  private boolean compiled;

  public Program(String name, String vertexSource, String fragmentSource) {
    this.name = name;
    this.vertexSource = vertexSource == null ? "" : vertexSource;
    this.fragmentSource = fragmentSource == null ? "" : fragmentSource;
  }

  public String getName() {
    return name;
  }

  public String getVertexSource() {
    return vertexSource;
  }

  public String getFragmentSource() {
    return fragmentSource;
  }

  public boolean isCompiled() {
    return compiled;
  }

  public void markCompiled(boolean compiled) {
    this.compiled = compiled;
  }
}
