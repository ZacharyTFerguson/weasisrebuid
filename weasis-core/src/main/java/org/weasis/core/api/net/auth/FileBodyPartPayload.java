/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.core.api.net.auth;

import java.io.File;
import java.net.http.HttpRequest;
import java.nio.file.Path;

public class FileBodyPartPayload implements BodySupplier {
  private final Path path;

  public FileBodyPartPayload(File file) {
    this.path = file == null ? null : file.toPath();
  }

  @Override
  public HttpRequest.BodyPublisher body() {
    if (path == null) {
      return HttpRequest.BodyPublishers.noBody();
    }
    try {
      return HttpRequest.BodyPublishers.ofFile(path);
    } catch (Exception e) {
      return HttpRequest.BodyPublishers.noBody();
    }
  }
}
