/*
 * SonarQube
 * Copyright (C) 2009-2023 SonarSource SA
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
package org.sonar.server.platform.db.migration.version.v101;

import com.google.common.annotations.VisibleForTesting;
import java.sql.Connection;
import java.sql.SQLException;
import org.sonar.db.Database;
import org.sonar.db.DatabaseUtils;
import org.sonar.server.platform.db.migration.sql.CreateIndexBuilder;
import org.sonar.server.platform.db.migration.step.DdlChange;

import static org.sonar.server.platform.db.migration.def.VarcharColumnDef.UUID_SIZE;
import static org.sonar.server.platform.db.migration.def.VarcharColumnDef.newVarcharColumnDefBuilder;
import static org.sonar.server.platform.db.migration.version.v101.AddReportSchedulesTable.TABLE_NAME;
import static org.sonar.server.platform.db.migration.version.v101.CreateExternalGroupsTable.EXTERNAL_GROUP_ID_COLUMN_NAME;

public class CreateUniqueIndexForReportSchedulesTable extends DdlChange {

  @VisibleForTesting
  static final String COLUMN_NAME_PORTFOLIO = "portfolio_uuid";
  @VisibleForTesting
  static final String COLUMN_NAME_BRANCH = "branch_uuid";

  @VisibleForTesting
  static final String INDEX_NAME = "uniq_report_schedules";


  public CreateUniqueIndexForReportSchedulesTable(Database db) {
    super(db);
  }

  @Override
  public void execute(Context context) throws SQLException {

    try (Connection connection = getDatabase().getDataSource().getConnection()) {
      createUserUuidUniqueIndex(context, connection);
    }
  }

  private void createUserUuidUniqueIndex(Context context, Connection connection) {
    if (!DatabaseUtils.indexExistsIgnoreCase(TABLE_NAME, INDEX_NAME, connection)) {
      context.execute(new CreateIndexBuilder(getDialect())
        .setTable(TABLE_NAME)
        .setName(INDEX_NAME)
        .addColumn(newVarcharColumnDefBuilder().setColumnName(COLUMN_NAME_PORTFOLIO).setLimit(UUID_SIZE).build())
        .addColumn(newVarcharColumnDefBuilder().setColumnName(COLUMN_NAME_BRANCH).setLimit(UUID_SIZE).build())
        .setUnique(true)
        .build());
    }
  }
}
