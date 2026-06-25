package com.rahimpour.legacyhub.analysis.relation.infrastructure.java;

import com.rahimpour.legacyhub.analysis.relation.domain.DetectedRelation;
import com.rahimpour.legacyhub.codesymbol.domain.CodeSymbol;
import com.rahimpour.legacyhub.codesymbol.domain.CodeSymbolType;
import com.rahimpour.legacyhub.sourcefile.domain.SourceFileLanguage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

import static com.rahimpour.legacyhub.coderelation.domain.CodeRelationType.HAS_FIELD;
import static com.rahimpour.legacyhub.coderelation.domain.CodeRelationType.HAS_METHOD;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;

class JavaSourceFileRelationAnalyzerTest {

//    @Test
//    void shouldSupportJavaOnly() {
//        JavaSourceFileRelationAnalyzer analyzer = new JavaSourceFileRelationAnalyzer();
//
//        assertThat(analyzer.supports(SourceFileLanguage.JAVA)).isTrue();
//
//        assertThat(analyzer.supports(SourceFileLanguage.SQL)).isFalse();
//        assertThat(analyzer.supports(SourceFileLanguage.CSHARP)).isFalse();
//        assertThat(analyzer.supports(SourceFileLanguage.DELPHI)).isFalse();
//        assertThat(analyzer.supports(SourceFileLanguage.DELPHI_FORM)).isFalse();
//        assertThat(analyzer.supports(SourceFileLanguage.DELPHI_PROJECT)).isFalse();
//    }
//
//    @Test
//    void shouldCreateRelationsForFieldsAndMethodsOfClass() {
//        // Arrange
//        JavaSourceFileRelationAnalyzer analyzer = new JavaSourceFileRelationAnalyzer();
//
//        CodeSymbol customerService = symbol(
//                CodeSymbolType.CLASS,
//                "CustomerService",
//                3
//        );
//
//        CodeSymbol repository = symbol(
//                CodeSymbolType.FIELD,
//                "repository",
//                5
//        );
//
//        CodeSymbol saveCustomer = symbol(
//                CodeSymbolType.METHOD,
//                "saveCustomer",
//                7
//        );
//
//        CodeSymbol deleteCustomer = symbol(
//                CodeSymbolType.METHOD,
//                "deleteCustomer",
//                11
//        );
//
//        // Act
//        List<DetectedRelation> relations = analyzer.analyze(
//                List.of(customerService, repository, saveCustomer, deleteCustomer)
//        );
//
//        // Assert
//        assertThat(relations).containsExactlyInAnyOrder(
//                new DetectedRelation(
//                        customerService.getId(),
//                        repository.getId(),
//                        HAS_FIELD
//                ),
//                new DetectedRelation(
//                        customerService.getId(),
//                        saveCustomer.getId(),
//                        HAS_METHOD
//                ),
//                new DetectedRelation(
//                        customerService.getId(),
//                        deleteCustomer.getId(),
//                        HAS_METHOD
//                )
//        );
//    }
//
//    @Test
//    void shouldUseEarliestContainerAsMainContainer() {
//        // Arrange
//        JavaSourceFileRelationAnalyzer analyzer = new JavaSourceFileRelationAnalyzer();
//
//        CodeSymbol firstClass = symbol(
//                CodeSymbolType.CLASS,
//                "CustomerService",
//                3
//        );
//
//        CodeSymbol secondClass = symbol(
//                CodeSymbolType.CLASS,
//                "CustomerValidator",
//                20
//        );
//
//        CodeSymbol validateCustomer = symbol(
//                CodeSymbolType.METHOD,
//                "validateCustomer",
//                22
//        );
//
//        // Act
//        List<DetectedRelation> relations = analyzer.analyze(
//                List.of(secondClass, validateCustomer, firstClass)
//        );
//
//        // Assert
//        assertThat(relations).containsExactly(
//                new DetectedRelation(
//                        firstClass.getId(),
//                        validateCustomer.getId(),
//                        HAS_METHOD
//                )
//        );
//    }
//
//    @ParameterizedTest
//    @MethodSource("containerTypes")
//    void shouldUseSupportedJavaTypeAsContainer(CodeSymbolType containerType) {
//        // Arrange
//        JavaSourceFileRelationAnalyzer analyzer = new JavaSourceFileRelationAnalyzer();
//
//        CodeSymbol container = symbol(
//                containerType,
//                "Container",
//                1
//        );
//
//        CodeSymbol method = symbol(
//                CodeSymbolType.METHOD,
//                "execute",
//                5
//        );
//
//        CodeSymbol field = symbol(
//                CodeSymbolType.FIELD,
//                "repository",
//                3
//        );
//
//        // Act
//        List<DetectedRelation> relations = analyzer.analyze(
//                List.of(container, field, method)
//        );
//
//        // Assert
//        assertThat(relations).containsExactlyInAnyOrder(
//                new DetectedRelation(
//                        container.getId(),
//                        field.getId(),
//                        HAS_FIELD
//                ),
//                new DetectedRelation(
//                        container.getId(),
//                        method.getId(),
//                        HAS_METHOD
//                )
//        );
//    }
//
//    private static Stream<org.junit.jupiter.params.provider.Arguments> containerTypes() {
//        return Stream.of(
//                arguments(CodeSymbolType.CLASS),
//                arguments(CodeSymbolType.INTERFACE),
//                arguments(CodeSymbolType.RECORD),
//                arguments(CodeSymbolType.ENUM)
//        );
//    }
//
//    @Test
//    void shouldReturnEmptyListWhenNoContainerExists() {
//        // Arrange
//        JavaSourceFileRelationAnalyzer analyzer = new JavaSourceFileRelationAnalyzer();
//
//        CodeSymbol namespace = symbol(
//                CodeSymbolType.NAMESPACE,
//                "com.example.customer",
//                1
//        );
//
//        CodeSymbol repository = symbol(
//                CodeSymbolType.FIELD,
//                "repository",
//                3
//        );
//
//        CodeSymbol saveCustomer = symbol(
//                CodeSymbolType.METHOD,
//                "saveCustomer",
//                5
//        );
//
//        // Act
//        List<DetectedRelation> relations = analyzer.analyze(
//                List.of(namespace, repository, saveCustomer)
//        );
//
//        // Assert
//        assertThat(relations).isEmpty();
//    }
//
//    @Test
//    void shouldIgnoreSymbolsThatAreNotFieldsOrMethods() {
//        // Arrange
//        JavaSourceFileRelationAnalyzer analyzer = new JavaSourceFileRelationAnalyzer();
//
//        CodeSymbol customerService = symbol(
//                CodeSymbolType.CLASS,
//                "CustomerService",
//                3
//        );
//
//        CodeSymbol namespace = symbol(
//                CodeSymbolType.NAMESPACE,
//                "com.example.customer",
//                1
//        );
//
//        CodeSymbol status = symbol(
//                CodeSymbolType.ENUM,
//                "Status",
//                20
//        );
//
//        CodeSymbol customerDto = symbol(
//                CodeSymbolType.RECORD,
//                "CustomerDto",
//                30
//        );
//
//        // Act
//        List<DetectedRelation> relations = analyzer.analyze(
//                List.of(customerService, namespace, status, customerDto)
//        );
//
//        // Assert
//        assertThat(relations).isEmpty();
//    }
//
//    @Test
//    void shouldNotCreateRelationFromContainerToItself() {
//        // Arrange
//        JavaSourceFileRelationAnalyzer analyzer = new JavaSourceFileRelationAnalyzer();
//
//        CodeSymbol customerService = symbol(
//                CodeSymbolType.CLASS,
//                "CustomerService",
//                3
//        );
//
//        // Act
//        List<DetectedRelation> relations = analyzer.analyze(
//                List.of(customerService)
//        );
//
//        // Assert
//        assertThat(relations).isEmpty();
//    }
//
//    @Test
//    void shouldHandleContainerWithoutStartLine() {
//        // Arrange
//        JavaSourceFileRelationAnalyzer analyzer = new JavaSourceFileRelationAnalyzer();
//
//        CodeSymbol customerServiceWithoutLine = symbol(
//                CodeSymbolType.CLASS,
//                "CustomerService",
//                null
//        );
//
//        CodeSymbol customerValidator = symbol(
//                CodeSymbolType.CLASS,
//                "CustomerValidator",
//                10
//        );
//
//        CodeSymbol validateCustomer = symbol(
//                CodeSymbolType.METHOD,
//                "validateCustomer",
//                12
//        );
//
//        // Act
//        List<DetectedRelation> relations = analyzer.analyze(
//                List.of(customerServiceWithoutLine, customerValidator, validateCustomer)
//        );
//
//        // Assert
//        assertThat(relations).containsExactly(
//                new DetectedRelation(
//                        customerValidator.getId(),
//                        validateCustomer.getId(),
//                        HAS_METHOD
//                )
//        );
//    }
//
//    private CodeSymbol symbol(
//            CodeSymbolType type,
//            String name,
//            Integer startLine
//    ) {
//        UUID projectId = UUID.randomUUID();
//        UUID sourceFileId = UUID.randomUUID();
//
//        return CodeSymbol.create(
//                projectId,
//                sourceFileId,
//                type,
//                name,
//                "com.example." + name,
//                startLine,
//                null
//        );
//    }
}