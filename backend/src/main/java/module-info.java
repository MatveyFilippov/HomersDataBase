module backend {
    requires com.google.common;

    exports homer.database.backend;
    exports homer.database.backend.engine.exceptions;
    exports homer.database.backend.engine.exceptions.catchable;
    exports homer.database.backend.engine.datatypes;
    exports homer.database.backend.engine.datatypes.base.implementations;
    exports homer.database.backend.engine.columns;
}