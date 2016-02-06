package net.digitalid.core.client;

import java.io.IOException;
import java.sql.SQLException;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.digitalid.utility.validation.annotations.elements.NonNullableElements;
import net.digitalid.utility.collections.freezable.FreezableArrayList;
import net.digitalid.utility.collections.freezable.FreezableList;
import net.digitalid.utility.collections.readonly.ReadOnlyList;
import net.digitalid.utility.collections.tuples.FreezablePair;
import net.digitalid.utility.collections.tuples.ReadOnlyPair;
import net.digitalid.utility.logging.exceptions.ExternalException;
import net.digitalid.utility.freezable.Frozen;
import net.digitalid.utility.validation.annotations.type.Immutable;
import net.digitalid.utility.validation.annotations.method.Pure;

import net.digitalid.database.core.annotations.NonCommitting;
import net.digitalid.database.core.exceptions.DatabaseException;

import net.digitalid.core.service.CoreService;

import net.digitalid.core.conversion.Block;

import net.digitalid.core.conversion.wrappers.signature.SignatureWrapper;

import net.digitalid.core.conversion.wrappers.structure.ListWrapper;
import net.digitalid.core.conversion.wrappers.structure.TupleWrapper;

import net.digitalid.core.agent.FreezableAgentPermissions;
import net.digitalid.core.agent.ReadOnlyAgentPermissions;
import net.digitalid.core.agent.Restrictions;

import net.digitalid.core.settings.PasswordModule;

import net.digitalid.service.core.cryptography.PublicKey;
import net.digitalid.service.core.dataservice.StateModule;

import net.digitalid.core.entity.Entity;
import net.digitalid.core.entity.NativeRole;
import net.digitalid.core.entity.NonHostEntity;
import net.digitalid.core.packet.exceptions.InvalidDeclarationException;
import net.digitalid.core.exceptions.NetworkException;
import net.digitalid.core.exceptions.RequestErrorCode;
import net.digitalid.core.exceptions.RequestException;

import net.digitalid.core.handler.Action;
import net.digitalid.core.handler.Method;

import net.digitalid.core.handler.core.CoreServiceInternalAction;

import net.digitalid.core.identifier.HostIdentifier;
import net.digitalid.core.identifier.InternalNonHostIdentifier;
import net.digitalid.core.identifier.NonHostIdentifier;

import net.digitalid.core.identity.NonHostIdentity;
import net.digitalid.core.identity.SemanticType;

import net.digitalid.core.resolution.Category;
import net.digitalid.core.resolution.FreezablePredecessors;
import net.digitalid.core.resolution.Mapper;
import net.digitalid.core.resolution.Predecessor;
import net.digitalid.core.resolution.Successor;

/**
 * Initializes a new account with the given states.
 */
@Immutable
public final class AccountInitialize extends CoreServiceInternalAction {
    
    /**
     * Stores the semantic type {@code state.initialize.account@core.digitalid.net}.
     */
    private static final @Nonnull SemanticType STATE = SemanticType.map("state.initialize.account@core.digitalid.net").load(TupleWrapper.XDF_TYPE, Predecessor.TYPE, CoreService.STATE);
    
    /**
     * Stores the semantic type {@code initialize.account@core.digitalid.net}.
     */
    public static final @Nonnull SemanticType TYPE = SemanticType.map("initialize.account@core.digitalid.net").load(ListWrapper.XDF_TYPE, STATE);
    
    
    /**
     * Stores the states to merge into the new account.
     */
    private final @Nonnull @Frozen @NonNullableElements ReadOnlyList<ReadOnlyPair<Predecessor, Block>> states;
    
    /**
     * Creates an action to initialize a new account.
     * 
     * @param role the role to which this handler belongs.
     * @param states the states to merge into the new account.
     */
    @NonCommitting
    AccountInitialize(@Nonnull NativeRole role, @Nonnull @Frozen @NonNullableElements ReadOnlyList<ReadOnlyPair<Predecessor, Block>> states) throws DatabaseException, NetworkException, InternalException, ExternalException, RequestException {
        super(role);
        
        this.states = states;
    }
    
