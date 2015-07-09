package net.digitalid.core.wrappers;

import java.math.BigInteger;
import javax.annotation.Nonnull;
import net.digitalid.core.annotations.Exposed;
import net.digitalid.core.annotations.Immutable;
import net.digitalid.core.annotations.Pure;
import net.digitalid.core.cryptography.Parameters;
import net.digitalid.core.exceptions.external.InvalidEncodingException;
import net.digitalid.core.identity.SemanticType;
import net.digitalid.core.identity.SyntacticType;

/**
 * Wraps a block with the syntactic type {@code hash@core.digitalid.net} for encoding and decoding.
 * 
 * @author Kaspar Etter (kaspar.etter@digitalid.net)
 * @version 1.0
 */
@Immutable
public final class HashWrapper extends BlockWrapper {
    
    /**
     * Stores the syntactic type {@code hash@core.digitalid.net}.
     */
    public static final @Nonnull SyntacticType TYPE = SyntacticType.create("hash@core.digitalid.net").load(0);
    
    /**
     * The byte length of a hash.
     */
    public static final int LENGTH = Parameters.HASH / 8;
    
    
    /**
     * Stores the value of this wrapper.
     * 
     * @invariant value.signum() >= 0 : "The value is positive.";
     * @invariant value.bitLength() <= Parameters.HASH : "The length of the value is at most Parameters.HASH.";
     */
    private final @Nonnull BigInteger value;
    
    /**
     * Encodes the given value into a new block of the given type.
     * 
     * @param type the semantic type of the new block.
     * @param value the value to encode into the new block.
     * 
     * @require type.isLoaded() : "The type declaration is loaded.";
     * @require type.isBasedOn(TYPE) : "The given type is based on the indicated syntactic type.";
     * @require value.signum() >= 0 : "The value is positive.";
     * @require value.bitLength() <= Parameters.HASH : "The length of the value may be at most Parameters.HASH.";
     */
    public HashWrapper(@Nonnull SemanticType type, @Nonnull BigInteger value) {
        super(type);
        
        assert value.signum() >= 0 : "The value is positive.";
        assert value.bitLength() <= Parameters.HASH : "The length of the value may be at most Parameters.HASH.";
        
        this.value = value;
    }
    
    /**
     * Wraps and decodes the given block.
     * 
     * @param block the block to wrap and decode.
     * 
     * @require block.getType().isBasedOn(TYPE) : "The block is based on the indicated syntactic type.";
     */
    public HashWrapper(@Nonnull Block block) throws InvalidEncodingException {
        super(block);
        
        if (block.getLength() != LENGTH) throw new InvalidEncodingException("The block's length is invalid.");
        
        this.value = new BigInteger(1, block.getBytes());
        
        if (value.signum() < 0) throw new InvalidEncodingException("The value is positive.");
        if (value.bitLength() > Parameters.HASH) throw new InvalidEncodingException("The length of the value may be at most Parameters.HASH.");
    }
    
    /**
     * Returns the value of the wrapped block.
     * 
     * @return the value of the wrapped block.
     * 
     * @ensure value.signum() >= 0 : "The value is positive.";
     * @ensure value.bitLength() <= Parameters.HASH : "The length of the value is at most Parameters.HASH.";
     */
    @Pure
    public @Nonnull BigInteger getValue() {
        return value;
    }
    
    
    @Pure
    @Override
    public @Nonnull SyntacticType getSyntacticType() {
        return TYPE;
    }
    
    @Pure
    @Override
    protected int determineLength() {
        return LENGTH;
    }
    
    @Pure
    @Override
    protected void encode(@Exposed @Nonnull Block block) {
        assert block.isEncoding() : "The given block is in the process of being encoded.";
        assert block.getType().isBasedOn(getSyntacticType()) : "The block is based on the indicated syntactic type.";
        assert block.getLength() == determineLength() : "The block's length has to match the determined length.";
        
        final @Nonnull byte[] bytes = value.toByteArray();
        final int offset = bytes.length > LENGTH ? 1 : 0;
        block.setBytes(LENGTH - bytes.length + offset, bytes, offset, bytes.length - offset);
    }
    
}
