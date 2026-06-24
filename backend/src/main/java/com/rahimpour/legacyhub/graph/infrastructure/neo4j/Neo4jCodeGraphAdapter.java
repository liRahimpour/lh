package com.rahimpour.legacyhub.graph.infrastructure.neo4j;

import com.rahimpour.legacyhub.coderelation.domain.CodeRelation;
import com.rahimpour.legacyhub.codesymbol.domain.CodeSymbol;
import com.rahimpour.legacyhub.graph.ports.CodeGraphPort;
import org.neo4j.driver.Driver;
import org.neo4j.driver.Session;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Component
public class Neo4jCodeGraphAdapter implements CodeGraphPort {

    private final Driver driver;

    public Neo4jCodeGraphAdapter(Driver driver) {
        this.driver = driver;
    }

    @Override
    public void syncSymbols(UUID projectId, List<CodeSymbol> symbols) {
        if (symbols.isEmpty()) {
            return;
        }

        try (Session session = driver.session()) {
            session.executeWriteWithoutResult(tx -> {
                for (CodeSymbol symbol : symbols) {
                    tx.run(
                            """
                            MERGE (symbol:CodeSymbol {id: $id})
                            SET symbol.projectId = $projectId,
                                symbol.sourceFileId = $sourceFileId,
                                symbol.name = $name,
                                symbol.type = $type,
                                symbol.fullyQualifiedName = $fullyQualifiedName,
                                symbol.startLine = $startLine,
                                symbol.endLine = $endLine
                            """,
                            toParameters(symbol)
                    );
                }
            });
        }
    }

    @Override
    public void syncRelations(UUID projectId, List<CodeRelation> relations) {
        if (relations.isEmpty()) {
            return;
        }

        try (Session session = driver.session()) {
            session.executeWriteWithoutResult(tx -> {
                for (CodeRelation relation : relations) {
                    String relationType = relation.getType().name();

                    String cypher = """
                        MATCH (source:CodeSymbol {
                            id: $sourceSymbolId,
                            projectId: $projectId
                        })
                        MATCH (target:CodeSymbol {
                            id: $targetSymbolId,
                            projectId: $projectId
                        })
                        MERGE (source)-[relationship:%s]->(target)
                        SET relationship.id = $relationId,
                            relationship.projectId = $projectId,
                            relationship.createdAt = $createdAt
                        """.formatted(relationType);

                    tx.run(cypher, toRelationParameters(relation));
                }
            });
        }
    }

    @Override
    public void deleteProjectGraph(UUID projectId) {
        try (Session session = driver.session()) {
            session.executeWriteWithoutResult(tx ->
                    tx.run(
                            """
                            MATCH (symbol:CodeSymbol {projectId: $projectId})
                            DETACH DELETE symbol
                            """,
                            Map.of("projectId", projectId.toString())
                    )
            );
        }
    }

    private Map<String, Object> toParameters(CodeSymbol symbol) {
        Map<String, Object> parameters = new HashMap<>();

        parameters.put("id", symbol.getId().toString());
        parameters.put("projectId", symbol.getProjectId().toString());
        parameters.put("sourceFileId", symbol.getSourceFileId().toString());
        parameters.put("name", symbol.getName());
        parameters.put("type", symbol.getType().name());
        parameters.put("fullyQualifiedName", symbol.getFullyQualifiedName());
        parameters.put("startLine", symbol.getStartLine());
        parameters.put("endLine", symbol.getEndLine());

        return parameters;
    }

    private Map<String, Object> toRelationParameters(CodeRelation relation) {
        Map<String, Object> parameters = new HashMap<>();

        parameters.put("relationId", relation.getId().toString());
        parameters.put("projectId", relation.getProjectId().toString());
        parameters.put("sourceSymbolId", relation.getSourceSymbolId().toString());
        parameters.put("targetSymbolId", relation.getTargetSymbolId().toString());
        parameters.put("createdAt", relation.getCreatedAt().toString());

        return parameters;
    }
}
