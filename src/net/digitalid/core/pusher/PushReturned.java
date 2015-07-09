package net.digitalid.core.pusher;

import java.io.IOException;
import java.sql.SQLException;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.digitalid.core.agent.ReadOnlyAgentPermissions;
import net.digitalid.core.agent.Restrictions;
import net.digitalid.core.annotations.Immutable;
import net.digitalid.core.annotations.NonCommitting;
import net.digitalid.core.annotations.Pure;
import net.digitalid.core.collections.FreezableArray;
import net.digitalid.core.collections.ReadOnlyArray;
import net.digitalid.core.entity.Entity;
import net.digitalid.core.entity.NonHostAccount;
import net.digitalid.core.errors.ShouldNeverHappenError;
import net.digitalid.core.exceptions.external.ExternalException;
import net.digitalid.core.exceptions.external.InvalidEncodingException;
import net.digitalid.core.exceptions.packet.PacketError;
import net.digitalid.core.exceptions.packet.PacketException;
import net.digitalid.core.handler.ActionReply;
import net.digitalid.core.handler.ExternalAction;
import net.digitalid.core.handler.Method;
import net.digitalid.core.handler.Reply;
import net.digitalid.core.host.Host;
import net.digitalid.core.identifier.HostIdentifier;
import net.digitalid.core.identity.SemanticType;
import net.digitalid.core.module.BothModule;
import net.digitalid.core.packet.Packet;
import net.digitalid.core.packet.Response;
import net.digitalid.core.service.Service;
import net.digitalid.core.wrappers.Block;
import net.digitalid.core.wrappers.BooleanWrapper;
import net.digitalid.core.wrappers.CompressionWrapper;
import net.digitalid.core.wrappers.HostSignatureWrapper;
import net.digitalid.core.wrappers.SelfcontainedWrapper;
import net.digitalid.core.wrappers.SignatureWrapper;
import net.digitalid.core.wrappers.TupleWrapper;

/**
 * An action of this type is added to the audit if an {@link ExternalAction external action} on a {@link Host host} has a {@link ActionReply reply}.
 * 
 * @see Pusher
 * 
 * @author Kaspar Etter (kaspar.etter@digitalid.net)
 * @version 1.0
 */
@Immutable
public final class PushReturned extends ExternalAction {
    
    /**
     * Stores the semantic type {@code valid.returned.push@core.digitalid.net}.
     */
    private static final @Nonnull SemanticType VALID = SemanticType.create("valid.returned.push@core.digitalid.net").load(BooleanWrapper.TYPE);
    
    /**
     * Stores the semantic type {@code reply.returned.push@core.digitalid.net}.
     */
    private static final @Nonnull SemanticType REPLY = SemanticType.create("reply.returned.push@core.digitalid.net").load(Packet.SIGNATURE);
    
    /**
     * Stores the semantic type {@code returned.push@core.digitalid.net}.
     */
    private static final @Nonnull SemanticType TYPE = SemanticType.create("returned.push@core.digitalid.net").load(TupleWrapper.TYPE, VALID, REPLY);
    
    
    /**
     * Stores whether the reply is valid.
     */
    private final boolean valid;
    
    /**
     * Stores the reply which was returned.
     * 
     * @invariant reply.getSignature() instanceof HostSignatureWrapper : "The reply is signed by a host.";
     */
    private final @Nonnull ActionReply reply;
    
    /**
     * Creates an external action to indicate a failed push.
     * 
     * @param account the account to which this handler belongs.
     * @param valid whether the given reply is valid.
     * @param reply the reply that was returned by an action.
     * 
     * @require reply.getSignature() instanceof HostSignatureWrapper : "The reply is signed by a host (and the signature thus not null).";
     * @require account.getIdentity().equals(reply.getEntityNotNull().getIdentity()) : "The account and the reply's entity have the same identity.";
     */
    PushReturned(@Nonnull NonHostAccount account, boolean valid, @Nonnull ActionReply reply) {
        super(account, account.getIdentity().getAddress(), account.getHost().getIdentifier());
        
        assert reply.getSignature() instanceof HostSignatureWrapper : "The reply is signed by a host (and the signature thus not null).";
        assert account.getIdentity().equals(reply.getEntityNotNull().getIdentity()) : "The account and the reply's entity have the same identity.";
        
        this.valid = valid;
        this.reply = reply;
    }
    
