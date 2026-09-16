/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.dicom.viewer2d;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.Supplier;
import org.osgi.service.component.annotations.Component;
import org.weasis.core.api.image.AffineTransformOp;
import org.weasis.core.api.service.UICore;
import org.weasis.core.ui.editor.image.ImageViewerPlugin;
import org.weasis.core.ui.editor.image.MouseActions;
import org.weasis.core.ui.editor.image.SynchView;
import org.weasis.core.ui.editor.image.ViewerToolBar;

/**
 * Gogo {@code dcmview2d:*} after the 2D plugin is up. {@code move} and {@code wl} require {@code
 * --} before negative numbers.
 */
@Component(
    immediate = true,
    service = DicomView2dCommands.class,
    property = {
      "osgi.command.scope=dcmview2d",
      "osgi.command.function=layout",
      "osgi.command.function=mouseLeftAction",
      "osgi.command.function=move",
      "osgi.command.function=wl",
      "osgi.command.function=reset",
      "osgi.command.function=scroll",
      "osgi.command.function=synch",
      "osgi.command.function=zoom"
    })
public class DicomView2dCommands {

  private final Supplier<View2d> target;

  public DicomView2dCommands() {
    this(View2dRegistry::selected);
  }

  public DicomView2dCommands(View2d view) {
    this(() -> view);
  }

  public DicomView2dCommands(Supplier<View2d> target) {
    this.target = target;
  }

  public String layout(String... args) {
    return applyLayout(requireView(), tokens(args));
  }

  String applyLayout(View2d view, List<String> a) {
    Integer n = intFlag(a, "-n");
    Integer i = intFlag(a, "-i");
    return applyLayout(containerOf(view), n, i);
  }

  static String applyLayout(View2dContainer host, Integer n, Integer i) {
    if (n != null && host != null) {
      host.setLayoutCount(n);
    }
    return layoutResult(host, n, i);
  }

  static String layoutResult(View2dContainer host, Integer n, Integer i) {
    if (i != null && host != null) {
      host.setLayoutIndex(i);
      return "layout -i " + host.getLayoutIndex();
    }
    return n != null ? "layout -n " + n : "layout";
  }

  static View2dContainer containerOf(View2d view) {
    if (view == null) {
      return null;
    }
    Object tagged = view.getClientProperty(View2dContainer.class);
    if (tagged instanceof View2dContainer taggedHost) {
      return taggedHost;
    }
    return containerWalk(view);
  }

  static View2dContainer containerWalk(java.awt.Component c) {
    while (c != null) {
      if (c instanceof View2dContainer host) {
        return host;
      }
      c = c.getParent();
    }
    return focusedContainer();
  }

  static View2dContainer focusedContainer() {
    ImageViewerPlugin<?> plugin = UICore.getInstance().getFocusedImagePlugin();
    return plugin instanceof View2dContainer host ? host : null;
  }

  static Integer intFlag(List<String> a, String flag) {
    int i = indexOfFlag(a, flag);
    if (i < 0) {
      return null;
    }
    return parseFlag(a, i, flag);
  }

  static Integer parseFlag(List<String> a, int i, String flag) {
    String t = a.get(i);
    String glued = gluedValue(t, flag);
    if (glued != null) {
      return Integer.valueOf(glued);
    }
    return i + 1 < a.size() ? Integer.valueOf(a.get(i + 1)) : null;
  }

  static String gluedValue(String t, String flag) {
    if (!hasGlued(t, flag)) {
      return null;
    }
    return stripEquals(t.substring(flag.length()));
  }

  static boolean hasGlued(String t, String flag) {
    return t.startsWith(flag) && t.length() > flag.length();
  }

  static String stripEquals(String rest) {
    if (rest.startsWith("=")) {
      rest = rest.substring(1);
    }
    return rest.isEmpty() ? null : rest;
  }

  public String mouseLeftAction(String... args) {
    View2d view = requireView();
    List<String> a = tokens(args);
    if (a.isEmpty()) {
      return view.getMouseActions().getLeft();
    }
    view.getMouseActions().setLeft(a.getFirst());
    ViewerToolBar.bindMeasureTool(view);
    applyLeftToLayout(view);
    return view.getMouseActions().getLeft();
  }

  static void applyLeftToLayout(View2d view) {
    Object host = view.getClientProperty(View2dContainer.class);
    if (!(host instanceof View2dContainer container)) {
      return;
    }
    String left = view.getMouseActions().getLeft();
    String tool = view.getMeasureTool();
    for (View2d cell : container.getLayoutViews()) {
      cell.getMouseActions().setLeft(left);
      cell.setMeasureTool(tool);
    }
  }

  public String move(String... args) {
    View2d view = requireView();
    double[] nums = numbersRequiringDashDash(args);
    double x = nums.length > 0 ? nums[0] : 0;
    double y = nums.length > 1 ? nums[1] : 0;
    view.setPan(view.getPanX() + x, view.getPanY() + y);
    return view.getPanX() + " " + view.getPanY();
  }

