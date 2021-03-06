/*
 * Copyright (C) 2017 Synacts GmbH, Switzerland (info@synacts.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.digitalid.core.node;

// TODO (and maybe move to another project)

//package net.digitalid.core.contact;
//
//import java.sql.SQLException;
//
//import javax.annotation.Nonnull;
//import javax.annotation.Nullable;
//
//import net.digitalid.utility.annotations.method.Pure;
//import net.digitalid.utility.exceptions.ExternalException;
//import net.digitalid.utility.validation.annotations.type.Immutable;
//
//import net.digitalid.database.annotations.transaction.NonCommitting;
//import net.digitalid.database.core.exceptions.DatabaseException;
//
//import net.digitalid.core.agent.ReadOnlyAgentPermissions;
//import net.digitalid.core.agent.Restrictions;
//import net.digitalid.core.conversion.Block;
//import net.digitalid.core.conversion.wrappers.signature.ClientSignatureWrapper;
//import net.digitalid.core.conversion.wrappers.signature.CredentialsSignatureWrapper;
//import net.digitalid.core.conversion.wrappers.signature.SignatureWrapper;
//import net.digitalid.core.entity.Entity;
//import net.digitalid.core.entity.NonHostEntity;
//import net.digitalid.core.handler.Method;
//import net.digitalid.core.handler.Reply;
//import net.digitalid.core.identification.identifier.HostIdentifier;
//import net.digitalid.core.identification.identity.InternalPerson;
//import net.digitalid.core.identification.identity.SemanticType;
//import net.digitalid.core.packet.exceptions.RequestErrorCode;
//import net.digitalid.core.packet.exceptions.RequestException;
//import net.digitalid.core.service.handler.CoreServiceActionReply;
//import net.digitalid.core.service.handler.CoreServiceExternalAction;
//
//import net.digitalid.service.core.dataservice.StateModule;
//import net.digitalid.service.core.exceptions.external.encoding.InvalidParameterValueException;
//
///**
// * Requests the given permissions of the given subject.
// */
//@Immutable
public final class AccessRequest {// extends CoreServiceExternalAction {
//    
//    /**
//     * Stores the semantic type {@code request.access@core.digitalid.net}.
//     */
//    public static final @Nonnull SemanticType TYPE = SemanticType.map("request.access@core.digitalid.net").load(FreezableNodePermissions.TYPE);
//    
//    
//    /**
//     * Stores the person of this access request.
//     */
//    private final @Nonnull InternalPerson person;
//    
//    /**
//     * Stores the permissions of this access request.
//     * 
//     * @invariant permissions.isFrozen() : "The permissions are frozen.";
//     * @invariant !permissions.isEmpty() : "The permissions are not empty.";
//     */
//    private final @Nonnull ReadOnlyNodePermissions permissions;
//    
//    /**
//     * Creates an external action to request the given permissions of the given subject.
//     * 
//     * @param entity the entity to which this handler belongs.
//     * @param subject the subject of this handler.
//     * @param permissions the requested permissions.
//     * 
//     * @require !(entity instanceof Account) || canBeSentByHosts() : "Methods encoded on hosts can be sent by hosts.";
//     * @require !(entity instanceof Role) || !canOnlyBeSentByHosts() : "Methods encoded on clients cannot only be sent by hosts.";
//     * 
//     * @require permissions.isFrozen() : "The permissions are frozen.";
//     * @require !permissions.isEmpty() : "The permissions are not empty.";
//     */
//    AccessRequest(@Nonnull NonHostEntity entity, @Nonnull InternalPerson subject, @Nonnull ReadOnlyNodePermissions permissions) {
//        super(entity, subject);
//        
//        Require.that(permissions.isFrozen()).orThrow("The permissions are frozen.");
//        Require.that(!permissions.isEmpty()).orThrow("The permissions are not empty.");
//        
//        this.person = subject;
//        this.permissions = permissions;
//    }
//    
//    /**
//     * Creates an external action that decodes the given block.
//     * 
//     * @param entity the entity to which this handler belongs.
//     * @param signature the signature of this handler (or a dummy that just contains a subject).
//     * @param recipient the recipient of this method.
//     * @param block the content which is to be decoded.
//     * 
//     * @require signature.hasSubject() : "The signature has a subject.";
//     * @require block.getType().isBasedOn(TYPE) : "The block is based on the indicated type.";
//     * 
//     * @ensure hasSignature() : "This handler has a signature.";
//     */
//    @NonCommitting
//    private AccessRequest(@Nonnull Entity entity, @Nonnull SignatureWrapper signature, @Nonnull HostIdentifier recipient, @Nonnull Block block) throws ExternalException {
//        super(entity, signature, recipient);
//        
//        this.person = entity.getIdentity().castTo(InternalPerson.class);
//        this.permissions = new FreezableNodePermissions(block).freeze();
//        if (permissions.isEmpty()) { throw InvalidParameterValueException.get("contact permissions", permissions); }
//    }
//    
//    @Pure
//    @Override
//    public @Nonnull Block toBlock() {
//        return permissions.toBlock().setType(TYPE);
//    }
//    
//    @Pure
//    @Override
//    public @Nonnull String getDescription() {
//        return "Requests access to " + permissions + ".";
//    }
//    
//    
//    /**
//     * Returns the permissions of this access request.
//     * 
//     * @return the permissions of this access request.
//     * 
//     * @ensure return.isFrozen() : "The permissions are frozen.";
//     * @ensure !return.isEmpty() : "The permissions are not empty.";
//     */
//    public @Nonnull ReadOnlyNodePermissions getPermissions() {
//        return permissions;
//    }
//    
//    
//    @Pure
//    @Override
//    public boolean canOnlyBeSentByHosts() {
//        return false;
//    }
//    
//    @Pure
//    @Override
//    public @Nonnull ReadOnlyAgentPermissions getRequiredPermissionsToExecuteMethod() {
//        return permissions.toAgentPermissions().freeze();
//    }
//    
//    @Pure
//    @Override
//    public @Nonnull Restrictions getRequiredRestrictionsToSeeAudit() {
//        return new Restrictions(false, false, true);
//    }
//    
//    @Pure
//    @Override
//    public @Nonnull Restrictions getFailedAuditRestrictions() {
//        return new Restrictions(false, false, true, Contact.get(getNonHostEntity(), person));
//    }
//    
//    
//    @Override
//    @NonCommitting
//    public @Nullable CoreServiceActionReply executeOnHost() throws RequestException, SQLException {
//        final @Nonnull SignatureWrapper signature = getSignatureNotNull();
//        if (signature instanceof CredentialsSignatureWrapper) {
//            ((CredentialsSignatureWrapper) signature).checkCover(getRequiredPermissionsToExecuteMethod());
//        } else if (signature instanceof ClientSignatureWrapper) {
//            throw RequestException.get(RequestErrorCode.AUTHORIZATION, "Access requests may not be signed by clients.");
//        }
//        executeOnClient();
//        return null;
//    }
//    
//    @Pure
//    @Override
//    public boolean matches(@Nullable Reply reply) {
//        return reply == null;
//    }
//    
//    
//    @Override
//    @NonCommitting
//    public void executeOnClient() throws DatabaseException {
//        // TODO: Add this access request to a list of pending access requests.
//    }
//    
//    @Override
//    @NonCommitting
//    public void executeOnFailure() throws DatabaseException {
//        // TODO: Add this access request to a list of failed access requests.
//    }
//    
//    
//    @Pure
//    @Override
//    public boolean equals(@Nullable Object object) {
//        return protectedEquals(object) && object instanceof AccessRequest && this.permissions.equals(((AccessRequest) object).permissions);
//    }
//    
//    @Pure
//    @Override
//    public int hashCode() {
//        return 89 * protectedHashCode() + permissions.hashCode();
//    }
//    
//    
//    @Pure
//    @Override
//    public @Nonnull SemanticType getType() {
//        return TYPE;
//    }
//    
//    @Pure
//    @Override
//    public @Nonnull StateModule getModule() {
//        return AccessModule.MODULE;
//    }
//    
//    /**
//     * The factory class for the surrounding method.
//     */
//    private static final class Factory extends Method.Factory {
//        
//        static { Method.add(TYPE, new Factory()); }
//        
//        @Pure
//        @Override
//        @NonCommitting
//        protected @Nonnull Method create(@Nonnull Entity entity, @Nonnull SignatureWrapper signature, @Nonnull HostIdentifier recipient, @Nonnull Block block) throws ExternalException {
//            return new AccessRequest(entity, signature, recipient, block);
//        }
//        
//    }
    
}
