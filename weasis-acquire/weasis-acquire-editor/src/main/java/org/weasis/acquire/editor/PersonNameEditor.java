/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.acquire.editor;

/**
 * PersonName component editor (4.7.3): Last, First, Middle, Prefix, Suffix. Reject caret, equals,
 * and backslash. Preview turns red over 64 characters. Ideographic/phonetic groups after equals are
 * preserved. Worklist / {@code $acquire:patient} values are not reformatted. Table shows lexical
 * order (Smith, John).
 */
public final class PersonNameEditor {

  public record Components(String last, String first, String middle, String prefix, String suffix) {

    public static Components empty() {
      return new Components("", "", "", "", "");
    }
  }

  private PersonNameEditor() {}

  public static boolean isValidComponent(String part) {
    if (part == null || part.isEmpty()) {
      return true;
    }
    return part.indexOf('^') < 0 && part.indexOf('=') < 0 && part.indexOf('\\') < 0;
  }

  public static String compose(Components c) {
    Components x = c == null ? Components.empty() : c;
    return String.join(
        "^", nz(x.last()), nz(x.first()), nz(x.middle()), nz(x.prefix()), nz(x.suffix()));
  }

  public static boolean previewOverLength(String composed) {
    return composed != null && composed.length() > 64;
  }

  public static String applyInbound(String raw, boolean fromWorklistOrCommand) {
    if (fromWorklistOrCommand) {
      return raw;
    }
    return raw == null ? "" : raw.trim();
  }

  /** Table display: Family, Given. Groups after {@code =} are preserved. */
  public static String lexicalDisplay(String pn) {
    if (pn == null || pn.isBlank()) {
      return "";
    }
    int eq = pn.indexOf('=');
    String ideo = eq < 0 ? "" : pn.substring(eq);
    String main = eq < 0 ? pn : pn.substring(0, eq);
    String[] parts = main.split("\\^", -1);
    String last = parts.length > 0 ? parts[0] : "";
    String first = parts.length > 1 ? parts[1] : "";
    if (last.isBlank() && first.isBlank()) {
      return pn;
    }
    if (first.isBlank()) {
      return last + ideo;
    }
    return last + ", " + first + ideo;
  }

  private static String nz(String s) {
    return s == null ? "" : s;
  }
}
