package fr.olympus.nomos.semantic;

/**
 * Signals that a semantic strategy could not produce a valid evaluation plan.
 */
public class SemanticAnalysisException extends RuntimeException {

    /**
     * Creates an exception with a descriptive semantic-analysis message.
     *
     * @param message description of the semantic failure
     */
    public SemanticAnalysisException(String message) {
        super(message);
    }

    /**
     * Creates an exception with a descriptive message and underlying cause.
     *
     * @param message description of the semantic failure
     * @param cause underlying failure raised during semantic analysis
     */
    public SemanticAnalysisException(String message, Throwable cause) {
        super(message, cause);
    }
}
