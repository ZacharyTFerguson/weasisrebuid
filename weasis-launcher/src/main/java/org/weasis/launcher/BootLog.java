/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.launcher;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.ConsoleAppender;
import ch.qos.logback.core.FileAppender;
import java.nio.file.Files;
import java.nio.file.Path;
import org.slf4j.LoggerFactory;

/** Always writes {@code ~/.weasis/log/boot.log}. */
public final class BootLog {

  private BootLog() {}

  public static void install(Path logDir) {
    try {
      Files.createDirectories(logDir);
      LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
      PatternLayoutEncoder encoder = new PatternLayoutEncoder();
      encoder.setContext(context);
      encoder.setPattern("%d{dd.MM.yyyy HH:mm:ss.SSS} *%-5level* [%thread] %logger{36}: %msg%n");
      encoder.start();

      FileAppender<ILoggingEvent> file = new FileAppender<>();
      file.setContext(context);
      file.setName("BOOT_FILE");
      file.setFile(logDir.resolve("boot.log").toString());
      file.setEncoder(encoder);
      file.start();

      ConsoleAppender<ILoggingEvent> console = new ConsoleAppender<>();
      console.setContext(context);
      console.setName("BOOT_CONSOLE");
      PatternLayoutEncoder consoleEncoder = new PatternLayoutEncoder();
      consoleEncoder.setContext(context);
      consoleEncoder.setPattern("%d{HH:mm:ss.SSS} %-5level %logger{36} - %msg%n");
      consoleEncoder.start();
      console.setEncoder(consoleEncoder);
      console.start();

      Logger root = context.getLogger(Logger.ROOT_LOGGER_NAME);
      root.detachAndStopAllAppenders();
      root.addAppender(file);
      root.addAppender(console);
      root.setLevel(Level.INFO);
    } catch (Exception e) {
      System.err.println("Cannot install boot.log: " + e.getMessage());
    }
  }
}
