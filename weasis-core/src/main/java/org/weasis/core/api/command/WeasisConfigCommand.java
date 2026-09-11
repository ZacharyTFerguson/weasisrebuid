/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.core.api.command;

import org.osgi.service.component.annotations.Component;

/** Gogo {@code weasis:config} is launch-only; after boot it reports that. */
@Component(
    immediate = true,
    service = WeasisConfigCommand.class,
    property = {"osgi.command.scope=weasis", "osgi.command.function=config"})
public class WeasisConfigCommand {

  public String config(String... args) {
    return "weasis:config is launch-only (cdb, arg, pro, auth, wcfg)";
  }
}
