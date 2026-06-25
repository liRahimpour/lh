package com.rahimpour.legacyhub.graph.domain;

public record GraphConnection(
        String relationType,
        String direction,
        GraphSymbol symbol
) {
}

//Beispiel:
//
//HAS_METHOD
//OUTGOING
//saveCustomer