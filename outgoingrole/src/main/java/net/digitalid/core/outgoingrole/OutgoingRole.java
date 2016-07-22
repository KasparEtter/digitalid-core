package net.digitalid.core.agent;

import java.util.Random;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.digitalid.utility.collections.concurrent.ConcurrentHashMap;
import net.digitalid.utility.collections.concurrent.ConcurrentMap;
import net.digitalid.utility.collections.readonly.ReadOnlySet;
import net.digitalid.utility.annotations.method.Pure;
import net.digitalid.utility.validation.annotations.type.Immutable;

import net.digitalid.database.core.Database;
import net.digitalid.database.annotations.transaction.Committing;
import net.digitalid.database.annotations.transaction.NonCommitting;
import net.digitalid.database.core.exceptions.DatabaseException;

import net.digitalid.core.contact.Contact;
import net.digitalid.core.context.Context;
import net.digitalid.core.credential.Credential;
import net.digitalid.core.entity.Entity;
import net.digitalid.core.entity.NonHostEntity;
import net.digitalid.core.packet.exceptions.RequestException;
import net.digitalid.core.identification.identity.Identity;
import net.digitalid.core.identification.identity.SemanticType;
import net.digitalid.core.pusher.Pusher;
import net.digitalid.core.synchronizer.Synchronizer;

import net.digitalid.service.core.annotations.OnlyForActions;
import net.digitalid.service.core.concept.Aspect;
import net.digitalid.service.core.concept.Instance;
import net.digitalid.service.core.concept.Observer;

/**
 * This class models an outgoing role that acts on behalf of an {@link Identity identity}.
 */
@Immutable
public final class OutgoingRole extends Agent {
    
    
    /* -------------------------------------------------- Aspects -------------------------------------------------- */
    
    /**
     * Stores the aspect of the relation being changed at the observed outgoing role.
     */
    public static final @Nonnull Aspect RELATION = new Aspect(OutgoingRole.class, "relation changed");
    
    /**
     * Stores the aspect of the context being changed at the observed outgoing role.
     */
    public static final @Nonnull Aspect CONTEXT = new Aspect(OutgoingRole.class, "context changed");
    
    
    /* -------------------------------------------------- Issuance -------------------------------------------------- */
    
    /**
     * Issues this outgoing role to the given contacts.
     * 
     * @param contacts the contacts to issue this outgoing role to.
     * 
     * @require isOnHost() : "This outgoing role is on a host.";
     */
    @NonCommitting
    public void issue(@Nonnull ReadOnlySet<Contact> contacts) throws DatabaseException {
        Require.that(isOnHost()).orThrow("This outgoing role is on a host.");
        
        for (final @Nonnull Contact contact : contacts) {
            if (contact.isInternal()) {
                Pusher.send(new OutgoingRoleIssue(this, contact.getInternalPerson()));
            }
        }
    }
    
    /**
     * Issues this outgoing role to the contacts of the context.
     * 
     * @require isOnHost() : "This outgoing role is on a host.";
     */
    @NonCommitting
    public void issue() throws DatabaseException {
        issue(getContext().getAllContacts());
    }
    
    /* -------------------------------------------------- Revocation -------------------------------------------------- */
    
    /**
     * Revokes this outgoing role from the given contacts.
     * 
     * @param contacts the contacts to revoke this outgoing role from.
     * 
     * @require isOnHost() : "This outgoing role is on a host.";
     */
    @NonCommitting
    public void revoke(@Nonnull ReadOnlySet<Contact> contacts) throws DatabaseException {
        Require.that(isOnHost()).orThrow("This outgoing role is on a host.");
        
        for (final @Nonnull Contact contact : contacts) {
            if (contact.isInternal()) {
                Pusher.send(new OutgoingRoleIssue(this, contact.getInternalPerson()));
            }
        }
    }
    
    /**
     * Revokes this outgoing role from the contacts of the context.
     * 
     * @require isOnHost() : "This outgoing role is on a host.";
     */
    @NonCommitting
    public void revoke() throws DatabaseException {
        revoke(getContext().getAllContacts());
    }
    
