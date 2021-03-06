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
package net.digitalid.core.cache.attributes;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.digitalid.utility.annotations.method.Pure;
import net.digitalid.utility.collections.list.FreezableList;
import net.digitalid.utility.collections.list.ReadOnlyList;
import net.digitalid.utility.generator.annotations.generators.GenerateBuilder;
import net.digitalid.utility.generator.annotations.generators.GenerateConverter;
import net.digitalid.utility.generator.annotations.generators.GenerateSubclass;
import net.digitalid.utility.validation.annotations.elements.NullableElements;
import net.digitalid.utility.validation.annotations.size.NonEmpty;
import net.digitalid.utility.validation.annotations.type.Immutable;
import net.digitalid.utility.validation.annotations.value.Valid;

import net.digitalid.core.entity.Entity;
import net.digitalid.core.handler.CoreHandler;
import net.digitalid.core.handler.method.Method;
import net.digitalid.core.handler.reply.QueryReply;
import net.digitalid.core.signature.attribute.AttributeValue;

/**
 * Replies the queried attribute values of the given subject that are accessible by the requester.
 * 
 * @see AttributesQuery
 */
@Immutable
@GenerateBuilder
@GenerateSubclass
@GenerateConverter
public abstract class AttributesReply extends QueryReply<Entity> implements CoreHandler<Entity> {
    
    /* -------------------------------------------------- Validity -------------------------------------------------- */
    
    /**
     * Returns whether all the attribute values which are not null are verified.
     */
    @Pure
    static boolean isValid(@Nonnull ReadOnlyList<AttributeValue> attributeValues) {
        for (final @Nullable AttributeValue attribute : attributeValues) {
            if (attribute != null && !attribute.isVerified()) { return false; }
        }
        return true;
    }
    
    /* -------------------------------------------------- Fields -------------------------------------------------- */
    
    /**
     * Returns the attribute values of this reply.
     */
    @Pure
    public abstract @Nonnull @NullableElements /* TODO: @Frozen */ @NonEmpty @Valid /* TODO: ReadOnly */ FreezableList<AttributeValue> getAttributeValues();
    
    // TODO: Do the following checks on the recipient.
    
//    /**
//     * Creates an attribute reply that decodes a packet with the given signature for the given entity.
//     * 
//     * @param entity the entity to which this handler belongs.
//     * @param signature the host signature of this handler.
//     * @param number the number that references this reply.
//     * @param block the content which is to be decoded.
//     * 
//     * @ensure hasSignature() : "This handler has a signature.";
//     * @ensure !isOnHost() : "Query replies are never decoded on hosts.";
//     */
//    @NonCommitting
//    private AttributesReply(@Nullable NonHostEntity entity, @Nonnull HostSignatureWrapper signature, long number, @Nonnull Block block) throws ExternalException {
//        super(entity, signature, number);
//        
//        final @Nonnull InternalIdentity subject = getSubject().getIdentity();
//        final @Nonnull ReadOnlyList<Block> elements = ListWrapper.decodeNullableElements(block);
//        final @Nonnull FreezableList<AttributeValue> attributeValues = FreezableArrayList.getWithCapacity(elements.size());
//        final @Nonnull Time time = Time.getCurrent();
//        for (final @Nullable Block element : elements) {
//            if (element != null) {
//                final @Nonnull AttributeValue attributeValue = AttributeValue.get(element, false);
//                try {
//                    attributeValue.verify();
//                    if (attributeValue.isCertified()) {
//                        final @Nonnull CertifiedAttributeValue certifiedAttributeValue = attributeValue.castTo(CertifiedAttributeValue.class);
//                        certifiedAttributeValue.checkSubject(subject);
//                        certifiedAttributeValue.checkIsValid(time);
//                    }
//                    attributeValues.add(attributeValue);
//                } catch (@Nonnull InvalidSignatureException exception) {
//                    attributeValues.add(new UncertifiedAttributeValue(attributeValue.getContent()));
//                }
//            } else {
//                attributeValues.add(null);
//            }
//        }
//        if (attributeValues.isEmpty()) { throw InvalidParameterValueException.get("attribute values", attributeValues); }
//        this.attributeValues = attributeValues.freeze();
//    }
    
    /* -------------------------------------------------- Matching -------------------------------------------------- */
    
    @Pure
    @Override
    public boolean matches(@Nonnull Method<Entity> method) {
        return method instanceof AttributesQuery;
    }
    
}
