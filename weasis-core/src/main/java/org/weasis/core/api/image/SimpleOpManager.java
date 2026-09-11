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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/** Linear {@link OpManager}: each node's output is the next node's input. */
public class SimpleOpManager implements OpManager {

  private final List<ImageOpNode> nodes = new ArrayList<>();
  private Object firstInput;
  private boolean dirty = true;

  @Override
  public List<ImageOpNode> getOperations() {
    return Collections.unmodifiableList(nodes);
  }

  @Override
  public ImageOpNode getNode(String name) {
    if (name == null) {
      return null;
    }
    for (ImageOpNode node : nodes) {
      if (name.equals(node.getName())) {
        return node;
      }
    }
    return null;
  }

  @Override
  public void addImageOperationAction(ImageOpNode node) {
    Objects.requireNonNull(node, "node");
    nodes.add(node);
    dirty = true;
  }

  @Override
  public void removeImageOpActionNode(ImageOpNode node) {
    if (nodes.remove(node)) {
      dirty = true;
    }
  }

  @Override
  public void setParamValue(String opName, String paramKey, Object value) {
    ImageOpNode node = getNode(opName);
    if (node != null) {
      node.setParam(paramKey, value);
      dirty = true;
    }
  }

  @Override
  public Object getParamValue(String opName, String paramKey) {
    ImageOpNode node = getNode(opName);
    return node == null ? null : node.getParam(paramKey);
  }

  @Override
  public void setFirstNode(Object img) {
    this.firstInput = img;
    dirty = true;
  }

  @Override
  public Object getFirstNodeInputImage() {
    return firstInput;
  }

  @Override
  public ImageOpNode getFirstNode() {
    return nodes.isEmpty() ? null : nodes.getFirst();
  }

  @Override
  public ImageOpNode getLastNode() {
    return nodes.isEmpty() ? null : nodes.getLast();
  }

  @Override
  public Object process() throws Exception {
    Object img = firstInput;
    for (ImageOpNode node : nodes) {
      node.setParam(ImageOpNode.INPUT_IMG, img);
      node.process();
      Object out = node.getParam(ImageOpNode.OUTPUT_IMG);
      if (out != null) {
        img = out;
      }
    }
    dirty = false;
    return img;
  }

  @Override
  public boolean needProcessing() {
    return dirty;
  }

  @Override
  public void clearNodeIOCache() {
    for (ImageOpNode node : nodes) {
      node.clearIOCache();
    }
    dirty = true;
  }

  /** View2d DICOM chain (ARCHITECTURE §5.1 / §2.6). */
  public static SimpleOpManager view2dChain() {
    SimpleOpManager manager = new SimpleOpManager();
    manager.addImageOperationAction(new WindowAndPresetsOp());
    manager.addImageOperationAction(new FilterOp());
    manager.addImageOperationAction(new PseudoColorOp());
    manager.addImageOperationAction(new ShutterOp());
    manager.addImageOperationAction(new OverlayOp());
    manager.addImageOperationAction(new AffineTransformOp());
    return manager;
  }
}
