/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.core.internal;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.weasis.core.api.i18n.LocaleCoverage;
import org.weasis.core.api.service.BundleTools;
import org.weasis.core.api.service.UICore;

public class Activator implements BundleActivator {

  @Override
  public void start(BundleContext context) {
    UICore.getInstance().setBundleContext(context);
    BundleTools.setBundleContext(context);
    LocaleCoverage.seed(UICore.getInstance().getSystemPreferences());
  }

  @Override
  public void stop(BundleContext context) {
    UICore.getInstance().setBundleContext(null);
    BundleTools.setBundleContext(null);
  }
}
