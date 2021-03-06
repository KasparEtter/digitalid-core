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
//import java.sql.Statement;
//
//import javax.annotation.Nonnull;
//import javax.annotation.Nullable;
//
//import net.digitalid.utility.annotations.method.Pure;
//import net.digitalid.utility.collections.freezable.FreezableList;
//import net.digitalid.utility.collections.list.FreezableLinkedList;
//import net.digitalid.utility.collections.list.ReadOnlyList;
//import net.digitalid.utility.exceptions.external.InvalidEncodingException;
//import net.digitalid.utility.validation.annotations.type.Stateless;
//
//import net.digitalid.database.annotations.transaction.NonCommitting;
//import net.digitalid.database.core.table.Site;
//import net.digitalid.database.interfaces.Database;
//
//import net.digitalid.core.agent.Agent;
//import net.digitalid.core.agent.ReadOnlyAgentPermissions;
//import net.digitalid.core.agent.Restrictions;
//import net.digitalid.core.conversion.Block;
//import net.digitalid.core.conversion.wrappers.structure.ListWrapper;
//import net.digitalid.core.conversion.wrappers.structure.TupleWrapper;
//import net.digitalid.core.entity.NonHostEntity;
//import net.digitalid.core.host.Host;
//import net.digitalid.core.identification.identity.SemanticType;
//import net.digitalid.core.service.CoreService;
//import net.digitalid.core.state.Service;
//
//import net.digitalid.service.core.dataservice.StateModule;
//
///**
// * This class provides database access to the {@link AccessRequest access requests} of the core service.
// */
//@Stateless
final class AccessModule {// implements StateModule {
//    
//    /**
//     * Stores an instance of this module.
//     */
//    static final AccessModule MODULE = new AccessModule();
//    
//    @Pure
//    @Override
//    public @Nonnull Service getService() {
//        return CoreService.SERVICE;
//    }
//    
//    @Override
//    @NonCommitting
//    public void createTables(@Nonnull Site site) throws DatabaseException {
//        try (@Nonnull Statement statement = Database.createStatement()) {
//            // TODO: Create the tables of this module.
//        }
//    }
//    
//    @Override
//    @NonCommitting
//    public void deleteTables(@Nonnull Site site) throws DatabaseException {
//        try (@Nonnull Statement statement = Database.createStatement()) {
//            // TODO: Delete the tables of this module.
//        }
//    }
//    
//    
//    /**
//     * Stores the semantic type {@code entry.access.request.module@core.digitalid.net}.
//     */
//    private static final @Nonnull SemanticType MODULE_ENTRY = SemanticType.map("entry.access.request.module@core.digitalid.net").load(TupleWrapper.XDF_TYPE, net.digitalid.core.identity.SemanticType.UNKNOWN);
//    
//    /**
//     * Stores the semantic type {@code access.request.module@core.digitalid.net}.
//     */
//    private static final @Nonnull SemanticType MODULE_FORMAT = SemanticType.map("access.request.module@core.digitalid.net").load(ListWrapper.XDF_TYPE, MODULE_ENTRY);
//    
//    @Pure
//    @Override
//    public @Nonnull SemanticType getModuleFormat() {
//        return MODULE_FORMAT;
//    }
//    
//    @Pure
//    @Override
//    @NonCommitting
//    public @Nonnull Block exportModule(@Nonnull Host host) throws DatabaseException {
//        final @Nonnull FreezableList<Block> entries = new FreezableLinkedList<>();
//        try (@Nonnull Statement statement = Database.createStatement()) {
//            // TODO: Retrieve all the entries from the database table(s).
//        }
//        return ListWrapper.encode(MODULE_FORMAT, entries.freeze());
//    }
//    
//    @Override
//    @NonCommitting
//    public void importModule(@Nonnull Host host, @Nonnull Block block) throws DatabaseException, InvalidEncodingException {
//        Require.that(block.getType().isBasedOn(getModuleFormat())).orThrow("The block is based on the format of this module.");
//        
//        final @Nonnull ReadOnlyList<Block> entries = ListWrapper.decodeNonNullableElements(block);
//        for (final @Nonnull Block entry : entries) {
//            // TODO: Add all entries to the database table(s).
//        }
//    }
//    
//    
//    /**
//     * Stores the semantic type {@code entry.access.request.state@core.digitalid.net}.
//     */
//    private static final @Nonnull SemanticType STATE_ENTRY = SemanticType.map("entry.access.request.state@core.digitalid.net").load(TupleWrapper.XDF_TYPE, net.digitalid.core.identity.SemanticType.UNKNOWN);
//    
//    /**
//     * Stores the semantic type {@code access.request.state@core.digitalid.net}.
//     */
//    private static final @Nonnull SemanticType STATE_FORMAT = SemanticType.map("access.request.state@core.digitalid.net").load(ListWrapper.XDF_TYPE, STATE_ENTRY);
//    
//    @Pure
//    @Override
//    public @Nonnull SemanticType getStateFormat() {
//        return STATE_FORMAT;
//    }
//    
//    @Pure
//    @Override
//    @NonCommitting
//    public @Nonnull Block getState(@Nonnull NonHostEntity entity, @Nonnull ReadOnlyAgentPermissions permissions, @Nonnull Restrictions restrictions, @Nullable Agent agent) throws DatabaseException {
//        final @Nonnull FreezableList<Block> entries = new FreezableLinkedList<>();
//        try (@Nonnull Statement statement = Database.createStatement()) {
//            // TODO: Retrieve the entries of the given entity from the database table(s).
//        }
//        return ListWrapper.encode(STATE_FORMAT, entries.freeze());
//    }
//    
//    @Override
//    @NonCommitting
//    public void addState(@Nonnull NonHostEntity entity, @Nonnull Block block) throws DatabaseException, InvalidEncodingException {
//        Require.that(block.getType().isBasedOn(getStateFormat())).orThrow("The block is based on the indicated type.");
//        
//        final @Nonnull ReadOnlyList<Block> entries = ListWrapper.decodeNonNullableElements(block);
//        for (final @Nonnull Block entry : entries) {
//            // TODO: Add the entries of the given entity to the database table(s).
//        }
//    }
//    
//    @Override
//    @NonCommitting
//    public void removeState(@Nonnull NonHostEntity entity) throws DatabaseException {
//        try (@Nonnull Statement statement = Database.createStatement()) {
//            // TODO: Remove the entries of the given entity from the database table(s).
//        }
//    }
//    
//    static { CoreService.SERVICE.add(MODULE); }
    
}
