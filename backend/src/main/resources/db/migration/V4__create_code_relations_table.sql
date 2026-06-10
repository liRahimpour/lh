CREATE TABLE code_relations (
                                id UUID PRIMARY KEY,
                                project_id UUID NOT NULL,
                                source_symbol_id UUID NOT NULL,
                                target_symbol_id UUID NOT NULL,
                                type VARCHAR(50) NOT NULL,
                                created_at TIMESTAMP NOT NULL,

                                CONSTRAINT fk_code_relations_project
                                    FOREIGN KEY (project_id)
                                        REFERENCES projects(id)
                                        ON DELETE CASCADE,

                                CONSTRAINT fk_code_relations_source_symbol
                                    FOREIGN KEY (source_symbol_id)
                                        REFERENCES code_symbols(id)
                                        ON DELETE CASCADE,

                                CONSTRAINT fk_code_relations_target_symbol
                                    FOREIGN KEY (target_symbol_id)
                                        REFERENCES code_symbols(id)
                                        ON DELETE CASCADE
);

CREATE INDEX idx_code_relations_project_id
    ON code_relations(project_id);

CREATE INDEX idx_code_relations_source_symbol_id
    ON code_relations(source_symbol_id);

CREATE INDEX idx_code_relations_target_symbol_id
    ON code_relations(target_symbol_id);

CREATE INDEX idx_code_relations_type
    ON code_relations(type);