package com.rahimpour.legacyhub.analysis.infrastructure.delphi;

import com.rahimpour.legacyhub.analysis.domain.DetectedSymbol;
import com.rahimpour.legacyhub.analysis.domain.SourceFileAnalyzer;
import com.rahimpour.legacyhub.codesymbol.domain.CodeSymbolType;
import com.rahimpour.legacyhub.sourcefile.domain.SourceFile;
import com.rahimpour.legacyhub.sourcefile.domain.SourceFileLanguage;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class DelphiSourceFileAnalyzer implements SourceFileAnalyzer {

    private static final Pattern UNIT_PATTERN = Pattern.compile(
            "^\\s*unit\\s+([A-Za-z_][A-Za-z0-9_]*)\\s*;",
            Pattern.CASE_INSENSITIVE | Pattern.MULTILINE
    );

    private static final Pattern CLASS_PATTERN = Pattern.compile(
            "^\\s*([A-Za-z_][A-Za-z0-9_]*)\\s*=\\s*class\\b",
            Pattern.CASE_INSENSITIVE | Pattern.MULTILINE
    );

    private static final Pattern PROCEDURE_PATTERN = Pattern.compile(
            "^\\s*procedure\\s+([A-Za-z_][A-Za-z0-9_\\.]*)(?:\\s*\\([^;]*\\))?\\s*;",
            Pattern.CASE_INSENSITIVE | Pattern.MULTILINE
    );

    private static final Pattern FUNCTION_PATTERN = Pattern.compile(
            "^\\s*function\\s+([A-Za-z_][A-Za-z0-9_\\.]*)(?:\\s*\\([^;]*\\))?\\s*:\\s*([A-Za-z_][A-Za-z0-9_\\.]*)\\s*;",
            Pattern.CASE_INSENSITIVE | Pattern.MULTILINE
    );

    private static final Pattern DFM_OBJECT_PATTERN = Pattern.compile(
            "^\\s*object\\s+([A-Za-z_][A-Za-z0-9_]*)\\s*:\\s*([A-Za-z_][A-Za-z0-9_]*)",
            Pattern.CASE_INSENSITIVE | Pattern.MULTILINE
    );

    private static final Pattern DFM_EVENT_PATTERN = Pattern.compile(
            "^\\s*(On[A-Za-z0-9_]+)\\s*=\\s*([A-Za-z_][A-Za-z0-9_]*)",
            Pattern.CASE_INSENSITIVE | Pattern.MULTILINE
    );

    @Override
    public boolean supports(SourceFileLanguage language) {
        return language == SourceFileLanguage.DELPHI
                || language == SourceFileLanguage.DELPHI_FORM
                || language == SourceFileLanguage.DELPHI_PROJECT;
    }

    @Override
    public List<DetectedSymbol> analyze(SourceFile sourceFile, String content) {
        if (sourceFile.getLanguage() == SourceFileLanguage.DELPHI_FORM) {
            return analyzeDfm(content);
        }

        if (sourceFile.getLanguage() == SourceFileLanguage.DELPHI_PROJECT) {
            return analyzeDpr(sourceFile);
        }

        return analyzePas(content);
    }

    private List<DetectedSymbol> analyzePas(String content) {
        List<DetectedSymbol> symbols = new ArrayList<>();

        findSingle(content, UNIT_PATTERN, CodeSymbolType.DELPHI_UNIT, symbols);
        findAll(content, CLASS_PATTERN, CodeSymbolType.CLASS, symbols);
        findAll(content, PROCEDURE_PATTERN, CodeSymbolType.PROCEDURE, symbols);
        findAll(content, FUNCTION_PATTERN, CodeSymbolType.FUNCTION, symbols);

        return symbols;
    }

    private List<DetectedSymbol> analyzeDfm(String content) {
        List<DetectedSymbol> symbols = new ArrayList<>();

        Matcher objectMatcher = DFM_OBJECT_PATTERN.matcher(content);
        boolean firstObject = true;

        while (objectMatcher.find()) {
            String objectName = objectMatcher.group(1);
            String objectType = objectMatcher.group(2);
            int line = calculateLineNumber(content, objectMatcher.start());

            CodeSymbolType symbolType = firstObject
                    ? CodeSymbolType.DELPHI_FORM
                    : CodeSymbolType.DELPHI_COMPONENT;

            symbols.add(new DetectedSymbol(
                    symbolType,
                    objectName,
                    objectName + ":" + objectType,
                    line,
                    null
            ));

            firstObject = false;
        }

        Matcher eventMatcher = DFM_EVENT_PATTERN.matcher(content);

        while (eventMatcher.find()) {
            String eventName = eventMatcher.group(1);
            String handlerName = eventMatcher.group(2);
            int line = calculateLineNumber(content, eventMatcher.start());

            symbols.add(new DetectedSymbol(
                    CodeSymbolType.EVENT_HANDLER,
                    handlerName,
                    eventName + ":" + handlerName,
                    line,
                    null
            ));
        }

        return symbols;
    }

    private List<DetectedSymbol> analyzeDpr(SourceFile sourceFile) {
        List<DetectedSymbol> symbols = new ArrayList<>();

        String projectName = removeExtension(sourceFile.getFilename());

        symbols.add(new DetectedSymbol(
                CodeSymbolType.DELPHI_UNIT,
                projectName,
                projectName,
                1,
                null
        ));

        return symbols;
    }

    private void findSingle(
            String content,
            Pattern pattern,
            CodeSymbolType type,
            List<DetectedSymbol> symbols
    ) {
        Matcher matcher = pattern.matcher(content);

        if (matcher.find()) {
            addSymbol(content, matcher, type, symbols);
        }
    }

    private void findAll(
            String content,
            Pattern pattern,
            CodeSymbolType type,
            List<DetectedSymbol> symbols
    ) {
        Matcher matcher = pattern.matcher(content);

        while (matcher.find()) {
            addSymbol(content, matcher, type, symbols);
        }
    }

    private void addSymbol(
            String content,
            Matcher matcher,
            CodeSymbolType type,
            List<DetectedSymbol> symbols
    ) {
        String name = matcher.group(1);
        int line = calculateLineNumber(content, matcher.start());

        symbols.add(new DetectedSymbol(
                type,
                name,
                name,
                line,
                null
        ));
    }

    private int calculateLineNumber(String content, int index) {
        int line = 1;

        for (int i = 0; i < index && i < content.length(); i++) {
            if (content.charAt(i) == '\n') {
                line++;
            }
        }

        return line;
    }

    private String removeExtension(String filename) {
        if (filename == null || filename.isBlank()) {
            return "UnknownDelphiProject";
        }

        int dotIndex = filename.lastIndexOf('.');

        if (dotIndex <= 0) {
            return filename;
        }

        return filename.substring(0, dotIndex);
    }
}
