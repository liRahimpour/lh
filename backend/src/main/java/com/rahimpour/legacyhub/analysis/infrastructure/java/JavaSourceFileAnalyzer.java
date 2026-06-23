package com.rahimpour.legacyhub.analysis.infrastructure.java;

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
public class JavaSourceFileAnalyzer implements SourceFileAnalyzer {

    private static final Pattern PACKAGE_PATTERN = Pattern.compile(
            "^\\s*package\\s+([a-zA-Z_][a-zA-Z0-9_\\.]*);",
            Pattern.MULTILINE
    );

    private static final Pattern CLASS_PATTERN = Pattern.compile(
            "^(?:\\s*@\\w+(?:\\([^)]*\\))?\\s*)*\\s*(?:public|protected|private|abstract|final|static|sealed|non-sealed\\s+)*\\s*class\\s+([A-Za-z_][A-Za-z0-9_]*)\\b",
            Pattern.MULTILINE
    );

    private static final Pattern INTERFACE_PATTERN = Pattern.compile(
            "^(?:\\s*@\\w+(?:\\([^)]*\\))?\\s*)*\\s*(?:public|protected|private|abstract|static\\s+)*\\s*interface\\s+([A-Za-z_][A-Za-z0-9_]*)\\b",
            Pattern.MULTILINE
    );

    private static final Pattern ENUM_PATTERN = Pattern.compile(
            "^(?:\\s*@\\w+(?:\\([^)]*\\))?\\s*)*\\s*(?:public|protected|private|static\\s+)*\\s*enum\\s+([A-Za-z_][A-Za-z0-9_]*)\\b",
            Pattern.MULTILINE
    );

    private static final Pattern RECORD_PATTERN = Pattern.compile(
            "^(?:\\s*@\\w+(?:\\([^)]*\\))?\\s*)*\\s*(?:public|protected|private|static|final\\s+)*\\s*record\\s+([A-Za-z_][A-Za-z0-9_]*)\\b",
            Pattern.MULTILINE
    );

    private static final Pattern ANNOTATION_PATTERN = Pattern.compile(
            "^(?:\\s*@\\w+(?:\\([^)]*\\))?\\s*)*\\s*(?:public|protected|private\\s+)*\\s*@interface\\s+([A-Za-z_][A-Za-z0-9_]*)\\b",
            Pattern.MULTILINE
    );

    private static final Pattern METHOD_WITH_BODY_PATTERN = Pattern.compile(
            "^\\s*(?:@[A-Za-z_][A-Za-z0-9_]*(?:\\([^)]*\\))?\\s*)*"
                    + "(?:(?:public|protected|private|static|final|abstract|synchronized|native|default|strictfp)\\s+)*+"
                    + "(?!if\\b|for\\b|while\\b|switch\\b|catch\\b|return\\b|new\\b)"
                    + "(?!class\\s+|interface\\s+|enum\\s+|record\\s+|@interface\\s+)"
                    + "[A-Za-z_][A-Za-z0-9_<>,\\[\\]\\.?\\s]*\\s+"
                    + "([A-Za-z_][A-Za-z0-9_]*)\\s*\\([^;{}]*\\)\\s*"
                    + "(?:throws\\s+[A-Za-z0-9_,\\.\\s]+)?\\s*\\{",
            Pattern.MULTILINE
    );

    private static final Pattern METHOD_DECLARATION_PATTERN = Pattern.compile(
            "^\\s*(?:@[A-Za-z_][A-Za-z0-9_]*(?:\\([^)]*\\))?\\s*)*"
                    + "(?:public|protected|private|static|final|abstract|default\\s+)*"
                    + "(?!if\\b|for\\b|while\\b|switch\\b|catch\\b|return\\b|new\\b)"
                    + "[A-Za-z_][A-Za-z0-9_<>,\\[\\]\\.?\\s]*\\s+"
                    + "([A-Za-z_][A-Za-z0-9_]*)\\s*\\([^;{}]*\\)\\s*"
                    + "(?:throws\\s+[A-Za-z0-9_,\\.\\s]+)?\\s*;",
            Pattern.MULTILINE
    );

    private static final Pattern FIELD_PATTERN = Pattern.compile(
            "^\\s*(?:@[A-Za-z_][A-Za-z0-9_]*(?:\\([^)]*\\))?\\s*)*"
                    + "(?:(?:public|protected|private|static|final|volatile|transient)\\s+)*"
                    + "[A-Za-z_][A-Za-z0-9_<>,\\[\\]\\.?]*\\s+"
                    + "([A-Za-z_][A-Za-z0-9_]*)\\s*(?:=\\s*[^;]+)?;",
            Pattern.MULTILINE
    );

    @Override
    public boolean supports(SourceFileLanguage language) {
        return language == SourceFileLanguage.JAVA;
    }

    @Override
    public List<DetectedSymbol> analyze(SourceFile sourceFile, String content) {
        List<DetectedSymbol> symbols = new ArrayList<>();

        String packageName = findPackageName(content);

        findPackage(content, symbols);
        findAll(content, CLASS_PATTERN, CodeSymbolType.CLASS, packageName, symbols);
        findAll(content, INTERFACE_PATTERN, CodeSymbolType.INTERFACE, packageName, symbols);
        findAll(content, ENUM_PATTERN, CodeSymbolType.ENUM, packageName, symbols);
        findAll(content, RECORD_PATTERN, CodeSymbolType.RECORD, packageName, symbols);
        findAll(content, ANNOTATION_PATTERN, CodeSymbolType.ANNOTATION, packageName, symbols);
        findAll(content, METHOD_WITH_BODY_PATTERN, CodeSymbolType.METHOD, packageName, symbols);
        findAll(content, METHOD_DECLARATION_PATTERN, CodeSymbolType.METHOD, packageName, symbols);
        findAll(content, FIELD_PATTERN, CodeSymbolType.FIELD, packageName, symbols);

        return symbols;
    }

    private void findPackage(String content, List<DetectedSymbol> symbols) {
        Matcher matcher = PACKAGE_PATTERN.matcher(content);

        if (matcher.find()) {
            String packageName = matcher.group(1);
            int line = calculateLineNumber(content, matcher.start());

            symbols.add(new DetectedSymbol(
                    CodeSymbolType.NAMESPACE,
                    packageName,
                    packageName,
                    line,
                    null
            ));
        }
    }

    private String findPackageName(String content) {
        Matcher matcher = PACKAGE_PATTERN.matcher(content);

        if (matcher.find()) {
            return matcher.group(1);
        }

        return null;
    }

    private void findAll(
            String content,
            Pattern pattern,
            CodeSymbolType type,
            String packageName,
            List<DetectedSymbol> symbols
    ) {
        Matcher matcher = pattern.matcher(content);

        while (matcher.find()) {
            String name = matcher.group(1);
            int line = calculateLineNumber(content, matcher.start());
            String fullyQualifiedName = buildFullyQualifiedName(packageName, name);

            symbols.add(new DetectedSymbol(
                    type,
                    name,
                    fullyQualifiedName,
                    line,
                    null
            ));
        }
    }

    private String buildFullyQualifiedName(String packageName, String name) {
        if (packageName == null || packageName.isBlank()) {
            return name;
        }

        return packageName + "." + name;
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
