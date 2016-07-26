package net.digitalid.core.agent;

import javax.annotation.Nonnull;

import net.digitalid.utility.annotations.method.Pure;
import net.digitalid.utility.annotations.ownership.Capturable;
import net.digitalid.utility.collections.list.FreezableList;
import net.digitalid.utility.freezable.annotations.NonFrozen;
import net.digitalid.utility.property.indexed.WritableIndexedProperty;
import net.digitalid.utility.property.nonnullable.WritableNonNullableProperty;
import net.digitalid.utility.validation.annotations.generation.Recover;
import net.digitalid.utility.validation.annotations.type.Immutable;

import net.digitalid.database.annotations.transaction.NonCommitting;
import net.digitalid.database.exceptions.DatabaseException;

import net.digitalid.core.concept.CoreConcept;
import net.digitalid.core.concept.annotations.GenerateProperty;
import net.digitalid.core.entity.NonHostEntity;
import net.digitalid.core.exceptions.request.RequestErrorCode;
import net.digitalid.core.exceptions.request.RequestException;
import net.digitalid.core.identification.identity.Identity;
import net.digitalid.core.identification.identity.SemanticType;
import net.digitalid.core.permissions.ReadOnlyAgentPermissions;
import net.digitalid.core.restrictions.Restrictions;

/**
 * This class models an agent that acts on behalf of an {@link Identity identity}.
 */
@Immutable
//@GenerateConverter
public abstract class Agent extends CoreConcept<NonHostEntity, Long> {
    
    /* -------------------------------------------------- Aspects -------------------------------------------------- */
    
    // TODO: How can we observe when a new agend is added? Do we need an extensible property besides the index?
    
//    /**
//     * Stores the aspect of the observed agent being created in the database.
//     * This aspect is also used to notify that an agent gets unremoved again.
//     */
//    public static final @Nonnull Aspect CREATED = new Aspect(Agent.class, "created");
    
    /* -------------------------------------------------- Removed -------------------------------------------------- */
    
    /**
     * Returns whether this agent is removed.
     */
    @Pure
    @GenerateProperty(requiredAgentToExecuteMethod = "concept", requiredAgentToSeeMethod = "concept")
    public abstract @Nonnull WritableNonNullableProperty<Boolean> removed();
    
    /**
     * Checks that this agent is not removed and throws a {@link RequestException} otherwise.
     */
    @Pure
    public void checkNotRemoved() throws RequestException {
        if (removed().get()) { throw RequestException.with(RequestErrorCode.AUTHORIZATION, "The agent has been removed."); }
    }
    
    // TODO: How do we issue and revoke outgoing roles? I guess the outgoing role has to observe its own property.
    
//    /**
//     * Removes this agent from the database by marking it as being removed.
//     */
//    @NonCommitting
//    @OnlyForActions
//    final void removeForActions() throws DatabaseException {
//        AgentModule.removeAgent(this);
//        if (isOnHost() && this instanceof OutgoingRole) { ((OutgoingRole) this).revoke(); }
//        removed = true;
//        notify(DELETED);
//    }
//    
//    /**
//     * Unremoves this agent from the database by marking it as no longer being removed.
//     */
//    @NonCommitting
//    @OnlyForActions
//    final void unremoveForActions() throws DatabaseException {
//        AgentModule.unremoveAgent(this);
//        if (isOnHost() && this instanceof OutgoingRole) { ((OutgoingRole) this).issue(); }
//        removed = false;
//        notify(CREATED);
//    }
    
    /* -------------------------------------------------- Permissions -------------------------------------------------- */
    
    /**
     * Returns the permissions of this agent.
     * <p>
     * <em>Important:</em> The additional permissions should not cover any existing permissions. If they do,
     * make sure to {@link #removePermissions(net.digitalid.service.core.agent.ReadonlyAgentPermissions) remove} them first.
     */
    @Pure
    @GenerateProperty(requiredPermissionsToExecuteMethod = "key, value", requiredAgentToExecuteMethod = "concept", requiredAgentToSeeMethod = "concept")
    public abstract @Nonnull WritableIndexedProperty<SemanticType, Boolean, ReadOnlyAgentPermissions> permissions();
    
