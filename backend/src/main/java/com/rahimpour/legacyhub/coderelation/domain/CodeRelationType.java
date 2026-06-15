package com.rahimpour.legacyhub.coderelation.domain;

public enum CodeRelationType {
    DECLARES,
    HAS_METHOD,
    HAS_FIELD,
    HAS_COMPONENT,
    HANDLES_EVENT,
    CALLS,
    USES,
    READS_TABLE,
    WRITES_TABLE,
    DEPENDS_ON,
    UNKNOWN
}
