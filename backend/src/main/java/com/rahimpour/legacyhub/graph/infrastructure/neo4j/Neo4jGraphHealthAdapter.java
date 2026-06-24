package com.rahimpour.legacyhub.graph.infrastructure.neo4j;

import com.rahimpour.legacyhub.graph.ports.GraphHealthPort;
import org.neo4j.driver.Driver;
import org.neo4j.driver.Session;
import org.springframework.stereotype.Component;

@Component
public class Neo4jGraphHealthAdapter implements GraphHealthPort {

    private final Driver driver;

    public Neo4jGraphHealthAdapter(Driver driver) {
        this.driver = driver;
    }

    @Override
    public boolean isAvailable() {
        try (Session session = driver.session()) {
            session.run("RETURN 1 AS connected").single();
            return true;
        } catch (Exception exception) {
            return false;
        }
    }
}
