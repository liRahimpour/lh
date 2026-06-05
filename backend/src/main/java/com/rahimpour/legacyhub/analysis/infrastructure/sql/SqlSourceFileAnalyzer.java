package com.rahimpour.legacyhub.analysis.infrastructure.sql;

import com.rahimpour.legacyhub.analysis.domain.DetectedSymbol;
import com.rahimpour.legacyhub.analysis.domain.SourceFileAnalyzer;
import com.rahimpour.legacyhub.codesymbol.domain.CodeSymbolType;
import com.rahimpour.legacyhub.sourcefile.domain.SourceFile;
import com.rahimpour.legacyhub.sourcefile.domain.SourceFileLanguage;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class SqlSourceFileAnalyzer implements SourceFileAnalyzer {

    private static final Pattern CREATE_TABLE_PATTERN = Pattern.compile(
            "\\bCREATE\\s+TABLE\\s+([\\w\\.\\[\\]\"]+)",
            Pattern.CASE_INSENSITIVE
    );

    private static final Pattern CREATE_VIEW_PATTERN = Pattern.compile(
            "\\bCREATE\\s+VIEW\\s+([\\w\\.\\[\\]\"]+)",
            Pattern.CASE_INSENSITIVE
    );

    private static final Pattern CREATE_PROCEDURE_PATTERN = Pattern.compile(
            "\\bCREATE\\s+(?:OR\\s+REPLACE\\s+)?PROCEDURE\\s+([\\w\\.\\[\\]\"]+)",
            Pattern.CASE_INSENSITIVE
    );

    private static final Pattern CREATE_FUNCTION_PATTERN = Pattern.compile(
            "\\bCREATE\\s+(?:OR\\s+REPLACE\\s+)?FUNCTION\\s+([\\w\\.\\[\\]\"]+)",
            Pattern.CASE_INSENSITIVE
    );

    @Override
    public boolean supports(SourceFileLanguage language) {
        return language == SourceFileLanguage.SQL;
    }

    @Override
    public List<DetectedSymbol> analyze(SourceFile sourceFile, String content) {
        List<DetectedSymbol> symbols = new ArrayList<>();

        symbols.addAll(findSymbols(content, CREATE_TABLE_PATTERN, CodeSymbolType.SQL_TABLE));
        symbols.addAll(findSymbols(content, CREATE_VIEW_PATTERN, CodeSymbolType.SQL_VIEW));
        symbols.addAll(findSymbols(content, CREATE_PROCEDURE_PATTERN, CodeSymbolType.SQL_PROCEDURE));
        symbols.addAll(findSymbols(content, CREATE_FUNCTION_PATTERN, CodeSymbolType.SQL_FUNCTION));

        return symbols;
    }

    private List<DetectedSymbol> findSymbols(
            String content,
            Pattern pattern,
            CodeSymbolType type
    ) {
        List<DetectedSymbol> symbols = new ArrayList<>();
        Matcher matcher = pattern.matcher(content);

        while (matcher.find()) {
            String rawName = matcher.group(1);
            String cleanName = cleanSqlIdentifier(rawName);
            int line = calculateLineNumber(content, matcher.start());

            symbols.add(new DetectedSymbol(
                    type,
                    cleanName,
                    cleanName,
                    line,
                    null
            ));
        }

        return symbols;
    }

    private String cleanSqlIdentifier(String identifier) {
        return identifier
                .replace("[", "")
                .replace("]", "")
                .replace("\"", "")
                .trim();
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
}
//TODO: Das ist kein perfekter SQL Parser. Aber für den MVP reichts
