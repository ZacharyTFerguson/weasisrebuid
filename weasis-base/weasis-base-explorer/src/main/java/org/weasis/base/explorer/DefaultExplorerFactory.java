/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.base.explorer;

import java.util.Hashtable;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.weasis.core.api.explorer.DataExplorerView;
import org.weasis.core.api.explorer.DataExplorerViewFactory;
import org.weasis.core.api.gui.Insertable;
import org.weasis.core.api.service.UICore;

@Component(service = DataExplorerViewFactory.class, immediate = true)
public class DefaultExplorerFactory implements DataExplorerViewFactory {

  @Activate
  public void activate() {
    UICore.getInstance().registerExplorerFactory(this);
  }

  @Deactivate
  public void deactivate() {
    UICore.getInstance().unregisterInsertableFactory(this);
  }

  @Override
  public DataExplorerView createInstance(Hashtable<String, Object> properties) {
    return new DefaultExplorer();
  }

  @Override
  public void dispose(Insertable component) {
    if (component instanceof DefaultExplorer explorer) {
      explorer.dispose();
    }
  }

  @Override
  public boolean isComponentCreatedByThisFactory(Insertable component) {
    return component instanceof DefaultExplorer;
  }
}
