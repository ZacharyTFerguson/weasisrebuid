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

import java.util.LinkedHashMap;
import java.util.Map;

public class ShaderManager {

  private final Map<RenderingType, Program> programs = new LinkedHashMap<>();

  public ShaderManager() {
    programs.put(
        RenderingType.COMPOSITE,
        new Program("composite", "// vertex", "// fragment composite"));
    programs.put(RenderingType.MIP, new Program("mip", "// vertex", "// fragment mip"));
    programs.put(RenderingType.MINIP, new Program("minip", "// vertex", "// fragment minip"));
    programs.put(RenderingType.ISO, new Program("iso", "// vertex", "// fragment iso"));
  }

  public Program program(RenderingType type) {
    return programs.get(type);
  }
}