    /* -------------------------------------------------- Restrictions -------------------------------------------------- */
    
    /**
     * Returns the restrictions of this agent.
     * 
     * TODO: How to check that the restrictions match?
     * @ensure return.match(this) : "The restrictions match this agent.";
     */
    @Pure
    @GenerateProperty(requiredRestrictionsToExecuteMethod = "value", requiredAgentToExecuteMethod = "concept", requiredAgentToSeeMethod = "concept")
    public abstract @Nonnull WritableNonNullableProperty<Restrictions> restrictions();
    
    /* -------------------------------------------------- Abstract -------------------------------------------------- */
    
    // TODO: Do we need the following methods at all? At least one of them could be implemented. The other one if the key (e.g. even vs. uneven) determines the subclass.
    
    /**
     * Returns whether this agent is a client agent.
     */
    @Pure
    public abstract boolean isClientAgent();
    
    /**
     * Returns whether this agent is an outgoing role.
     */
    @Pure
    public abstract boolean isOutgoingRole();
    
    /* -------------------------------------------------- Matching -------------------------------------------------- */
    
    /**
     * Returns whether this agent matches the given restrictions.
     */
    @Pure
    public boolean matches(@Nonnull Restrictions restrictions) {
        return isClientAgent() == restrictions.isOnlyForClients() && (restrictions.getNode() == null || getEntity().equals(restrictions.getNode().getEntity()));
    }
    
    /* -------------------------------------------------- Weaker Agents -------------------------------------------------- */
    
    /**
     * Returns the agents that are weaker than this agent.
     */
    @Pure
    @NonCommitting
    public abstract @Capturable @Nonnull @NonFrozen FreezableList<@Nonnull Agent> getWeakerAgents() throws DatabaseException;
    
    /**
     * Returns the weaker agent with the given agent number.
     * 
     * @throws DatabaseException if no such weaker agent is found.
     */
    @Pure
    @NonCommitting
    public abstract @Nonnull Agent getWeakerAgent(long agentNumber) throws DatabaseException;
    
    /* -------------------------------------------------- Coverage -------------------------------------------------- */
    
    /**
     * Returns whether this agent covers the given agent.
     * 
     * @require getEntity().equals(agent.getEntity()) : "The given agent belongs to the same entity.";
     */
    @Pure
    @NonCommitting
    public abstract boolean covers(@Nonnull /* @Matching */ Agent agent) throws DatabaseException; /* {
        return !removed().get() && AgentModule.isStronger(this, agent);
    } */
    
    /**
     * Checks that this agent covers the given agent and throws a {@link RequestException} otherwise.
     */
    @Pure
    @NonCommitting
    public void checkCovers(@Nonnull Agent agent) throws RequestException, DatabaseException {
        if (!covers(agent)) { throw RequestException.with(RequestErrorCode.AUTHORIZATION, "The agent $ does not cover the agent $.", this, agent); }
    }
    
    /* -------------------------------------------------- Constructors -------------------------------------------------- */
    
    /**
     * Returns the agent with the given number at the given entity.
     */
    @Pure
    @Recover
    public static @Nonnull Agent of(@Nonnull NonHostEntity entity, long number) {
        // TODO: Make this injectable?
        return null;
//        return client ? ClientAgent.get(entity, number, removed) : OutgoingRole.get(entity, number, removed, false);
    }
    
    /* -------------------------------------------------- SQLizable -------------------------------------------------- */
    
    // TODO: Remove the following code once we can handle foreign key constraints automatically.
    
//    @NonCommitting
//    public static @Nonnull String getReference(@Nonnull Site site) throws DatabaseException {
//        AgentModule.createReferenceTable(site);
//        return "REFERENCES " + site + "agent (entity, agent) ON DELETE CASCADE";
//    }
    
}
