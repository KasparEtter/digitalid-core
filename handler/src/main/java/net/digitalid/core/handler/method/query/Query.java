package net.digitalid.core.handler.method.query;

import javax.annotation.Nonnull;

import net.digitalid.utility.annotations.method.Pure;
import net.digitalid.utility.annotations.method.PureWithSideEffects;
import net.digitalid.utility.validation.annotations.type.Immutable;

import net.digitalid.database.annotations.transaction.NonCommitting;
import net.digitalid.database.exceptions.DatabaseException;

import net.digitalid.core.entity.Entity;
import net.digitalid.core.entity.annotations.OnHostRecipient;
import net.digitalid.core.exceptions.request.RequestException;
import net.digitalid.core.handler.method.MethodImplementation;
import net.digitalid.core.handler.reply.QueryReply;

/**
 * Queries have to be sent by the caller and are thus executed synchronously.
 * 
 * @see InternalQuery
 * @see ExternalQuery
 */
@Immutable
public abstract class Query<E extends Entity> extends MethodImplementation<E> {
    
    /* -------------------------------------------------- Lodged -------------------------------------------------- */
    
    @Pure
    @Override
    public boolean isLodged() {
        return false;
    }
    
    /* -------------------------------------------------- Requirements -------------------------------------------------- */
    
    @Pure
    @Override
    public final boolean canBeSentByHosts() {
        return false;
    }
    
    @Pure
    @Override
    public final boolean canBeSentByClients() {
        return true;
    }
    
    /* -------------------------------------------------- Execution -------------------------------------------------- */
    
    @Override
    @NonCommitting
    @OnHostRecipient
    @PureWithSideEffects
    public abstract @Nonnull QueryReply<E> executeOnHost() throws RequestException, DatabaseException;
    
}