    /* -------------------------------------------------- Relation -------------------------------------------------- */
    
    /**
     * Stores the relation between the issuing and the receiving identity.
     * 
     * @invariant relation.isRoleType() : "The relation is a role type.";
     */
    private @Nullable SemanticType relation;
    
    /**
     * Returns the relation between the issuing and the receiving identity.
     * 
     * @return the relation between the issuing and the receiving identity.
     * 
     * @ensure return.isRoleType() : "The returned relation is a role type.";
     */
    @Pure
    @NonCommitting
    public @Nonnull SemanticType getRelation() throws DatabaseException {
        if (relation == null) { relation = AgentModule.getRelation(this); }
        return relation;
    }
    
    /**
     * Sets the relation of this outgoing role.
     * 
     * @param newRelation the new relation of this outgoing role.
     * 
     * @require isOnClient() : "This outgoing role is on a client.";
     * @require newRelation.isRoleType() : "The new relation is a role type.";
     */
    @Committing
    public void setRelation(@Nonnull SemanticType newRelation) throws DatabaseException {
        final @Nonnull SemanticType oldRelation = getRelation();
        if (!newRelation.equals(oldRelation)) {
            Synchronizer.execute(new OutgoingRoleRelationReplace(this, oldRelation, newRelation));
        }
    }
    
    /**
     * Replaces the relation of this outgoing role.
     * 
     * @param oldRelation the old relation of this outgoing role.
     * @param newRelation the new relation of this outgoing role.
     * 
     * @require oldRelation.isRoleType() : "The old relation is a role type.";
     * @require newRelation.isRoleType() : "The new relation is a role type.";
     */
    @NonCommitting
    @OnlyForActions
    public void replaceRelation(@Nonnull SemanticType oldRelation, @Nonnull SemanticType newRelation) throws DatabaseException {
        if (isOnHost()) { revoke(); }
        AgentModule.replaceRelation(this, oldRelation, newRelation);
        relation = newRelation;
        if (isOnHost()) { issue(); }
        notify(RELATION);
    }
    
    /* -------------------------------------------------- Context -------------------------------------------------- */
    
    /**
     * Stores the context to which this outgoing role is assigned.
     * 
     * @invariant context.getEntity().equals(getEntity()) : "The context belongs to the same entity.";
     */
    private @Nullable Context context;
    
    /**
     * Returns the context to which this outgoing role is assigned.
     * 
     * @return the context to which this outgoing role is assigned.
     * 
     * @ensure return.getEntity().equals(getEntity()) : "The context belongs to the same entity.";
     */
    @Pure
    @NonCommitting
    public @Nonnull Context getContext() throws DatabaseException {
        if (context == null) { context = AgentModule.getContext(this); }
        return context;
    }
    
    /**
     * Sets the context of this outgoing role.
     * 
     * @param newContext the new context of this outgoing role.
     * 
     * @require isOnClient() : "This outgoing role is on a client.";
     * @require newContext.getEntity().equals(getEntity()) : "The new context belongs to the same entity.";
     */
    @Committing
    public void setContext(@Nonnull Context newContext) throws DatabaseException {
        final @Nonnull Context oldContext = getContext();
        if (!newContext.equals(oldContext)) {
            Synchronizer.execute(new OutgoingRoleContextReplace(this, oldContext, newContext));
        }
    }
    
    /**
     * Replaces the context of this outgoing role.
     * 
     * @param oldContext the old context of this outgoing role.
     * @param newContext the new context of this outgoing role.
     * 
     * @require oldContext.getEntity().equals(getEntity()) : "The old context belongs to the same entity.";
     * @require newContext.getEntity().equals(getEntity()) : "The new context belongs to the same entity.";
     */
    @NonCommitting
    @OnlyForActions
    public void replaceContext(@Nonnull Context oldContext, @Nonnull Context newContext) throws DatabaseException {
        AgentModule.replaceContext(this, oldContext, newContext);
        if (isOnHost()) {
            final @Nonnull ReadOnlySet<Contact> oldContacts = oldContext.getAllContacts();
            final @Nonnull ReadOnlySet<Contact> newContacts = newContext.getAllContacts();
            revoke(oldContacts.subtract(newContacts));
            issue(newContacts.subtract(oldContacts));
        }
        context = newContext;
        notify(CONTEXT);
    }
    
