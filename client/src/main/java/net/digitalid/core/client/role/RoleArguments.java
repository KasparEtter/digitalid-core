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
package net.digitalid.core.client.role;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.digitalid.utility.annotations.method.CallSuper;
import net.digitalid.utility.annotations.method.Pure;
import net.digitalid.utility.collaboration.annotations.TODO;
import net.digitalid.utility.collaboration.enumerations.Author;
import net.digitalid.utility.contracts.Validate;
import net.digitalid.utility.generator.annotations.generators.GenerateBuilder;
import net.digitalid.utility.generator.annotations.generators.GenerateConverter;
import net.digitalid.utility.generator.annotations.generators.GenerateSubclass;
import net.digitalid.utility.rootclass.RootClass;
import net.digitalid.utility.validation.annotations.type.Immutable;

import net.digitalid.core.client.Client;
import net.digitalid.core.identification.annotations.RoleType;
import net.digitalid.core.identification.identity.InternalNonHostIdentity;
import net.digitalid.core.identification.identity.SemanticType;

/**
 * This type models the arguments of the {@link Role} type.
 * 
 * @invariant (getRelation() == null) == (getRecipient() == null) : "The relation and the recipient are either both null or non-null.";
 * 
 * @see RoleEntry
 */
@Immutable
@GenerateBuilder
@GenerateSubclass
@GenerateConverter
public abstract class RoleArguments extends RootClass {
    
    /* -------------------------------------------------- Fields -------------------------------------------------- */
    
    @Pure
    public abstract @Nonnull Client getClient();
    
    @Pure
    public abstract @Nonnull InternalNonHostIdentity getIssuer();
    
    @Pure
    public abstract @Nullable @RoleType SemanticType getRelation();
    
    @Pure
    @TODO(task = "Make sure this generates a foreign key constraint on the table itself (instead on a non-existent role table).", date = "2017-03-21", author = Author.KASPAR_ETTER)
    public abstract @Nullable /* Role */ Long getRecipient();
    
    @Pure
    public abstract long getAgentKey();
    
    /* -------------------------------------------------- Validation -------------------------------------------------- */
    
    @Pure
    @Override
    @CallSuper
    public void validate() {
        super.validate();
        
        Validate.that((getRelation() == null) == (getRecipient() == null)).orThrow("The relation and the recipient have to be either both null or non-null but were $ and $.", getRelation(), getRecipient());
    }
    
}
