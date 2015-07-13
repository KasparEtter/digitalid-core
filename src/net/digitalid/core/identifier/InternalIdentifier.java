package net.digitalid.core.identifier;

import java.io.IOException;
import java.sql.SQLException;
import java.util.regex.Pattern;
import javax.annotation.Nonnull;
import net.digitalid.core.annotations.Immutable;
import net.digitalid.core.annotations.NonCommitting;
import net.digitalid.core.annotations.Pure;
import net.digitalid.core.exceptions.external.ExternalException;
import net.digitalid.core.exceptions.external.IdentityNotFoundException;
import net.digitalid.core.exceptions.packet.PacketException;
import net.digitalid.core.identity.InternalIdentity;
import net.digitalid.core.identity.Mapper;

/**
 * This class models internal identifiers.
 * 
 * @see HostIdentifier
 * @see InternalNonHostIdentifier
 * 
 * @author Kaspar Etter (kaspar.etter@digitalid.net)
 * @version 1.0
 */
@Immutable
public abstract class InternalIdentifier extends IdentifierClass {
    
    /**
     * The pattern that valid internal identifiers have to match.
     */
    private static final Pattern PATTERN = Pattern.compile("(?:(?:[a-z0-9]+(?:[._-][a-z0-9]+)*)?@)?[a-z0-9]+(?:[.-][a-z0-9]+)*\\.[a-z][a-z]+");
    
    /**
     * Returns whether the given string conforms to the criteria of this class.
     *
     * @param string the string to check.
     * 
     * @return whether the given string conforms to the criteria of this class.
     */
    @Pure
    static boolean isConforming(@Nonnull String string) {
        return IdentifierClass.isConforming(string) && PATTERN.matcher(string).matches() && string.length() - string.indexOf("@") < 40;
    }
    
    /**
     * Returns whether the given string is a valid internal identifier.
     *
     * @param string the string to check.
     * 
     * @return whether the given string is a valid internal identifier.
     */
    @Pure
    public static boolean isValid(@Nonnull String string) {
        return string.contains("@") ? InternalNonHostIdentifier.isValid(string) : HostIdentifier.isValid(string);
    }
    
    /**
     * Returns a new internal identifier with the given string.
     * 
     * @param string the string of the new internal identifier.
     * 
     * @return a new internal identifier with the given string.
     * 
     * @require isValid(string) : "The string is a valid internal identifier.";
     */
    @Pure
    public static @Nonnull InternalIdentifier create(@Nonnull String string) {
        assert isValid(string) : "The string is a valid internal identifier.";
        
        return string.contains("@") ? new InternalNonHostIdentifier(string) : new HostIdentifier(string);
    }
    
    
    /**
     * Creates an internal identifier with the given string.
     * 
     * @param string the string of the internal identifier.
     * 
     * @require isValid(string) : "The string is a valid internal identifier.";
     */
    InternalIdentifier(@Nonnull String string) {
        super(string);
        
        assert isValid(string) : "The string is a valid internal identifier.";
    }
    
    
    @Pure
    @Override
    @NonCommitting
    public abstract @Nonnull InternalIdentity getMappedIdentity() throws SQLException;
    
    @Pure
    @Override
    @NonCommitting
    public abstract @Nonnull InternalIdentity getIdentity() throws SQLException, IOException, PacketException, ExternalException;
    
    
    /**
     * Returns whether an identity with this internal identifier exists.
     * 
     * @return whether an identity with this internal identifier exists.
     */
    @Pure
    @NonCommitting
    public final boolean exists() throws SQLException, IOException, PacketException, ExternalException {
        try {
            Mapper.getIdentity(this);
            return true;
        } catch (@Nonnull IdentityNotFoundException exception) {
            return false;
        }
    }
    
    
    /**
     * Returns the host part of this internal identifier.
     * 
     * @return the host part of this internal identifier.
     */
    @Pure
    public abstract @Nonnull HostIdentifier getHostIdentifier();
    
}
