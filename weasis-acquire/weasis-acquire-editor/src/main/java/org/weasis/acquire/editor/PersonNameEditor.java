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

import org.weasis.acquire.explorer.gui.central.meta.panel.PersonNameView;

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

  public static Components parse(String pn) {
    if (pn == null || pn.isBlank()) {
      return Components.empty();
    }
    int eq = pn.indexOf('=');
    String main = eq < 0 ? pn : pn.substring(0, eq);
    String[] parts = main.split("\\^", -1);
    return new Components(
        part(parts, 0), part(parts, 1), part(parts, 2), part(parts, 3), part(parts, 4));
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

  public static boolean isValid(Components c) {
    Components x = c == null ? Components.empty() : c;
    return isValidComponent(x.last())
        && isValidComponent(x.first())
        && isValidComponent(x.middle())
        && isValidComponent(x.prefix())
        && isValidComponent(x.suffix());
  }

  /** Explorer table chrome delegates PN compose/validate to this helper. */
  public static Components fromView(PersonNameView view) {
    if (view == null) {
      return Components.empty();
    }
    return new Components(view.last(), view.first(), view.middle(), view.prefix(), view.suffix());
  }

  public static void writeTo(PersonNameView view, Components c) {
    if (view == null) {
      return;
    }
    Components x = c == null ? Components.empty() : c;
    view.setComponents(x.last(), x.first(), x.middle(), x.prefix(), x.suffix());
  }

  public static String compose(PersonNameView view) {
    if (view == null) {
      return compose(Components.empty());
    }
    if (view.preservesInbound()) {
      return view.composed();
    }
    return compose(fromView(view));
  }

  public static boolean isValid(PersonNameView view) {
    return view == null || isValid(fromView(view));
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

  private static String part(String[] parts, int index) {
    return parts != null && index < parts.length ? parts[index] : "";
  }

  private static String nz(String s) {
    return s == null ? "" : s;
  }
}
