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

import java.time.Instant;

/**
 * ViewerHub Launch APIs (weasis.org, not typical IHE): {@code lowerDateTime} = older than; {@code
 * upperDateTime} = more recent than.
 */
public final class DateFilters {

  public enum Meaning {
    OLDER_THAN,
    MORE_RECENT_THAN
  }

  private DateFilters() {}

  public static Meaning lowerDateTime() {
    return Meaning.OLDER_THAN;
  }

  public static Meaning upperDateTime() {
    return Meaning.MORE_RECENT_THAN;
  }

  public static boolean matches(Instant study, Instant lower, Instant upper) {
    if (study == null) {
      return false;
    }
    if (lower != null && !study.isBefore(lower)) {
      return false;
    }
    if (upper != null && !study.isAfter(upper)) {
      return false;
    }
    return true;
  }
}
