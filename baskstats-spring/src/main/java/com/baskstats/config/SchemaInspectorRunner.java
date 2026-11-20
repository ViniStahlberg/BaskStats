package com.baskstats.config;

import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.simple.JdbcClient;

@Configuration
@ConditionalOnProperty(
    name = "baskstats.schema.inspect",
    havingValue = "true",
    matchIfMissing = false)
public class SchemaInspectorRunner {

  @Bean
  ApplicationRunner inspectSchema(JdbcClient db) {
    return args -> {
      System.out.println("=== SEARCH PATH ===");
      db.sql("SHOW search_path").query((rs, rowNum) -> rs.getString(1)).list()
          .forEach(System.out::println);

      System.out.println("=== TABELAS E LINHAS ===");
      db.sql("""
          SELECT schemaname, relname, n_live_tup
          FROM pg_stat_user_tables
          ORDER BY schemaname, relname
          """)
          .query((rs, rowNum) ->
              rs.getString("schemaname") + "." + rs.getString("relname")
                  + " (~" + rs.getLong("n_live_tup") + " linhas)")
          .list()
          .forEach(System.out::println);

      System.out.println("=== COLUNAS ===");
      db.sql("""
          SELECT table_schema, table_name, column_name, data_type
          FROM information_schema.columns
          WHERE table_schema NOT IN ('information_schema', 'pg_catalog')
          ORDER BY table_schema, table_name, ordinal_position
          """)
          .query((rs, rowNum) ->
              rs.getString("table_schema") + "." + rs.getString("table_name")
                  + " -> " + rs.getString("column_name") + " (" + rs.getString("data_type") + ")")
          .list()
          .forEach(System.out::println);
    };
  }
}