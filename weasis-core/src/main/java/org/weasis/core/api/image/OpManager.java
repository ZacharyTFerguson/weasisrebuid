/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.core.api.image;

import java.util.List;

/** Ordered image-op graph. */
public interface OpManager {

  List<ImageOpNode> getOperations();

  ImageOpNode getNode(String name);

  void addImageOperationAction(ImageOpNode node);

  void removeImageOpActionNode(ImageOpNode node);

  void setParamValue(String opName, String paramKey, Object value);

  Object getParamValue(String opName, String paramKey);

  void setFirstNode(Object img);

  Object getFirstNodeInputImage();

  ImageOpNode getFirstNode();

  ImageOpNode getLastNode();

  Object process() throws Exception;

  boolean needProcessing();

  void clearNodeIOCache();
}
