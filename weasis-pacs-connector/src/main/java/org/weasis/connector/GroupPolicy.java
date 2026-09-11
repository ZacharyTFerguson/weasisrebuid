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

/** Groups: user / host / user-group / host-group. A user cannot belong to a host group. */
public final class GroupPolicy {

  public enum Kind {
    USER,
    HOST,
    USER_GROUP,
    HOST_GROUP
  }

  private GroupPolicy() {}

  public static boolean canBelong(Kind member, Kind group) {
    if (member == null || group == null) {
      return false;
    }
    boolean memberUser = member == Kind.USER || member == Kind.USER_GROUP;
    boolean memberHost = member == Kind.HOST || member == Kind.HOST_GROUP;
    boolean groupUser = group == Kind.USER || group == Kind.USER_GROUP;
    boolean groupHost = group == Kind.HOST || group == Kind.HOST_GROUP;
    if (memberUser && groupHost) {
      return false;
    }
    if (memberHost && groupUser) {
      return false;
    }
    return true;
  }
}
