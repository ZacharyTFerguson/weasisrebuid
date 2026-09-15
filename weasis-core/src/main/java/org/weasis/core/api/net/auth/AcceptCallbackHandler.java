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

import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

public class AcceptCallbackHandler implements CompletionHandler<AsynchronousSocketChannel, Void> {
  private final AcceptCompletionHandler delegate;

  public AcceptCallbackHandler(AcceptCompletionHandler delegate) {
    this.delegate = delegate;
  }

  @Override
  public void completed(AsynchronousSocketChannel result, Void attachment) {
    if (delegate != null) {
      delegate.completed(result);
    }
  }

  @Override
  public void failed(Throwable exc, Void attachment) {
    if (delegate != null) {
      delegate.failed(exc);
    }
  }
}
