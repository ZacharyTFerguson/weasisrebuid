/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */

package org.weasis.core.api.net;

public class AuthResponse {
  private final int status;
  private final String body;

  public AuthResponse(int status, String body) {
    this.status = status;
    this.body = body == null ? "" : body;
  }

  public int getStatus() {
    return status;
  }

  public String getBody() {
    return body;
  }

  public boolean isOk() {
    return status >= 200 && status < 300;
  }
}
