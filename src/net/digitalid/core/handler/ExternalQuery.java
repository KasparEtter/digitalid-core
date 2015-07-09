package net.digitalid.core.handler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.digitalid.core.annotations.Immutable;
import net.digitalid.core.annotations.Pure;
import net.digitalid.core.client.Client;
import net.digitalid.core.entity.Entity;
import net.digitalid.core.entity.Role;
import net.digitalid.core.exceptions.external.InvalidEncodingException;
import net.digitalid.core.host.Host;
import net.digitalid.core.identifier.HostIdentifier;
import net.digitalid.core.identifier.InternalIdentifier;
import net.digitalid.core.service.CoreServiceExternalQuery;
import net.digitalid.core.wrappers.SignatureWrapper;

/**
 * External queries can be sent by both {@link Host hosts} and {@link Client clients}.
 * 
 * @see CoreServiceExternalQuery
 * 
 * @author Kaspar Etter (kaspar.etter@digitalid.net)
 * @version 1.0
 */
@Immutable
public abstract class ExternalQuery extends Query {
    
    /**
     * Creates an external query that encodes the content of a packet for the given recipient about the given subject.
     * 
     * @param role the role to which this handler belongs.
     * @param subject the subject of this handler.
     * @param recipient the recipient of this method.
     */
    protected ExternalQuery(@Nullable Role role, @Nonnull InternalIdentifier subject, @Nonnull HostIdentifier recipient) {
        super(role, subject, recipient);
    }
    
    /**
     * Creates an external query that decodes a packet with the given signature for the given entity.
     * 
     * @param entity the entity to which this handler belongs.
     * @param signature the signature of this handler.
     * @param recipient the recipient of this method.
     * 
     * @require signature.hasSubject() : "The signature has a subject.";
     * 
     * @ensure hasEntity() : "This method has an entity.";
     * @ensure hasSignature() : "This handler has a signature.";
     * @ensure isOnHost() : "Queries are only decoded on hosts.";
     */
    protected ExternalQuery(@Nonnull Entity entity, @Nonnull SignatureWrapper signature, @Nonnull HostIdentifier recipient) throws InvalidEncodingException {
        super(entity, signature, recipient);
    }
    
    
    @Pure
    @Override
    public boolean isSimilarTo(@Nonnull Method other) {
        return super.isSimilarTo(other) && other instanceof ExternalQuery;
    }
    
}