    /* -------------------------------------------------- Restrictable -------------------------------------------------- */
    
    /**
     * Stores whether this outgoing role can be restricted.
     */
    private final boolean restrictable;
    
    /**
     * Returns whether this outgoing role can be restricted.
     * 
     * @return whether this outgoing role can be restricted.
     */
    public boolean isRestrictable() {
        return restrictable;
    }
    
    /**
     * Restricts this outgoing role to the permissions and restrictions of the given credential.
     * 
     * @param credential the credential with which to restrict this outgoing role.
     * 
     * @require isRestrictable() : "This outgoing role can be restricted.";
     * @require credential.isRoleBased() : "The credential is role-based.";
     */
    @NonCommitting
    public void restrictTo(@Nonnull Credential credential) throws DatabaseException {
        Require.that(isRestrictable()).orThrow("This outgoing role can be restricted.");
        Require.that(credential.isRoleBased()).orThrow("The credential is role-based.");
        
        if (permissions == null) { getPermissions(); }
        assert permissions != null;
        permissions.restrictTo(credential.getPermissionsNotNull());
        
        if (restrictions == null) { getRestrictions(); }
        assert restrictions != null;
        restrictions = restrictions.restrictTo(credential.getRestrictionsNotNull());
    }
    
    /**
     * Checks whether this outgoing role covers the given credential and throws a {@link RequestException} if not.
     * 
     * @param credential the credential that needs to be covered.
     * 
     * @require credential.getPermissions() != null : "The permissions of the credential are not null.";
     * @require credential.getRestrictions() != null : "The restrictions of the credential are not null.";
     */
    @NonCommitting
    public void checkCovers(@Nonnull Credential credential) throws DatabaseException, RequestException {
        final @Nullable ReadOnlyAgentPermissions permissions = credential.getPermissions();
        Require.that(permissions != null).orThrow("The permissions of the credential are not null.");
        final @Nullable Restrictions restrictions = credential.getRestrictions();
        Require.that(restrictions != null).orThrow("The restrictions of the credential are not null.");
        
        getPermissions().checkCover(permissions);
        getRestrictions().checkCover(restrictions);
    }
    
    /* -------------------------------------------------- Agent -------------------------------------------------- */
    
    @Override
    public void reset() throws DatabaseException {
        this.relation = null;
        this.context = null;
        super.reset();
    }
    
    @Pure
    @Override
    public boolean isClient() {
        return false;
    }
    
    /* -------------------------------------------------- Creation -------------------------------------------------- */
    
    /**
     * Creates a new outgoing role with the given context.
     * 
     * @param relation the relation between the issuing and the receiving identity.
     * @param context the context to which the outgoing role is assigned.
     * 
     * @require relation.isRoleType() : "The relation is a role type.";
     * @require context.isOnClient() : "The context is on a client.";
     */
    @Committing
    public static @Nonnull OutgoingRole create(@Nonnull SemanticType relation, @Nonnull Context context) throws DatabaseException {
        final @Nonnull OutgoingRole outgoingRole = get(context.getRole(), new Random().nextLong(), false, false);
        Synchronizer.execute(new OutgoingRoleCreate(outgoingRole, relation, context));
        return outgoingRole;
    }
    
    /**
     * Creates this outgoing role in the database.
     * 
     * @param relation the relation between the issuing and the receiving identity.
     * @param context the context to which the outgoing role is assigned.
     * 
     * @require relation.isRoleType() : "The relation is a role type.";
     * @require context.getEntity().equals(getEntity()) : "The context belongs to the entity of this outgoing role.";
     */
    @NonCommitting
    @OnlyForActions
    public void createForActions(@Nonnull SemanticType relation, @Nonnull Context context) throws DatabaseException {
        AgentModule.addOutgoingRole(this, relation, context);
        this.relation = relation;
        this.context = context;
        if (isOnHost()) { issue(); }
        notify(Agent.CREATED);
    }
    