    /**
     * Creates an action that decodes the given block.
     * 
     * @param entity the entity to which this handler belongs.
     * @param signature the signature of this handler (or a dummy that just contains a subject).
     * @param recipient the recipient of this method.
     * @param block the content which is to be decoded.
     * 
     * @require signature.hasSubject() : "The signature has a subject.";
     * @require block.getType().isBasedOn(TYPE) : "The block is based on the indicated type.";
     * 
     * @ensure hasSignature() : "This handler has a signature.";
     */
    @NonCommitting
    private AccountInitialize(@Nonnull Entity entity, @Nonnull SignatureWrapper signature, @Nonnull HostIdentifier recipient, @Nonnull Block block) throws DatabaseException, NetworkException, InternalException, ExternalException, RequestException {
        super(entity, signature, recipient);
        
        final @Nonnull InternalNonHostIdentifier subject = getSubject().castTo(InternalNonHostIdentifier.class);
        if (isOnHost() && FreezablePredecessors.exist(subject)) { throw RequestException.get(RequestErrorCode.METHOD, "The subject " + subject + " is already initialized."); }
        
        final @Nonnull Category category = entity.getIdentity().getCategory();
        final @Nonnull ReadOnlyList<Block> elements = ListWrapper.decodeNonNullableElements(block);
        if (elements.size() > 1 && !category.isInternalPerson()) { throw InvalidDeclarationException.get("Only internal persons may have more than one predecessor.", subject); }
        
        final @Nonnull FreezableList<ReadOnlyPair<Predecessor, Block>> states = FreezableArrayList.getWithCapacity(elements.size());
        for (final @Nonnull Block element : elements) {
            final @Nonnull TupleWrapper tuple = TupleWrapper.decode(element);
            final @Nonnull Predecessor predecessor = new Predecessor(tuple.getNonNullableElement(0));
            final @Nonnull NonHostIdentifier identifier = predecessor.getIdentifier();
            final @Nonnull NonHostIdentity identity = identifier.getIdentity();
            final @Nonnull String message = "The claimed predecessor " + identifier + " of " + subject;
            if (!(identity.getCategory().isExternalPerson() && category.isInternalPerson() || identity.getCategory() == category)) { throw InvalidDeclarationException.get(message + " has a wrong category.", subject); }
            if (identifier instanceof InternalNonHostIdentifier) {
                if (!FreezablePredecessors.get((InternalNonHostIdentifier) identifier).equals(predecessor.getPredecessors())) { throw InvalidDeclarationException.get(message + " has other predecessors.", subject); }
            } else {
                if (!predecessor.getPredecessors().isEmpty()) { throw InvalidDeclarationException.get(message + " is an external person and may not have any predecessors.", subject); }
            }
            if (!Successor.getReloaded(identifier).equals(subject)) { throw InvalidDeclarationException.get(message + " does not link back.", subject); }
            states.add(new FreezablePair<>(predecessor, tuple.getNullableElement(1)).freeze());
        }
        this.states = states.freeze();
    }
    
    @Pure
    @Override
    public @Nonnull Block toBlock() {
        final @Nonnull FreezableList<Block> elements = FreezableArrayList.getWithCapacity(states.size());
        for (final @Nonnull ReadOnlyPair<Predecessor, Block> state : states) {
            elements.add(TupleWrapper.encode(STATE, state.getElement0(), state.getElement1()).toBlock());
        }
        return ListWrapper.encode(TYPE, elements.freeze());
    }
    
    @Pure
    @Override
    public @Nonnull String getDescription() {
        return "Initializes the new account.";
    }
    
    
    @Pure
    @Override
    public @Nullable PublicKey getPublicKey() {
        return null;
    }
    
    @Pure
    @Override
    public @Nonnull ReadOnlyAgentPermissions getRequiredPermissionsToSeeAudit() {
        return FreezableAgentPermissions.GENERAL_WRITE;
    }
    
    @Pure
    @Override
    public @Nonnull Restrictions getRequiredRestrictionsToSeeAudit() {
        return Restrictions.MAX;
    }
    
    
    @Override
    @NonCommitting
    protected void executeOnBoth() throws DatabaseException {
        final @Nonnull NonHostEntity entity = getNonHostEntity();
        
        try {
            @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
            final @Nonnull FreezablePredecessors predecessors = new FreezablePredecessors(states.size());
            final @Nonnull FreezableList<NonHostIdentity> identities = FreezableArrayList.getWithCapacity(states.size());
            for (final @Nonnull ReadOnlyPair<Predecessor, Block> state : states) {
                final @Nonnull Predecessor predecessor = state.getElement0();
                identities.add(predecessor.getIdentifier().getIdentity().castTo(NonHostIdentity.class));
                CoreService.SERVICE.addState(entity, state.getElement1());
                predecessors.add(predecessor);
            }
            Mapper.mergeIdentities(identities.freeze(), entity.getIdentity());
            predecessors.set(getSubject().castTo(InternalNonHostIdentifier.class), null);
        } catch (@Nonnull IOException | RequestException | ExternalException exception) {
            throw new SQLException("A problem occurred while adding a state.", exception);
        }
        
        if (states.isEmpty()) { PasswordModule.set(entity, ""); }
    }
    
    @Pure
    @Override
    public boolean interferesWith(@Nonnull Action action) {
        return false;
    }
    
    @Pure
    @Override
    public @Nonnull AccountClose getReverse() {
        return new AccountClose(getRole().toNativeRole(), null);
    }
    
    
    @Pure
    @Override
    public boolean equals(@Nullable Object object) {
        return protectedEquals(object) && object instanceof AccountInitialize && this.states.equals(((AccountInitialize) object).states);
    }
    
    @Pure
    @Override
    public int hashCode() {
        return 89 * protectedHashCode() + states.hashCode();
    }
    
    
    @Pure
    @Override
    public @Nonnull SemanticType getType() {
        return TYPE;
    }
    
    @Pure
    @Override
    public @Nonnull StateModule getModule() {
        return CoreService.SERVICE;
    }
    
    /**
     * The factory class for the surrounding method.
     */
    private static final class Factory extends Method.Factory {
        
        static { Method.add(TYPE, new Factory()); }
        
        @Pure
        @Override
        @NonCommitting
        protected @Nonnull Method create(@Nonnull Entity entity, @Nonnull SignatureWrapper signature, @Nonnull HostIdentifier recipient, @Nonnull Block block) throws DatabaseException, NetworkException, InternalException, ExternalException, RequestException {
            return new AccountInitialize(entity, signature, recipient, block);
        }
        
    }
    
}
