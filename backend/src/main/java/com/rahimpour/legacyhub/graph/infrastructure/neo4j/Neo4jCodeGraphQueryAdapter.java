package com.rahimpour.legacyhub.graph.infrastructure.neo4j;

import com.rahimpour.legacyhub.graph.domain.*;
import com.rahimpour.legacyhub.graph.ports.CodeGraphQueryPort;
import org.neo4j.driver.Driver;
import org.neo4j.driver.Record;
import org.neo4j.driver.Session;
import org.neo4j.driver.types.Node;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Component
public class Neo4jCodeGraphQueryAdapter implements CodeGraphQueryPort {

    private final Driver driver;

    public Neo4jCodeGraphQueryAdapter(Driver driver) {
        this.driver = driver;
    }

    @Override
    public GraphOverview getProjectOverview(UUID projectId) {
        try (Session session = driver.session()) {
            Map<String, Object> parameters = Map.of(
                    "projectId", projectId.toString()
            );

            long symbolCount = session.run(
                    """
                    MATCH (symbol:CodeSymbol {projectId: $projectId})
                    RETURN count(symbol) AS count
                    """,
                    parameters
            ).single().get("count").asLong();

            long relationCount = session.run(
                    """
                    MATCH (:CodeSymbol {projectId: $projectId})-[relation]->(:CodeSymbol {projectId: $projectId})
                    RETURN count(relation) AS count
                    """,
                    parameters
            ).single().get("count").asLong();

            Map<String, Long> relationTypeCounts = new HashMap<>();

            List<Record> records = session.run(
                    """
                    MATCH (:CodeSymbol {projectId: $projectId})-[relation]->(:CodeSymbol {projectId: $projectId})
                    RETURN type(relation) AS relationType, count(relation) AS count
                    ORDER BY relationType
                    """,
                    parameters
            ).list();

            for (Record record : records) {
                relationTypeCounts.put(
                        record.get("relationType").asString(),
                        record.get("count").asLong()
                );
            }

            return new GraphOverview(
                    projectId,
                    symbolCount,
                    relationCount,
                    relationTypeCounts
            );
        }
    }

    @Override
    public Optional<GraphNeighborhood> findNeighborhood(UUID projectId, UUID symbolId) {
        try (Session session = driver.session()) {
            Map<String, Object> parameters = Map.of(
                    "projectId", projectId.toString(),
                    "symbolId", symbolId.toString()
            );

            List<Record> centerRecords = session.run(
                    """
                    MATCH (center:CodeSymbol {
                        projectId: $projectId,
                        id: $symbolId
                    })
                    RETURN center
                    """,
                    parameters
            ).list();

            if (centerRecords.isEmpty()) {
                return Optional.empty();
            }

            GraphSymbol center = toGraphSymbol(centerRecords.getFirst().get("center").asNode());

            List<GraphConnection> outgoing = session.run(
                            """
                            MATCH (center:CodeSymbol {
                                projectId: $projectId,
                                id: $symbolId
                            })-[relation]->(target:CodeSymbol {
                                projectId: $projectId
                            })
                            RETURN type(relation) AS relationType, target
                            ORDER BY relationType, target.name
                            """,
                            parameters
                    ).list()
                    .stream()
                    .map(record -> new GraphConnection(
                            record.get("relationType").asString(),
                            "OUTGOING",
                            toGraphSymbol(record.get("target").asNode())
                    ))
                    .toList();

            List<GraphConnection> incoming = session.run(
                            """
                            MATCH (source:CodeSymbol {
                                projectId: $projectId
                            })-[relation]->(center:CodeSymbol {
                                projectId: $projectId,
                                id: $symbolId
                            })
                            RETURN type(relation) AS relationType, source
                            ORDER BY relationType, source.name
                            """,
                            parameters
                    ).list()
                    .stream()
                    .map(record -> new GraphConnection(
                            record.get("relationType").asString(),
                            "INCOMING",
                            toGraphSymbol(record.get("source").asNode())
                    ))
                    .toList();

            return Optional.of(new GraphNeighborhood(center, outgoing, incoming));
        }
    }

    @Override
    public GraphTraversalResult findDependencies(
            UUID projectId,
            UUID symbolId,
            int maxDepth
    ) {
        String cypher = """
            MATCH path = (center:CodeSymbol {
                projectId: $projectId,
                id: $symbolId
            })-[:USES|CALLS|READS_TABLE|WRITES_TABLE|DEPENDS_ON*1..%d]->(dependency:CodeSymbol {
                projectId: $projectId
            })
            WHERE dependency.id <> $symbolId
            WITH dependency, min(length(path)) AS depth
            RETURN dependency, depth
            ORDER BY depth, dependency.type, dependency.name
            """.formatted(maxDepth);

        List<GraphTraversalItem> results = findTraversalItems(
                projectId,
                symbolId,
                cypher
        );

        return new GraphTraversalResult(
                symbolId,
                GraphTraversalDirection.DEPENDENCIES,
                maxDepth,
                results.size(),
                results
        );
    }

    @Override
    public GraphTraversalResult findImpact(
            UUID projectId,
            UUID symbolId,
            int maxDepth
    ) {
        String cypher = """
            MATCH path = (impact:CodeSymbol {
                projectId: $projectId
            })-[:USES|CALLS|READS_TABLE|WRITES_TABLE|DEPENDS_ON*1..%d]->(center:CodeSymbol {
                projectId: $projectId,
                id: $symbolId
            })
            WHERE impact.id <> $symbolId
            WITH impact, min(length(path)) AS depth
            RETURN impact, depth
            ORDER BY depth, impact.type, impact.name
            """.formatted(maxDepth);

        List<GraphTraversalItem> results = findTraversalItems(
                projectId,
                symbolId,
                cypher
        );

        return new GraphTraversalResult(
                symbolId,
                GraphTraversalDirection.IMPACT,
                maxDepth,
                results.size(),
                results
        );
    }

    private List<GraphTraversalItem> findTraversalItems(
            UUID projectId,
            UUID symbolId,
            String cypher
    ) {
        try (Session session = driver.session()) {
            Map<String, Object> parameters = Map.of(
                    "projectId", projectId.toString(),
                    "symbolId", symbolId.toString()
            );

            return session.run(cypher, parameters)
                    .list()
                    .stream()
                    .map(record -> new GraphTraversalItem(
                            toGraphSymbol(record.get(0).asNode()),
                            record.get("depth").asInt()
                    ))
                    .toList();
        }
    }

    private GraphSymbol toGraphSymbol(Node node) {
        return new GraphSymbol(
                UUID.fromString(node.get("id").asString()),
                UUID.fromString(node.get("projectId").asString()),
                UUID.fromString(node.get("sourceFileId").asString()),
                node.get("name").asString(),
                node.get("type").asString(),
                node.get("fullyQualifiedName").isNull()
                        ? null
                        : node.get("fullyQualifiedName").asString(),
                node.get("startLine").isNull()
                        ? null
                        : node.get("startLine").asInt(),
                node.get("endLine").isNull()
                        ? null
                        : node.get("endLine").asInt()
        );
    }
}
