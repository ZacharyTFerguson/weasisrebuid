/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.acquire.editor;

import java.util.Hashtable;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.weasis.core.api.gui.Insertable;
import org.weasis.core.api.gui.InsertableFactory;
import org.weasis.core.api.service.UICore;

@Component(service = InsertableFactory.class, immediate = true)
public class AcquireEditorFactory implements InsertableFactory {

  @Activate
  public void activate() {
    UICore.getInstance().registerInsertableFactory(this);
  }

  @Deactivate
  public void deactivate() {
    UICore.getInstance().unregisterInsertableFactory(this);
  }

  @Override
  public Insertable createInstance(Hashtable<String, Object> properties) {
    return new AcquireEditor();
  }

  @Override
  public void dispose(Insertable component) {
    if (component instanceof AcquireEditor editor) {
      editor.dispose();
    }
  }

  @Override
  public boolean isComponentCreatedByThisFactory(Insertable component) {
    return component instanceof AcquireEditor;
  }

  @Override
  public Insertable.Type getType() {
    return Insertable.Type.TOOL;
  }
}
