package net.digitalid.core.identifier;

import java.io.IOException;
import java.sql.SQLException;
import javax.annotation.Nonnull;
import net.digitalid.core.annotations.Immutable;
import net.digitalid.core.annotations.NonCommitting;
import net.digitalid.core.annotations.Pure;
import net.digitalid.core.exceptions.external.ExternalException;
import net.digitalid.core.exceptions.external.InvalidEncodingException;
import net.digitalid.core.exceptions.packet.PacketException;
import net.digitalid.core.identity.Identity;
import net.digitalid.core.interfaces.Blockable;
import net.digitalid.core.interfaces.SQLizable;

/**
 * This interface models identifiers.
 * 
 * @see IdentifierClass
 * @see NonHostIdentifier
 * 
 * @author Kaspar Etter (kaspar.etter@digitalid.net)
 * @version 1.0
 */
@Immutable
public interface Identifier extends Blockable, SQLizable {
    
    /**
     * Returns the string of this identifier.
     * 
     * @return the string of this identifier.
     * 
     * @ensure IdentifierClass.isValid(string) : "The returned string is a valid identifier.";
     */
    @Pure
    public @Nonnull String getString();
    
    
    /**
     * Returns whether this identifier is mapped.
     * 
     * @return whether this identifier is mapped.
     */
    @Pure
    @NonCommitting
    public boolean isMapped() throws SQLException;
    
    /**
     * Returns whether this identifier is not mapped.
     * 
     * @return whether this identifier is not mapped.
     */
    @Pure
    @NonCommitting
    public boolean isNotMapped() throws SQLException;
    
    /**
     * Returns the mapped identity of this identifier.
     * 
     * @return the mapped identity of this identifier.
     * 
     * @require isMapped() : "This identifier is mapped.";
     */
    @Pure
    @NonCommitting
    public @Nonnull Identity getMappedIdentity() throws SQLException;
    
    /**
     * Returns the identity of this identifier.
     * 
     * @return the identity of this identifier.
     * 
     * @ensure !(result instanceof Type) || ((Type) result).isLoaded() : "If the result is a type, its declaration is loaded.";
     */
    @Pure
    @NonCommitting
    public @Nonnull Identity getIdentity() throws SQLException, IOException, PacketException, ExternalException;
    
    
    /**
     * Returns this identifier as a {@link NonHostIdentifier}.
     * 
     * @return this identifier as a {@link NonHostIdentifier}.
     * 
     * @throws InvalidEncodingException if this identifier is not an instance of {@link NonHostIdentifier}.
     */
    @Pure
    public @Nonnull NonHostIdentifier toNonHostIdentifier() throws InvalidEncodingException;
    
    
    /**
     * Returns this identifier as an {@link InternalIdentifier}.
     * 
     * @return this identifier as an {@link InternalIdentifier}.
     * 
     * @throws InvalidEncodingException if this identifier is not an instance of {@link InternalIdentifier}.
     */
    @Pure
    public @Nonnull InternalIdentifier toInternalIdentifier() throws InvalidEncodingException;
    
    /**
     * Returns this identifier as a {@link HostIdentifier}.
     * 
     * @return this identifier as a {@link HostIdentifier}.
     * 
     * @throws InvalidEncodingException if this identifier is not an instance of {@link HostIdentifier}.
     */
    @Pure
    public @Nonnull HostIdentifier toHostIdentifier() throws InvalidEncodingException;
    
    /**
     * Returns this identifier as a {@link InternalNonHostIdentifier}.
     * 
     * @return this identifier as a {@link InternalNonHostIdentifier}.
     * 
     * @throws InvalidEncodingException if this identifier is not an instance of {@link InternalNonHostIdentifier}.
     */
    @Pure
    public @Nonnull InternalNonHostIdentifier toInternalNonHostIdentifier() throws InvalidEncodingException;
    
    
    /**
     * Returns this identifier as an {@link ExternalIdentifier}.
     * 
     * @return this identifier as an {@link ExternalIdentifier}.
     * 
     * @throws InvalidEncodingException if this identifier is not an instance of {@link ExternalIdentifier}.
     */
    @Pure
    public @Nonnull ExternalIdentifier toExternalIdentifier() throws InvalidEncodingException;
    
    /**
     * Returns this identifier as an {@link EmailIdentifier}.
     * 
     * @return this identifier as an {@link EmailIdentifier}.
     * 
     * @throws InvalidEncodingException if this identifier is not an instance of {@link EmailIdentifier}.
     */
    @Pure
    public @Nonnull EmailIdentifier toEmailIdentifier() throws InvalidEncodingException;
    
    /**
     * Returns this identifier as a {@link MobileIdentifier}.
     * 
     * @return this identifier as a {@link MobileIdentifier}.
     * 
     * @throws InvalidEncodingException if this identifier is not an instance of {@link MobileIdentifier}.
     */
    @Pure
    public @Nonnull MobileIdentifier toMobileIdentifier() throws InvalidEncodingException;
    
}
