/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.base.ui.internal;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

/** UI aggregator named by {@code weasis.main.ui}. Delegates to {@link MainWindowActivator}. */
public class Activator implements BundleActivator {
  private final MainWindowActivator delegate = new MainWindowActivator();

  @Override
  public void start(BundleContext context) {
    delegate.start(context);
  }

  @Override
  public void stop(BundleContext context) {
    delegate.stop(context);
  }
}
