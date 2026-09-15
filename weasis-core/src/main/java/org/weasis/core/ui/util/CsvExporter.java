/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.core.ui.util;

import java.io.File;
import java.io.FileWriter;
import java.util.List;

public class CsvExporter {
  public void write(File file, List<String[]> rows) throws Exception {
    try (FileWriter w = new FileWriter(file)) {
      if (rows == null) {
        return;
      }
      for (String[] row : rows) {
        if (row == null) {
          continue;
        }
        for (int i = 0; i < row.length; i++) {
          if (i > 0) {
            w.write(',');
          }
          w.write(escape(row[i]));
        }
        w.write(10);
      }
    }
  }

  static String escape(String v) {
    if (v == null) {
      return "";
    }
    if (v.indexOf(',') >= 0 || v.indexOf('"') >= 0) {
      return '"' + v.replace("\"", "\"\"") + '"';
    }
    return v;
  }
}
