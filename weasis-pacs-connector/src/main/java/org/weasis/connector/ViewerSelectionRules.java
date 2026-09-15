/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.connector;

import java.util.ArrayList;
import java.util.List;

/** Newest-on-top ViewerHub selection: modalities OR; archive or ALL. Default WEASIS. */
public final class ViewerSelectionRules {

  public record Rule(DisplayQuery.Viewer viewer, List<String> modalities, String archive) {
    public Rule {
      modalities = modalities == null ? List.of() : List.copyOf(modalities);
    }
  }

  private final List<Rule> rules;

  public ViewerSelectionRules(List<Rule> rules) {
    this.rules = rules == null ? List.of() : List.copyOf(rules);
  }

  public static ViewerSelectionRules defaults() {
    return new ViewerSelectionRules(
        List.of(new Rule(DisplayQuery.Viewer.WEASIS, List.of(), "ALL")));
  }

  public DisplayQuery.Viewer pick(DisplayQuery query) {
    for (Rule rule : rules) {
      if (matches(rule, query)) {
        return rule.viewer();
      }
    }
    return DisplayQuery.Viewer.WEASIS;
  }

  static boolean matches(Rule rule, DisplayQuery query) {
    if (query == null) {
      return false;
    }
    if (rule.archive() != null
        && !"ALL".equalsIgnoreCase(rule.archive())
        && query.archive() != null
        && !rule.archive().equalsIgnoreCase(query.archive())) {
      return false;
    }
    if (rule.modalities().isEmpty() || query.modalitiesInStudy().isEmpty()) {
      return true;
    }
    for (String wanted : rule.modalities()) {
      for (String have : query.modalitiesInStudy()) {
        if (wanted.equalsIgnoreCase(have)) {
          return true;
        }
      }
    }
    return false;
  }

  public ViewerSelectionRules addFirst(Rule rule) {
    List<Rule> next = new ArrayList<>();
    next.add(rule);
    next.addAll(rules);
    return new ViewerSelectionRules(next);
  }
}
