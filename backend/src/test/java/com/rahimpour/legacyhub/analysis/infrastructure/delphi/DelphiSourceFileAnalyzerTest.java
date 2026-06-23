package com.rahimpour.legacyhub.analysis.infrastructure.delphi;

import com.rahimpour.legacyhub.analysis.domain.DetectedSymbol;
import com.rahimpour.legacyhub.sourcefile.domain.SourceFile;
import com.rahimpour.legacyhub.sourcefile.domain.SourceFileLanguage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

import static com.rahimpour.legacyhub.codesymbol.domain.CodeSymbolType.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class DelphiSourceFileAnalyzerTest {

    @Test
    void shouldSupportDelphiLanguagesOnly() {
        DelphiSourceFileAnalyzer analyzer = new DelphiSourceFileAnalyzer();

        assertThat(analyzer.supports(SourceFileLanguage.DELPHI)).isTrue();
        assertThat(analyzer.supports(SourceFileLanguage.DELPHI_FORM)).isTrue();
        assertThat(analyzer.supports(SourceFileLanguage.DELPHI_PROJECT)).isTrue();

        assertThat(analyzer.supports(SourceFileLanguage.JAVA)).isFalse();
        assertThat(analyzer.supports(SourceFileLanguage.SQL)).isFalse();
        assertThat(analyzer.supports(SourceFileLanguage.CSHARP)).isFalse();
    }

    @Test
    void shouldDetectPasUnitClassProceduresAndFunctions() {
        // Arrange
        DelphiSourceFileAnalyzer analyzer = new DelphiSourceFileAnalyzer();
        SourceFile sourceFile = sourceFileWithLanguage(SourceFileLanguage.DELPHI);

        String content = """
                unit CustomerUnit;

                interface

                type
                  TCustomerService = class
                  public
                    procedure SaveCustomer;
                    function FindCustomer(Id: Integer): String;
                  end;

                implementation

                procedure TCustomerService.SaveCustomer;
                begin
                end;

                function TCustomerService.FindCustomer(Id: Integer): String;
                begin
                  Result := '';
                end;

                end.
                """;

        // Act
        List<DetectedSymbol> symbols = analyzer.analyze(sourceFile, content);

        // Assert
        assertThat(symbols)
                .extracting("type", "name")
                .containsExactlyInAnyOrder(
                        tuple(DELPHI_UNIT, "CustomerUnit"),
                        tuple(CLASS, "TCustomerService"),
                        tuple(PROCEDURE, "SaveCustomer"),
                        tuple(FUNCTION, "FindCustomer"),
                        tuple(PROCEDURE, "TCustomerService.SaveCustomer"),
                        tuple(FUNCTION, "TCustomerService.FindCustomer")
                );
    }

    @Test
    void shouldDetectPasSymbolsCaseInsensitively() {
        // Arrange
        DelphiSourceFileAnalyzer analyzer = new DelphiSourceFileAnalyzer();
        SourceFile sourceFile = sourceFileWithLanguage(SourceFileLanguage.DELPHI);

        String content = """
                UNIT LegacyUnit;

                TYPE
                  TLegacyService = CLASS
                  PUBLIC
                    PROCEDURE ProcessData;
                    FUNCTION CalculateScore(Value: Integer): Integer;
                  END;
                """;

        // Act
        List<DetectedSymbol> symbols = analyzer.analyze(sourceFile, content);

        // Assert
        assertThat(symbols)
                .extracting("type", "name")
                .containsExactlyInAnyOrder(
                        tuple(DELPHI_UNIT, "LegacyUnit"),
                        tuple(CLASS, "TLegacyService"),
                        tuple(PROCEDURE, "ProcessData"),
                        tuple(FUNCTION, "CalculateScore")
                );
    }

    @Test
    void shouldReturnEmptyListForPasWithoutRecognizedSymbols() {
        // Arrange
        DelphiSourceFileAnalyzer analyzer = new DelphiSourceFileAnalyzer();
        SourceFile sourceFile = sourceFileWithLanguage(SourceFileLanguage.DELPHI);

        String content = """
                implementation

                begin
                  WriteLn('Hello');
                end.
                """;

        // Act
        List<DetectedSymbol> symbols = analyzer.analyze(sourceFile, content);

        // Assert
        assertThat(symbols).isEmpty();
    }

    @Test
    void shouldDetectDfmFormComponentsAndEventHandlers() {
        // Arrange
        DelphiSourceFileAnalyzer analyzer = new DelphiSourceFileAnalyzer();
        SourceFile sourceFile = sourceFileWithLanguage(SourceFileLanguage.DELPHI_FORM);

        String content = """
                object CustomerForm: TCustomerForm
                  Caption = 'Customer'

                  object SaveButton: TButton
                    Caption = 'Save'
                    OnClick = SaveButtonClick
                  end

                  object CustomerNameEdit: TEdit
                    OnChange = CustomerNameEditChange
                  end
                end
                """;

        // Act
        List<DetectedSymbol> symbols = analyzer.analyze(sourceFile, content);

        // Assert
        assertThat(symbols)
                .extracting("type", "name")
                .containsExactlyInAnyOrder(
                        tuple(DELPHI_FORM, "CustomerForm"),
                        tuple(DELPHI_COMPONENT, "SaveButton"),
                        tuple(DELPHI_COMPONENT, "CustomerNameEdit"),
                        tuple(EVENT_HANDLER, "SaveButtonClick"),
                        tuple(EVENT_HANDLER, "CustomerNameEditChange")
                );
    }

    @Test
    void shouldUseObjectNameAndTypeAsFullyQualifiedNameForDfmObjects() {
        // Arrange
        DelphiSourceFileAnalyzer analyzer = new DelphiSourceFileAnalyzer();
        SourceFile sourceFile = sourceFileWithLanguage(SourceFileLanguage.DELPHI_FORM);

        String content = """
                object CustomerForm: TCustomerForm
                  object SaveButton: TButton
                  end
                end
                """;

        // Act
        List<DetectedSymbol> symbols = analyzer.analyze(sourceFile, content);

        // Assert
        assertThat(symbols)
                .filteredOn(symbol -> symbol.type() == DELPHI_FORM)
                .singleElement()
                .satisfies(symbol -> {
                    assertThat(symbol.name()).isEqualTo("CustomerForm");
                    assertThat(symbol.fullyQualifiedName()).isEqualTo("CustomerForm:TCustomerForm");
                });

        assertThat(symbols)
                .filteredOn(symbol -> symbol.type() == DELPHI_COMPONENT)
                .singleElement()
                .satisfies(symbol -> {
                    assertThat(symbol.name()).isEqualTo("SaveButton");
                    assertThat(symbol.fullyQualifiedName()).isEqualTo("SaveButton:TButton");
                });
    }

    @ParameterizedTest
    @MethodSource("delphiProjectCases")
    void shouldDetectDprProjectName(String filename, String expectedProjectName) {
        // Arrange
        DelphiSourceFileAnalyzer analyzer = new DelphiSourceFileAnalyzer();
        SourceFile sourceFile = mock(SourceFile.class);

        when(sourceFile.getLanguage()).thenReturn(SourceFileLanguage.DELPHI_PROJECT);
        when(sourceFile.getFilename()).thenReturn(filename);

        // Act
        List<DetectedSymbol> symbols = analyzer.analyze(sourceFile, "program ignored;");

        // Assert
        assertThat(symbols)
                .extracting("type", "name", "fullyQualifiedName")
                .containsExactly(
                        tuple(DELPHI_UNIT, expectedProjectName, expectedProjectName)
                );
    }

    private static Stream<org.junit.jupiter.params.provider.Arguments> delphiProjectCases() {
        return Stream.of(
                arguments("LegacyApp.dpr", "LegacyApp"),
                arguments("CustomerManagement.dpr", "CustomerManagement"),
                arguments("NoExtension", "NoExtension"),
                arguments("", "UnknownDelphiProject"),
                arguments(null, "UnknownDelphiProject")
        );
    }

    private SourceFile sourceFileWithLanguage(SourceFileLanguage language) {
        SourceFile sourceFile = mock(SourceFile.class);
        when(sourceFile.getLanguage()).thenReturn(language);
        return sourceFile;
    }
}