    /* -------------------------------------------------- Indexing -------------------------------------------------- */
    
    /**
     * Caches outgoing roles given their entity and number.
     */
    private static final @Nonnull ConcurrentMap<NonHostEntity, ConcurrentMap<Long, OutgoingRole>> index = new ConcurrentHashMap<>();
    
    static {
        if (Database.isSingleAccess()) {
            Instance.observeAspects(new Observer() {
                @Override public void notify(@Nonnull Aspect aspect, @Nonnull Instance instance) { index.remove(instance); }
            }, Entity.DELETED);
        }
    }
    
    /**
     * Resets the outgoing roles of the given entity after having reloaded the agents module.
     * 
     * @param entity the entity whose outgoing roles are to be reset.
     */
    public static void reset(@Nonnull NonHostEntity entity) throws DatabaseException {
        if (Database.isSingleAccess()) {
            final @Nullable ConcurrentMap<Long, OutgoingRole> map = index.get(entity);
            if (map != null) {
                for (final @Nonnull OutgoingRole outgoingRole : map.values()) { outgoingRole.reset(); }
            }
        }
    }
    
    /* -------------------------------------------------- Constructors -------------------------------------------------- */
    
    /**
     * Creates a new outgoing role with the given entity and number.
     * 
     * @param entity the entity to which this outgoing role belongs.
     * @param number the number that references this outgoing role.
     * @param removed whether this outgoing role has been removed.
     * @param restrictable whether the outgoing role can be restricted.
     */
    private OutgoingRole(@Nonnull NonHostEntity entity, long number, boolean removed, boolean restrictable) {
        super(entity, number, removed);
        
        this.restrictable = restrictable;
    }
    
    /**
     * Returns a (locally cached) outgoing role that might not (yet) exist in the database.
     * 
     * @param entity the entity to which the outgoing role belongs.
     * @param number the number that denotes the outgoing role.
     * @param removed whether the outgoing role has been removed.
     * @param restrictable whether the outgoing role can be restricted.
     * 
     * @return a new or existing outgoing role with the given entity and number.
     */
    @Pure
    public static @Nonnull OutgoingRole get(@Nonnull NonHostEntity entity, long number, boolean removed, boolean restrictable) {
        if (!restrictable && Database.isSingleAccess()) {
            @Nullable ConcurrentMap<Long, OutgoingRole> map = index.get(entity);
            if (map == null) { map = index.putIfAbsentElseReturnPresent(entity, new ConcurrentHashMap<Long, OutgoingRole>()); }
            @Nullable OutgoingRole outgoingRole = map.get(number);
            if (outgoingRole == null) { outgoingRole = map.putIfAbsentElseReturnPresent(number, new OutgoingRole(entity, number, removed, restrictable)); }
            return outgoingRole;
        } else {
            return new OutgoingRole(entity, number, removed, restrictable);
        }
    }
    
    /* -------------------------------------------------- SQLizable -------------------------------------------------- */
    
    /**
     * Returns the given column of the result set as an instance of this class.
     * 
     * @param entity the entity to which the outgoing role belongs.
     * @param resultSet the result set to retrieve the data from.
     * @param columnIndex the index of the column containing the data.
     * @param removed whether the outgoing role has been removed.
     * @param restrictable whether the outgoing role can be restricted.
     * 
     * @return the given column of the result set as an instance of this class.
     */
    @Pure
    @NonCommitting
    public static @Nonnull OutgoingRole get(@Nonnull NonHostEntity entity, @NonCapturable @Nonnull SelectionResult result, boolean removed, boolean restrictable) throws DatabaseException {
        return get(entity, resultSet.getLong(columnIndex), removed, restrictable);
    }
    
}