package com.rahimpour.legacyhub.codesymbol.domain;

public enum CodeSymbolType {
    NAMESPACE,
    CLASS,
    INTERFACE,
    ENUM,
    RECORD,
    ANNOTATION,
    METHOD,
    PROPERTY,
    FIELD,

    DELPHI_UNIT,
    DELPHI_FORM,
    DELPHI_COMPONENT,
    PROCEDURE,
    FUNCTION,
    EVENT_HANDLER,

    SQL_TABLE,
    SQL_VIEW,
    SQL_PROCEDURE,
    SQL_FUNCTION,

    CONFIG_ENTRY,

    UNKNOWN
}

//example:
// DELPHI_FORM → CustomerForm
//DELPHI_COMPONENT → BtnSave
//EVENT_HANDLER → BtnSaveClick
