/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.base.viewer2d;

import org.osgi.service.component.annotations.Component;
import org.weasis.core.api.command.ImageCommandArgs;

@Component(
    immediate = true,
    service = ImageProtocolCommands.class,
    property = {
      "osgi.command.scope=image",
      "osgi.command.function=get",
      "osgi.command.function=close"
    })
public class ImageProtocolCommands {

  public String get(String... args) {
    ImageCommandArgs.GetMode mode = ImageCommandArgs.parseGet(args);
    String value = ImageCommandArgs.parseGetValue(args);
    if (mode == ImageCommandArgs.GetMode.HELP || value == null) {
      return "Usage: image:get (-f file | -u URL)";
    }
    return "image " + mode.name().toLowerCase() + " " + value;
  }

  public String close(String... args) {
    ImageCommandArgs.CloseMode mode = ImageCommandArgs.parseClose(args);
    if (mode == ImageCommandArgs.CloseMode.HELP) {
      return "Usage: image:close (-a | -g group | -s series)";
    }
    return "closed " + mode.name().toLowerCase();
  }
}
