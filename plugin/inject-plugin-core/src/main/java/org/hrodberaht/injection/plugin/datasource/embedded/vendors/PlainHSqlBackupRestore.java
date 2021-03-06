/*
 * Copyright (c) 2017 org.hrodberaht
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.hrodberaht.injection.plugin.datasource.embedded.vendors;

import org.hrodberaht.injection.plugin.datasource.DataSourceException;
import org.hrodberaht.injection.plugin.datasource.jdbc.JDBCService;
import org.hrodberaht.injection.plugin.datasource.jdbc.JDBCServiceFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class PlainHSqlBackupRestore implements DatasourceBackupRestore {


    private static final Logger LOG = LoggerFactory.getLogger(PlainHSqlBackupRestore.class);
    private static final String JDBC_BASEURL_NEW = "jdbc:hsqldb:mem:";
    private final HsqlBDDataSourceConfigurationRestorable.HSQLDriverManager driverManager;

    public PlainHSqlBackupRestore(HsqlBDDataSourceConfigurationRestorable.HSQLDriverManager driverManager) {
        this.driverManager = driverManager;
    }

    @Override
    public TestDataSourceWrapper getTestDataSource(String name) {
        return new TestDataSourceWrapper(name, driverManager);
    }

    @Override
    public void createSnapshot(final String name) {

        final String fileName = getFilename(name);

        LOG.info("PlainHSqlBackupRestore backup to : {}", fileName);

        PlainConnection connection = null;
        try {
            connection = driverManager.getInnerConnection();
            JDBCService jdbcTemplate = createJdbcService(connection);
            String backup = "SCRIPT '" + fileName + "'";
            jdbcTemplate.execute(backup);
            connection.commit();
        } catch (SQLException e) {
            throw new DataSourceException(e);
        } finally {
            if (connection != null) {
                connection.dontFailClose();
            }
        }
    }

    private JDBCService createJdbcService(Connection connection) {
        return JDBCServiceFactory.of(new DataSourceWrapper(connection));
    }

    private String getFilename(String name) {
        String customBackup = System.getProperty("hrodberaht.junit.sql.backupFile");
        if (customBackup != null) {
            name = customBackup;
        }
        return name;
    }

    @Override
    public void loadSnapshot(String name) {

        final String fileName = getFilename(name);
        LOG.debug("PlainHSqlBackupRestore restore from : {}", fileName);

        try (PlainConnection connection = driverManager.getInnerConnection()) {

            JDBCService jdbcTemplate = createJdbcService(connection);
            readFile(new File(fileName), jdbcTemplate);
            connection.commit();
            connection.closeIt();
        } catch (IOException | SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String jdbcUrl() {
        return JDBC_BASEURL_NEW;
    }

    private String readFile(File file, JDBCService jdbcTemplate) throws IOException {
        StringBuilder fileContents = new StringBuilder((int) file.length());
        Scanner scanner = new Scanner(file);
        Map<String, String> restoreRetryMap = new HashMap<>();
        try {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                if (shouldExecuteLine(line)) {
                    executeLine(jdbcTemplate, line, restoreRetryMap);
                }
            }
            retryRestoreMap(jdbcTemplate, restoreRetryMap);
            return fileContents.toString();
        } finally {
            scanner.close();
        }
    }

    private void retryRestoreMap(final JDBCService jdbcTemplate, final Map<String, String> restoreRetryMap) {
        final Map<String, String> restoreRetryMapAgain = new HashMap<>();
        restoreRetryMap.keySet().forEach(line -> {
            if (shouldExecuteLine(line)) {
                executeLine(jdbcTemplate, line, restoreRetryMapAgain);
            }
        });

        if (restoreRetryMapAgain.size() < restoreRetryMap.size()) {
            retryRestoreMap(jdbcTemplate, restoreRetryMapAgain);
        } else {
            restoreRetryMap.keySet().forEach(line ->
                    LOG.warn("Last - Failed to restore line - " + line)
            );
        }

    }

    private void executeLine(JDBCService jdbcTemplate, String line, Map<String, String> restoreRetryMap) {
        try {
            jdbcTemplate.execute(line);
        } catch (Exception e) {
            LOG.debug("Failed to restore line - " + line);
            restoreRetryMap.put(line, "failed");
        }
    }

    private boolean shouldExecuteLine(String line) {
        return !
                ("CREATE SCHEMA PUBLIC AUTHORIZATION DBA".equals(line)
                        || line.contains("CREATE USER SA PASSWORD DIGEST")
                        || line.contains("GRANT DBA TO SA")
                        || line.equals("SET SCHEMA SYSTEM_LOBS")
                        || line.contains("INSERT INTO BLOCKS VALUES")
                );
    }
}