    /**
     * Creates an external action that decodes the given block.
     * 
     * @param entity the entity to which this handler belongs.
     * @param signature the signature of this handler.
     * @param recipient the recipient of this method.
     * @param block the content which is to be decoded.
     * 
     * @require signature.hasSubject() : "The signature has a subject.";
     * @require block.getType().isBasedOn(TYPE) : "The block is based on the indicated type.";
     * 
     * @ensure hasSignature() : "This handler has a signature.";
     */
    @NonCommitting
    private PushReturned(@Nonnull Entity entity, @Nonnull SignatureWrapper signature, @Nonnull HostIdentifier recipient, @Nonnull Block block) throws SQLException, IOException, PacketException, ExternalException {
        super(entity, signature, recipient);
        
        final @Nonnull ReadOnlyArray<Block> elements = new TupleWrapper(block).getElementsNotNull(2);
        this.valid = new BooleanWrapper(elements.getNotNull(0)).getValue();
        
        final @Nonnull SignatureWrapper _signature = SignatureWrapper.decodeWithoutVerifying(elements.getNotNull(1), false, null);
        if (!(_signature instanceof HostSignatureWrapper)) throw new InvalidEncodingException("Replies have to be signed by a host.");
        final @Nonnull CompressionWrapper _compression = new CompressionWrapper(_signature.getElementNotNull());
        final @Nonnull SelfcontainedWrapper _content = new SelfcontainedWrapper(_compression.getElementNotNull());
        try {
            this.reply = Reply.get(entity.toNonHostEntity(), (HostSignatureWrapper) _signature, _content.getElement()).toActionReply();
        } catch (@Nonnull PacketException exception) {
            throw new InvalidEncodingException("Could not decode the reply to an external action.", exception);
        }
    }
    
    @Pure
    @Override
    public @Nonnull Block toBlock() {
        final @Nonnull FreezableArray<Block> elements = new FreezableArray<>(2);
        elements.set(0, new BooleanWrapper(VALID, valid).toBlock());
        elements.set(1, reply.getSignatureNotNull().toBlock().setType(TYPE));
        return new TupleWrapper(TYPE, elements.freeze()).toBlock();
    }
    
    @Pure
    @Override
    public @Nonnull String getDescription() {
        return "An external action returned a reply of type " + reply.getType().getAddress() + ".";
    }
    
    
    /**
     * Returns whether the reply is valid.
     * 
     * @return whether the reply is valid.
     */
    public boolean isValid() {
        return valid;
    }
    
    /**
     * Returns the reply which was returned.
     * 
     * @return the reply which was returned.
     */
    public @Nonnull ActionReply getReply() {
        return reply;
    }
    
    
    @Pure
    @Override
    public @Nonnull Service getService() {
        return reply.getService();
    }
    
    
    @Override
    public @Nullable ActionReply executeOnHost() throws PacketException {
        throw new PacketException(PacketError.METHOD, "Returned push replies cannot be executed on a host.");
    }
    
    @Pure
    @Override
    public boolean matches(@Nullable Reply reply) {
        return reply == null;
    }
    
    @Override
    @NonCommitting
    public void executeOnClient() throws SQLException {
        if (!isValid()) {
            // TODO: Add it to the Errors module.
        }
        else reply.executeBySynchronizer();
    }
    
    @Override
    public void executeOnFailure() {
        throw new ShouldNeverHappenError("Returned push replies should never be pushed themselves.");
    }
    
    
    @Pure
    @Override
    public boolean isSimilarTo(@Nonnull Method other) {
        return false;
    }
    
    @Override
    public @Nullable Response send() throws PacketException {
        throw new PacketException(PacketError.INTERNAL, "Returned push replies cannot be sent.");
    }
    
    
    @Pure
    @Override
    public @Nonnull ReadOnlyAgentPermissions getAuditPermissions() {
        return reply.getAuditPermissions();
    }
    
    @Pure
    @Override
    public @Nonnull Restrictions getAuditRestrictions() {
        return reply.getAuditRestrictions();
    }
    
    
    @Pure
    @Override
    public boolean equals(@Nullable Object object) {
        if (protectedEquals(object) && object instanceof PushFailed) {
            final @Nonnull PushReturned other = (PushReturned) object;
            return this.valid == other.valid && this.reply.equals(other.reply);
        }
        return false;
    }
    
    @Pure
    @Override
    public int hashCode() {
        int hash = protectedHashCode();
        hash = 89 * hash + (valid ? 1 : 0);
        hash = 89 * hash + reply.hashCode();
        return hash;
    }
    
    
    @Pure
    @Override
    public @Nonnull SemanticType getType() {
        return TYPE;
    }
    
    @Pure
    @Override
    public @Nonnull BothModule getModule() {
        return PusherModule.MODULE;
    }
    
    /**
     * The factory class for the surrounding method.
     */
    private static final class Factory extends Method.Factory {
        
        static { Method.add(TYPE, new Factory()); }
        
        @Pure
        @Override
        @NonCommitting
        protected @Nonnull Method create(@Nonnull Entity entity, @Nonnull SignatureWrapper signature, @Nonnull HostIdentifier recipient, @Nonnull Block block) throws SQLException, IOException, PacketException, ExternalException {
            return new PushReturned(entity, signature, recipient, block);
        }
        
    }
    
}
