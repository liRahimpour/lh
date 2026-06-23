package com.rahimpour.legacyhub.analysis.infrastructure.java;

import com.rahimpour.legacyhub.analysis.domain.DetectedSymbol;
import com.rahimpour.legacyhub.codesymbol.domain.CodeSymbolType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

import static com.rahimpour.legacyhub.codesymbol.domain.CodeSymbolType.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;
import static org.junit.jupiter.params.provider.Arguments.arguments;

class JavaSourceFileAnalyzerTest {

    @Test
    void shouldDetectPackageClassFieldAndMethod() {
        // Arrange
        JavaSourceFileAnalyzer analyzer = new JavaSourceFileAnalyzer();

        String content = """
                package com.example.customer;

                public class CustomerService {

                    private String customerName;

                    public void saveCustomer() {
                    }
                }
                """;

        // Act
        List<DetectedSymbol> symbols = analyzer.analyze(null, content);

        // Assert
        assertThat(symbols)
                .extracting("type", "name")
                .containsExactlyInAnyOrder(
                        tuple(NAMESPACE, "com.example.customer"),
                        tuple(CLASS, "CustomerService"),
                        tuple(FIELD, "customerName"),
                        tuple(METHOD, "saveCustomer")
                );
    }

    @Test
    void shouldDetectInterfaceAndMethodDeclarations() {
        // Arrange
        JavaSourceFileAnalyzer analyzer = new JavaSourceFileAnalyzer();

        String content = """
            package com.example.customer;

            public interface CustomerRepository {

                Customer findById(Long id);

                void deleteById(Long id);
            }
            """;

        // Act
        List<DetectedSymbol> symbols = analyzer.analyze(null, content);

        // Assert
        assertThat(symbols)
                .extracting("type", "name")
                .containsExactlyInAnyOrder(
                        tuple(NAMESPACE, "com.example.customer"),
                        tuple(INTERFACE, "CustomerRepository"),
                        tuple(METHOD, "findById"),
                        tuple(METHOD, "deleteById")
                );
    }

    @ParameterizedTest
    @MethodSource("javaTypeCases")
    void shouldDetectJavaType(String content, CodeSymbolType expectedType, String expectedName) {
        // Arrange
        JavaSourceFileAnalyzer analyzer = new JavaSourceFileAnalyzer();

        // Act
        List<DetectedSymbol> symbols = analyzer.analyze(null, content);

        // Assert
        assertThat(symbols)
                .extracting("type", "name")
                .containsExactlyInAnyOrder(
                        tuple(expectedType, expectedName)
                );
    }

    private static Stream<Arguments> javaTypeCases() {
        return Stream.of(
                arguments("""
                    public enum Status {
                        ACTIVE,
                        INACTIVE
                    }
                    """, ENUM, "Status"),

                arguments("""
                    public record CustomerDto(String name) {
                    }
                    """, RECORD, "CustomerDto"),

                arguments("""
                    public @interface Audited {
                    }
                    """, ANNOTATION, "Audited")
        );
    }

    @Test
    void shouldNotDetectControlStructuresAsMethods() {
        JavaSourceFileAnalyzer analyzer = new JavaSourceFileAnalyzer();

        String content = """
            public class Example {

                public void execute() {
                    if (true) {
                    }

                    for (int i = 0; i < 10; i++) {
                    }

                    while (false) {
                    }
                }
            }
            """;

        List<DetectedSymbol> symbols = analyzer.analyze(null, content);

        assertThat(symbols)
                .filteredOn(symbol -> symbol.type() == METHOD)
                .extracting("name")
                .containsExactly("execute");
    }

    @Test
    void shouldDetectCorrectLineNumbers() {
        // Arrange
        JavaSourceFileAnalyzer analyzer = new JavaSourceFileAnalyzer();
        // TODO: problem ist dass öleerzeichen zwischen zeiken nicht als eine line gezählt wird.
        String content = """
            package com.example.customer;
            public class CustomerService {
                private String customerName;
                public void saveCustomer() {
                }
            }
            """;

        // Act
        List<DetectedSymbol> symbols = analyzer.analyze(null, content);

        // Assert
        assertThat(symbols)
                .filteredOn(symbol -> symbol.type() == CLASS)
                .singleElement()
                .satisfies(symbol -> {
                    assertThat(symbol.name()).isEqualTo("CustomerService");
                    assertThat(symbol.startLine()).isEqualTo(2);
                });

        assertThat(symbols)
                .filteredOn(symbol -> symbol.type() == FIELD)
                .singleElement()
                .satisfies(symbol -> {
                    assertThat(symbol.name()).isEqualTo("customerName");
                    assertThat(symbol.startLine()).isEqualTo(3);
                });

        assertThat(symbols)
                .filteredOn(symbol -> symbol.type() == METHOD)
                .singleElement()
                .satisfies(symbol -> {
                    assertThat(symbol.name()).isEqualTo("saveCustomer");
                    assertThat(symbol.startLine()).isEqualTo(4);
                });
    }
}