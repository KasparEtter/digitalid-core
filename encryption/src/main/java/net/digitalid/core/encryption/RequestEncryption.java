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
package net.digitalid.core.encryption;

import javax.annotation.Nonnull;

import net.digitalid.utility.annotations.generics.Unspecifiable;
import net.digitalid.utility.annotations.method.Pure;
import net.digitalid.utility.collaboration.annotations.TODO;
import net.digitalid.utility.collaboration.enumerations.Author;
import net.digitalid.utility.exceptions.ExternalException;
import net.digitalid.utility.generator.annotations.generators.GenerateBuilder;
import net.digitalid.utility.generator.annotations.generators.GenerateSubclass;
import net.digitalid.utility.time.Time;
import net.digitalid.utility.validation.annotations.generation.Default;
import net.digitalid.utility.validation.annotations.generation.Derive;
import net.digitalid.utility.validation.annotations.math.Positive;
import net.digitalid.utility.validation.annotations.type.Immutable;

import net.digitalid.core.asymmetrickey.PrivateKey;
import net.digitalid.core.asymmetrickey.PublicKey;
import net.digitalid.core.identification.identifier.HostIdentifier;
import net.digitalid.core.symmetrickey.InitializationVector;
import net.digitalid.core.symmetrickey.SymmetricKey;

/**
 * This class encrypts the wrapped object as a request for encoding and decrypts it for decoding.
 */
@Immutable
@GenerateBuilder
@GenerateSubclass
public abstract class RequestEncryption<@Unspecifiable OBJECT> extends Encryption<OBJECT> {
    
    /* -------------------------------------------------- Recipient -------------------------------------------------- */
    
    @Pure
    @Override
    public abstract @Nonnull HostIdentifier getRecipient();
    
    /* -------------------------------------------------- Time -------------------------------------------------- */
    
    /**
     * The time at which the object has been or will be encrypted.
     * This information is required to retrieve the appropriate
     * {@link PublicKey public} and {@link PrivateKey private key}
     * from the host's key chain.
     */
    @Pure
    @Default("net.digitalid.utility.time.TimeBuilder.build()")
    public abstract @Nonnull @Positive Time getTime();
    
    /* -------------------------------------------------- Symmetric Key -------------------------------------------------- */
    
    /**
     * Returns the symmetric key that has been or will be used to encrypt the object.
     */
    @Pure
    @Default("net.digitalid.core.symmetrickey.SymmetricKeyBuilder.build()")
    public abstract @Nonnull SymmetricKey getSymmetricKey();
    
    /* -------------------------------------------------- Initialization Vector -------------------------------------------------- */
    
    /**
     * Returns the initialization vector of the symmetric encryption scheme (AES).
     */
    @Pure
    @Default("net.digitalid.core.symmetrickey.InitializationVectorBuilder.build()")
    public abstract @Nonnull InitializationVector getInitializationVector();
    
    /* -------------------------------------------------- Public Key -------------------------------------------------- */
    
    /**
     * Returns the public key of the recipient.
     */
    @Pure
    @Derive("net.digitalid.core.asymmetrickey.PublicKeyRetriever.retrieve(recipient, time)")
    public abstract @Nonnull PublicKey getPublicKey();
    
    /* -------------------------------------------------- Constructor -------------------------------------------------- */
    
    @TODO(task = "Remove this constructor as soon as we can declare exceptions in derive expressions.", date = "2017-01-28", author = Author.KASPAR_ETTER)
    protected RequestEncryption() throws ExternalException {}
    
}
