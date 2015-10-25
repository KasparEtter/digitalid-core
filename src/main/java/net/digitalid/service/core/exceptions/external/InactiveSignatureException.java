package net.digitalid.service.core.exceptions.external;

import javax.annotation.Nonnull;
import net.digitalid.utility.annotations.state.Immutable;

/**
 * This exception is thrown when a signature is inactive.
 */
@Immutable
public final class InactiveSignatureException extends ExternalException {
    
    /**
     * Creates a new inactive signature exception with the given message.
     * 
     * @param message a string explaining the exception that occurred.
     */
    public InactiveSignatureException(@Nonnull String message) {
        super(message);
    }
    
}
