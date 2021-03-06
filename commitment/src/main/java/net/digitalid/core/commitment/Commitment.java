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
package net.digitalid.core.commitment;

import java.math.BigInteger;

import javax.annotation.Nonnull;

import net.digitalid.utility.annotations.method.Pure;
import net.digitalid.utility.exceptions.ExternalException;
import net.digitalid.utility.generator.annotations.generators.GenerateBuilder;
import net.digitalid.utility.generator.annotations.generators.GenerateConverter;
import net.digitalid.utility.generator.annotations.generators.GenerateSubclass;
import net.digitalid.utility.rootclass.RootInterface;
import net.digitalid.utility.time.Time;
import net.digitalid.utility.validation.annotations.generation.Derive;
import net.digitalid.utility.validation.annotations.generation.OrderOfAssignment;
import net.digitalid.utility.validation.annotations.type.Immutable;

import net.digitalid.core.asymmetrickey.PublicKey;
import net.digitalid.core.asymmetrickey.PublicKeyRetriever;
import net.digitalid.core.group.Element;
import net.digitalid.core.group.Exponent;
import net.digitalid.core.identification.identity.HostIdentity;

/**
 * This class models the commitment of a client.
 * 
 * @see SecretCommitment
 */
@Immutable
@GenerateBuilder
@GenerateSubclass
@GenerateConverter
public interface Commitment extends RootInterface {
    
    // TODO: Remove the following code once these (or such) semantic types are generated implicitly.
    
//    /**
//     * Stores the semantic type {@code host.commitment.client@core.digitalid.net}.
//     */
//    private static final @Nonnull SemanticType HOST = SemanticType.map("host.commitment.client@core.digitalid.net").load(HostIdentity.IDENTIFIER);
//    
//    /**
//     * Stores the semantic type {@code time.commitment.client@core.digitalid.net}.
//     */
//    private static final @Nonnull SemanticType TIME = SemanticType.map("time.commitment.client@core.digitalid.net").load(Time.TYPE);
//    
//    /**
//     * Stores the semantic type {@code value.commitment.client@core.digitalid.net}.
//     */
//    private static final @Nonnull SemanticType VALUE = SemanticType.map("value.commitment.client@core.digitalid.net").load(Element.TYPE);
//    
//    /**
//     * Stores the semantic type {@code commitment.client@core.digitalid.net}.
//     */
//    public static final @Nonnull SemanticType TYPE = SemanticType.map("commitment.client@core.digitalid.net").load(TupleWrapper.XDF_TYPE, HOST, TIME, VALUE);
    
    // TODO: Remove the following code once this can be done by the generated converter.
    
//    @NonCommitting
//    public Commitment(@Nonnull Block block) throws ExternalException {
//        Require.that(block.getType().isBasedOn(TYPE)).orThrow("The block is based on the indicated type.");
//        
//        final @Nonnull ReadOnlyArray<Block> elements = TupleWrapper.decode(block).getNonNullableElements(3);
//        final @Nonnull HostIdentifier identifier = IdentifierImplementation.XDF_CONVERTER.decodeNonNullable(None.OBJECT, elements.getNonNullable(0)).castTo(HostIdentifier.class);
//        this.host = identifier.getIdentity();
//        this.time = Time.XDF_CONVERTER.decodeNonNullable(None.OBJECT, elements.getNonNullable(1));
//        this.publicKey = (Server.hasHost(identifier) ? Server.getHost(identifier).getPublicKeyChain() : Cache.getPublicKeyChain(host)).getKey(time);
//        this.value = publicKey.getCompositeGroup().getElement(IntegerWrapper.decodeNonNullable(elements.getNonNullable(2)));
//    }
//    
//    @Pure
//    @Override
//    public final @Nonnull Block toBlock() {
//        final @Nonnull FreezableArray<Block> elements = FreezableArray.get(3);
//        elements.set(0, host.toBlock(HOST));
//        elements.set(1, time.toBlock().setType(TIME));
//        elements.set(2, value.toBlock().setType(VALUE));
//        return TupleWrapper.encode(TYPE, elements.freeze());
//    }
    
    /* -------------------------------------------------- Host -------------------------------------------------- */
    
    /**
     * Returns the host at which this commitment was made.
     */
    @Pure
    public @Nonnull HostIdentity getHost();
    
    /* -------------------------------------------------- Time -------------------------------------------------- */
    
    /**
     * Returns the time at which this commitment was made.
     */
    @Pure
    public @Nonnull Time getTime();
    
    /* -------------------------------------------------- Value -------------------------------------------------- */
    
    /**
     * Returns the value of this commitment.
     */
    @Pure
    public @Nonnull BigInteger getValue();
    
    /* -------------------------------------------------- Public Key -------------------------------------------------- */
    
    /**
     * Returns the public key of this commitment.
     */
    @Pure
    // TODO: Support importing qualified names: @Derive("{net.digitalid.core.identification.PublicKeyRetriever}.retrieve(host, time)")
    // TODO: Allow to throw exceptions in derive-expressions: @Derive("net.digitalid.core.identification.PublicKeyRetriever.retrieve(host, time)")
    public @Nonnull PublicKey getPublicKey();
    
    /* -------------------------------------------------- Value -------------------------------------------------- */
    
    /**
     * Returns the value of this commitment.
     */
    @Pure
    @OrderOfAssignment(2)
    @Derive("publicKey.getCompositeGroup().getElement(value)")
    public @Nonnull Element getElement();
    
    /* -------------------------------------------------- Secret -------------------------------------------------- */
    
    /**
     * Adds the given secret to this commitment.
     */
    @Pure
    public default @Nonnull SecretCommitment addSecret(@Nonnull Exponent secret) throws ExternalException {
        return SecretCommitmentBuilder.withHost(getHost()).withTime(getTime()).withPublicKey(PublicKeyRetriever.retrieve(getHost(), getTime())).withSecret(secret).build();
    }
    
}
