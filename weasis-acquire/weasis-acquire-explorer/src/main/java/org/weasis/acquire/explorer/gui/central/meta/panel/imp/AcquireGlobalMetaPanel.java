/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.acquire.explorer.gui.central.meta.panel.imp;

import java.util.Map;
import java.util.Properties;
import org.weasis.acquire.explorer.gui.central.meta.model.imp.AcquireGlobalMeta;
import org.weasis.acquire.explorer.gui.central.meta.panel.AcquireMetadataPanel;

public class AcquireGlobalMetaPanel extends AcquireMetadataPanel {

  public AcquireGlobalMetaPanel(Properties prefs, Map<String, String> values) {
    super(new AcquireGlobalMeta(prefs, values));
  }
}
