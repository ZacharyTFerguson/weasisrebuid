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

import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

/** Local loopback redirect listener used by OAuth authorization-code flow. */
public class AsyncCallbackServerHandler {
  private final int port;
  private volatile String code = "";

  public AsyncCallbackServerHandler(int port) {
    this.port = port;
  }

  public int getPort() {
    return port;
  }

  public String getCode() {
    return code;
  }

  public void bind(AsynchronousServerSocketChannel server) throws Exception {
    if (server == null) {
      return;
    }
    server.bind(new InetSocketAddress("127.0.0.1", port));
    server.accept(
        null,
        new CompletionHandler<AsynchronousSocketChannel, Void>() {
          @Override
          public void completed(AsynchronousSocketChannel result, Void attachment) {
            code = "received";
          }

          @Override
          public void failed(Throwable exc, Void attachment) {}
        });
  }
}