  public String wl(String... args) {
    View2d view = requireView();
    double[] nums = numbersRequiringDashDash(args);
    if (nums.length >= 2) {
      view.setWindowLevel(nums[0], nums[1]);
    } else if (nums.length == 1) {
      view.setWindowLevel(nums[0], view.getLevel());
    }
    return view.getWindow() + " " + view.getLevel();
  }

  public String reset(String... args) {
    View2d view = requireView();
    List<String> a = tokens(args);
    if (a.isEmpty() || a.contains("-a")) {
      view.resetView("-a");
      return "reset all";
    }
    view.resetView(a.getFirst());
    return "reset " + a.getFirst();
  }

  public String scroll(String... args) {
    View2d view = requireView();
    List<String> a = tokens(args);
    int s = indexOfFlag(a, "-s");
    if (s >= 0 && s + 1 < a.size()) {
      view.setFrameIndex(Integer.parseInt(a.get(s + 1)));
    } else if (a.contains("-i")) {
      view.setFrameIndex(view.getFrameIndex() + 1);
    } else if (a.contains("-d")) {
      view.setFrameIndex(Math.max(0, view.getFrameIndex() - 1));
    }
    return Integer.toString(view.getFrameIndex());
  }

  public String synch(String... args) {
    View2d view = requireView();
    List<String> a = tokens(args);
    if (a.isEmpty()) {
      return view.getSynch().name();
    }
    String v = a.getFirst();
    SynchView synch =
        switch (v.toLowerCase(Locale.ROOT)) {
          case "none" -> SynchView.NONE;
          case "tile" -> SynchView.TILE;
          default -> SynchView.STACK;
        };
    view.setSynch(synch);
    return synch.name();
  }

  public String zoom(String... args) {
    View2d view = requireView();
    List<String> a = tokens(args);
    if (a.isEmpty()) {
      return Double.toString(view.getZoom());
    }
    int setFlag = indexOfFlag(a, "-s");
    if (setFlag >= 0) {
      String raw = setFlag + 1 < a.size() ? a.get(setFlag + 1) : "";
      if (raw.startsWith("--set=")) {
        raw = raw.substring("--set=".length());
      }
      view.setZoom(Double.parseDouble(raw));
      return Double.toString(view.getZoom());
    }
    if ("set".equalsIgnoreCase(a.getFirst()) && a.size() > 1) {
      view.setZoom(Double.parseDouble(a.get(1)));
    } else if ("increase".equalsIgnoreCase(a.getFirst())) {
      double n = a.size() > 1 ? Double.parseDouble(a.get(1)) : 1;
      view.increaseZoom(n);
    } else if ("decrease".equalsIgnoreCase(a.getFirst())) {
      double n = a.size() > 1 ? Double.parseDouble(a.get(1)) : 1;
      view.increaseZoom(-n);
    } else {
      view.setZoom(Double.parseDouble(a.getFirst()));
    }
    return Double.toString(view.getZoom());
  }

  View2d requireView() {
    View2d view = target.get();
    if (view == null) {
      throw new IllegalStateException("no View2d");
    }
    return view;
  }

  static List<String> tokens(String... args) {
    List<String> out = new ArrayList<>();
    if (args == null) {
      return out;
    }
    for (String a : args) {
      if (a != null && !a.isBlank()) {
        out.add(a);
      }
    }
    return out;
  }

  static int indexOfFlag(List<String> a, String flag) {
    for (int i = 0; i < a.size(); i++) {
      if (flagMatch(a.get(i), flag)) {
        return i;
      }
    }
    return -1;
  }

  static boolean flagMatch(String t, String flag) {
    return flag.equals(t) || t.startsWith(flag + "=") || gluedNumber(t, flag);
  }

  static boolean gluedNumber(String t, String flag) {
    return t.startsWith(flag)
        && t.length() > flag.length()
        && Character.isDigit(t.charAt(flag.length()));
  }

  /**
   * Negative numbers are only accepted after {@code --}. Positive numbers may appear without it.
   */
  static double[] numbersRequiringDashDash(String... args) {
    List<String> a = tokens(args);
    boolean allowNeg = false;
    List<Double> nums = new ArrayList<>();
    for (String t : a) {
      if ("--".equals(t)) {
        allowNeg = true;
        continue;
      }
      try {
        double v = Double.parseDouble(t);
        if (v < 0 && !allowNeg) {
          throw new IllegalArgumentException("negative requires -- : " + t);
        }
        nums.add(v);
      } catch (NumberFormatException e) {
        // skip flags
      }
    }
    double[] out = new double[nums.size()];
    for (int i = 0; i < nums.size(); i++) {
      out[i] = nums.get(i);
    }
    return out;
  }

  public static String helpZoom() {
    return "dcmview2d:zoom (set VALUE | increase NUMBER | decrease NUMBER); -s --set=VALUE; "
        + AffineTransformOp.ZOOM_BEST_FIT
        + " best fit, "
        + AffineTransformOp.ZOOM_REAL_SIZE
        + " real size";
  }

  public static String helpMouse() {
    return "mouseLeftAction includes "
        + MouseActions.DRAW
        + " (prefs may say "
        + MouseActions.DRAWINGS
        + ")";
  }
}
