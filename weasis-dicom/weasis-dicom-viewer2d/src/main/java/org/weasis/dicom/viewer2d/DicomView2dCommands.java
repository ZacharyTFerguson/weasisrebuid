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
import org.weasis.core.ui.editor.image.MouseActions;
import org.weasis.core.ui.editor.image.SynchView;

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
    View2d view = requireView();
    List<String> a = tokens(args);
    int n = indexOfFlag(a, "-n");
    int i = indexOfFlag(a, "-i");
    Object host = view.getClientProperty(View2dContainer.class);
    if (n >= 0 && n + 1 < a.size() && host instanceof View2dContainer container) {
      container.setLayoutCount(Integer.parseInt(a.get(n + 1)));
    }
    if (i >= 0 && i + 1 < a.size() && host instanceof View2dContainer container) {
      container.setLayoutIndex(Integer.parseInt(a.get(i + 1)));
      return "layout -i " + container.getLayoutIndex();
    }
    if (n >= 0 && n + 1 < a.size()) {
      return "layout -n " + a.get(n + 1);
    }
    return "layout";
  }

  public String mouseLeftAction(String... args) {
    View2d view = requireView();
    List<String> a = tokens(args);
    if (a.isEmpty()) {
      return view.getMouseActions().getLeft();
    }
    view.getMouseActions().setLeft(a.getFirst());
    return view.getMouseActions().getLeft();
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
      if (flag.equals(a.get(i)) || a.get(i).startsWith(flag + "=")) {
        return i;
      }
    }
    return -1;
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
