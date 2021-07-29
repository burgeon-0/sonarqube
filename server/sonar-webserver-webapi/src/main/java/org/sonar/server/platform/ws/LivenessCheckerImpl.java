/*
 * SonarQube
 * Copyright (C) 2009-2021 SonarSource SA
 * mailto:info AT sonarsource DOT com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package org.sonar.server.platform.ws;

import org.sonar.server.health.CeStatusNodeCheck;
import org.sonar.server.health.DbConnectionNodeCheck;
import org.sonar.server.health.EsStatusNodeCheck;
import org.sonar.server.health.Health;
import org.sonar.server.health.WebServerStatusNodeCheck;
import org.sonar.server.platform.WebServer;

public class LivenessCheckerImpl implements LivenessChecker {

  private final WebServer webServer;
  private final DbConnectionNodeCheck dbConnectionNodeCheck;
  private final CeStatusNodeCheck ceStatusNodeCheck;
  private final EsStatusNodeCheck esStatusNodeCheck;
  private final WebServerStatusNodeCheck webServerStatusNodeCheck;

  public LivenessCheckerImpl(WebServer webServer, DbConnectionNodeCheck dbConnectionNodeCheck,
    WebServerStatusNodeCheck webServerStatusNodeCheck, CeStatusNodeCheck ceStatusNodeCheck, EsStatusNodeCheck esStatusNodeCheck) {
    this.webServer = webServer;
    this.dbConnectionNodeCheck = dbConnectionNodeCheck;
    this.ceStatusNodeCheck = ceStatusNodeCheck;
    this.esStatusNodeCheck = esStatusNodeCheck;
    this.webServerStatusNodeCheck = webServerStatusNodeCheck;
  }

  public boolean liveness() {

    if (!Health.Status.GREEN.equals(dbConnectionNodeCheck.check().getStatus())) {
      return false;
    }

    if (!Health.Status.GREEN.equals(webServerStatusNodeCheck.check().getStatus())) {
      return false;
    }

    if (!Health.Status.GREEN.equals(ceStatusNodeCheck.check().getStatus())) {
      return false;
    }

    if (webServer.isStandalone() && Health.Status.RED.equals(esStatusNodeCheck.check().getStatus())) {
      return false;
    }

    return true;
  }
}