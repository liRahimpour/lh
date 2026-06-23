package com.rahimpour.legacyhub.analysis.infrastructure.sql;

import com.rahimpour.legacyhub.analysis.domain.DetectedSymbol;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.rahimpour.legacyhub.codesymbol.domain.CodeSymbolType.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;

class SqlSourceFileAnalyzerTest {

    @Test
    void shouldDetectSqlSymbols() {
        SqlSourceFileAnalyzer sqlSourceFileAnalyzer = new SqlSourceFileAnalyzer();

        String content = """
                CREATE TABLE customer (
                    id BIGINT
                );
                
                CREATE VIEW active_customers AS
                SELECT * FROM customer;
                
                CREATE PROCEDURE update_customer
                AS
                SELECT 1;
                
                CREATE FUNCTION calculate_score()
                RETURNS INT
                AS
                SELECT 1;
                """;

        List<DetectedSymbol> detectedSymbols = sqlSourceFileAnalyzer.analyze(null, content);

        assertThat(detectedSymbols).extracting("type", "name")
                .containsExactlyInAnyOrder(
                        tuple(SQL_TABLE, "customer"),
                        tuple(SQL_VIEW, "active_customers"),
                        tuple(SQL_PROCEDURE, "update_customer"),
                        tuple(SQL_FUNCTION, "calculate_score")
                );
    }

    @Test
    void shouldNotDetectSymbolsForSqlWithoutCreateStatements() {
        // Arrange
        SqlSourceFileAnalyzer analyzer = new SqlSourceFileAnalyzer();

        String content = """
            SELECT * FROM customer;

            INSERT INTO customer(id)
            VALUES (1);

            UPDATE customer
            SET id = 2;
            """;

        // Act
        List<DetectedSymbol> symbols = analyzer.analyze(null, content);

        // Assert
        assertThat(symbols).isEmpty();
    }

}


