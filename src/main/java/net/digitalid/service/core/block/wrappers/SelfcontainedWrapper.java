package net.digitalid.service.core.block.wrappers;

import java.io.IOException;
import java.io.InputStream;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.digitalid.service.core.auxiliary.None;
import net.digitalid.service.core.block.Block;
import net.digitalid.service.core.block.annotations.Encoding;
import net.digitalid.service.core.block.annotations.NonEncoding;
import net.digitalid.service.core.block.wrappers.exceptions.UnexpectedEndOfFileException;
import net.digitalid.service.core.block.wrappers.exceptions.UnsupportedBlockLengthException;
import net.digitalid.service.core.exceptions.abort.AbortException;
import net.digitalid.service.core.exceptions.external.ExternalException;
import net.digitalid.service.core.exceptions.external.InvalidEncodingException;
import net.digitalid.service.core.exceptions.network.NetworkException;
import net.digitalid.service.core.exceptions.packet.PacketException;
import net.digitalid.service.core.converter.xdf.XDF;
import net.digitalid.service.core.converter.xdf.ConvertToXDF;
import net.digitalid.service.core.identifier.Identifier;
import net.digitalid.service.core.identity.Identity;
import net.digitalid.service.core.identity.SemanticType;
import net.digitalid.service.core.identity.SyntacticType;
import net.digitalid.service.core.identity.annotations.BasedOn;
import net.digitalid.service.core.identity.annotations.Loaded;
import net.digitalid.utility.annotations.math.NonNegative;
import net.digitalid.utility.annotations.state.Immutable;
import net.digitalid.utility.annotations.state.Pure;
import net.digitalid.utility.collections.annotations.elements.NonNullableElements;
import net.digitalid.utility.collections.readonly.ReadOnlyArray;
import net.digitalid.utility.database.annotations.Locked;
import net.digitalid.utility.database.annotations.NonCommitting;

/**
 * This class wraps an {@link Block element} for encoding and decoding a block of the syntactic type {@code selfcontained@core.digitalid.net}.
 */
@Immutable
public final class SelfcontainedWrapper extends BlockBasedWrapper<SelfcontainedWrapper> {
    
    /* –––––––––––––––––––––––––––––––––––––––––––––––––– Types –––––––––––––––––––––––––––––––––––––––––––––––––– */
    
    /**
     * Stores the syntactic type {@code selfcontained@core.digitalid.net}.
     */
    public static final @Nonnull SyntacticType TYPE = SyntacticType.map("selfcontained@core.digitalid.net").load(0);
    
    /**
     * Stores the semantic type {@code default@core.digitalid.net}.
     */
    public static final @Nonnull SemanticType DEFAULT = SemanticType.map("default@core.digitalid.net").load(TYPE);
    
    /**
     * Stores the semantic type {@code implementation.selfcontained@core.digitalid.net}.
     */
    private static final @Nonnull SemanticType IMPLEMENTATION = SemanticType.map("implementation.selfcontained@core.digitalid.net").load(TupleWrapper.TYPE, SemanticType.IDENTIFIER, SemanticType.UNKNOWN);
    
    @Pure
    @Override
    public @Nonnull SyntacticType getSyntacticType() {
        return TYPE;
    }
    
    /* –––––––––––––––––––––––––––––––––––––––––––––––––– Element –––––––––––––––––––––––––––––––––––––––––––––––––– */
    
    /**
     * Stores the tuple of this wrapper.
     */
    private final @Nonnull @BasedOn("implementation.selfcontained@core.digitalid.net") Block tuple;
   
    /**
     * Stores the element of this wrapper.
     */
    private final @Nonnull Block element;
    
    /**
     * Returns the element of this wrapper.
     * 
     * @return the element of this wrapper.
     */
    @Pure
    public @Nonnull Block getElement() {
        return element;
    }
    
    /* –––––––––––––––––––––––––––––––––––––––––––––––––– Constructors –––––––––––––––––––––––––––––––––––––––––––––––––– */
    
    /**
     * Creates a new selfcontained wrapper with the given type and element.
     * 
     * @param type the semantic type of the new selfcontained wrapper.
     * @param element the element of the new selfcontained wrapper.
     */
    private SelfcontainedWrapper(@Nonnull @BasedOn("selfcontained@core.digitalid.net") SemanticType type, @Nonnull @NonEncoding Block element) {
        super(type);
        
        this.tuple = TupleWrapper.encode(IMPLEMENTATION, ConvertToXDF.<Identity>nonNullable(element.getType(), SemanticType.IDENTIFIER), element);
        this.element = element;
    }
    
    /**
     * Creates a new selfcontained wrapper from the given block.
     * 
     * @param block the block that contains the identifier and the element.
     */
    @Locked
    @NonCommitting
    private SelfcontainedWrapper(@Nonnull @NonEncoding @BasedOn("selfcontained@core.digitalid.net") Block block) throws AbortException, PacketException, ExternalException, NetworkException {
        super(block.getType());
        
        this.tuple = Block.get(IMPLEMENTATION, block);
        final @Nonnull @NonNullableElements ReadOnlyArray<Block> elements = TupleWrapper.decode(tuple).getNonNullableElements(2);
        final @Nonnull Identifier identifier = Identifier.ENCODING_FACTORY.decodeNonNullable(None.OBJECT, elements.getNonNullable(0));
        this.element = elements.getNonNullable(1);
        element.setType(identifier.getIdentity().toSemanticType());
    }
    
    /* –––––––––––––––––––––––––––––––––––––––––––––––––– Utility –––––––––––––––––––––––––––––––––––––––––––––––––– */
    
    /**
     * Encodes the given element into a new non-nullable selfcontained block of the given type.
     * 
     * @param type the semantic type of the new block.
     * @param element the element to encode into the new block.
     * 
     * @return a new non-nullable selfcontained block containing the given element.
     */
    @Pure
    public static @Nonnull @NonEncoding <V extends XDF<V, Object>> Block encodeNonNullable(@Nonnull @BasedOn("selfcontained@core.digitalid.net") SemanticType type, @Nonnull V element) {
        return new EncodingFactory(type).encodeNonNullable(new SelfcontainedWrapper(type, ConvertToXDF.nonNullable(element)));
    }
    
    /**
     * Encodes the given element into a new nullable selfcontained block of the given type.
     * 
     * @param type the semantic type of the new block.
     * @param element the element to encode into the new block.
     * 
     * @return a new nullable selfcontained block containing the given element.
     */
    @Pure
    public static @Nullable @NonEncoding <V extends XDF<V, Object>> Block encodeNullable(@Nonnull @BasedOn("selfcontained@core.digitalid.net") SemanticType type, @Nullable V element) {
        return element == null ? null : encodeNonNullable(type, element);
    }
    
    /**
     * Decodes the given non-nullable block. 
     * 
     * @param block the block to be decoded.
     * 
     * @return the element contained in the given block.
     */
    @Pure
    @Locked
    @NonCommitting
    public static @Nonnull @NonEncoding Block decodeNonNullable(@Nonnull @NonEncoding @BasedOn("selfcontained@core.digitalid.net") Block block) throws AbortException, PacketException, ExternalException, NetworkException {
        return new EncodingFactory(block.getType()).decodeNonNullable(None.OBJECT, block).element;
    }
    
    /**
     * Decodes the given nullable block. 
     * 
     * @param block the block to be decoded.
     * 
     * @return the element contained in the given block.
     */
    @Pure
    @Locked
    @NonCommitting
    public static @Nullable @NonEncoding Block decodeNullable(@Nullable @NonEncoding @BasedOn("selfcontained@core.digitalid.net") Block block) throws AbortException, PacketException, ExternalException, NetworkException {
        return block == null ? null : decodeNonNullable(block);
    }
    
    /**
     * Reads, wraps and decodes a selfcontained block from the given input stream and optionally closes the input stream afterwards.
     * 
     * @param inputStream the input stream to read from.
     * @param close whether the input stream shall be closed.
     * 
     * @return the element of the selfcontained block from the given input stream.
     */
    @Pure
    @Locked
    @NonCommitting
    public static @Nonnull @NonEncoding Block decodeBlockFrom(@Nonnull InputStream inputStream, boolean close) throws AbortException, PacketException, ExternalException, NetworkException {
        try {
            return decodeNonNullable(readBlockFrom(inputStream, close));
        } catch (@Nonnull IOException exception) {
            throw NetworkException.get(exception);
        }
    }
    
    /* –––––––––––––––––––––––––––––––––––––––––––––––––– Encoding –––––––––––––––––––––––––––––––––––––––––––––––––– */
    
    @Pure
    @Override
    public int determineLength() {
        return tuple.getLength();
    }
    
    @Pure
    @Override
    public void encode(@Nonnull @Encoding Block block) {
        assert block.getLength() == determineLength() : "The block's length has to match the determined length.";
        assert block.getType().isBasedOn(getSyntacticType()) : "The block is based on the indicated syntactic type.";
        
        tuple.writeTo(block);
    }
    
    /* –––––––––––––––––––––––––––––––––––––––––––––––––– XDF –––––––––––––––––––––––––––––––––––––––––––––––––– */
    
    /**
     * The encoding factory for this class.
     */
    @Immutable
    public static final class EncodingFactory extends Wrapper.EncodingFactory<SelfcontainedWrapper> {
        
        /**
         * Creates a new encoding factory with the given type.
         * 
         * @param type the semantic type of the encoded blocks and decoded wrappers.
         */
        private EncodingFactory(@Nonnull @Loaded @BasedOn("selfcontained@core.digitalid.net") SemanticType type) {
            super(type);
        }
        
        @Pure
        @Locked
        @Override
        @NonCommitting
        public @Nonnull SelfcontainedWrapper decodeNonNullable(@Nonnull Object none, @Nonnull @NonEncoding @BasedOn("selfcontained@core.digitalid.net") Block block) throws AbortException, PacketException, ExternalException, NetworkException {
            return new SelfcontainedWrapper(block);
        }
        
    }
    
    @Pure
    @Override
    public @Nonnull EncodingFactory getXDFConverter() {
        return new EncodingFactory(getSemanticType());
    }
    
    /* –––––––––––––––––––––––––––––––––––––––––––––––––– Storable –––––––––––––––––––––––––––––––––––––––––––––––––– */
    
    @Pure
    @Override
    public @Nonnull StoringFactory<SelfcontainedWrapper> getSQLConverter() {
        return new StoringFactory<>(getXDFConverter());
    }
    
    /* –––––––––––––––––––––––––––––––––––––––––––––––––– Reading –––––––––––––––––––––––––––––––––––––––––––––––––– */
    
    /**
     * Reads a selfcontained block from the given input stream and optionally closes the input stream afterwards.
     * 
     * @param inputStream the input stream to read from.
     * @param close whether the input stream shall be closed.
     * 
     * @return the selfcontained block read from the input stream.
     * 
     * @ensure return.getType().equals(DEFAULT) : "The returned block is selfcontained.";
     */
    private static @Nonnull Block readBlockFrom(@Nonnull InputStream inputStream, boolean close) throws InvalidEncodingException, IOException {
        try {
            final @Nonnull byte[] intvarOfIdentifier = new byte[8];
            read(inputStream, intvarOfIdentifier, 0, 1);
            
            final int intvarOfIdentifierLength = IntvarWrapper.decodeLength(intvarOfIdentifier);
            read(inputStream, intvarOfIdentifier, 1, intvarOfIdentifierLength - 1);
            
            final int identifierLength = longToInt(IntvarWrapper.decodeValue(intvarOfIdentifier, intvarOfIdentifierLength));
            final @Nonnull byte[] identifier = new byte[identifierLength];
            read(inputStream, identifier, 0, identifierLength);
            
            final @Nonnull byte[] intvarOfElement = new byte[8];
            read(inputStream, intvarOfElement, 0, 1);
            
            final int intvarOfElementLength = IntvarWrapper.decodeLength(intvarOfElement);
            read(inputStream, intvarOfElement, 1, intvarOfElementLength - 1);
            
            final int elementLength = longToInt(IntvarWrapper.decodeValue(intvarOfElement, intvarOfElementLength));
            final int length = longToInt((long) intvarOfIdentifierLength + (long) identifierLength + (long) intvarOfElementLength + (long) elementLength);
            final @Nonnull byte[] bytes = new byte[length];
            
            System.arraycopy(intvarOfIdentifier, 0, bytes, 0, intvarOfIdentifierLength);
            System.arraycopy(identifier, 0, bytes, intvarOfIdentifierLength, identifierLength);
            System.arraycopy(intvarOfElement, 0, bytes, intvarOfIdentifierLength + identifierLength, intvarOfElementLength);
            read(inputStream, bytes, intvarOfIdentifierLength + identifierLength + intvarOfElementLength, elementLength);
            
            return Block.get(DEFAULT, bytes);
        } finally {
            if (close) inputStream.close();
        }
    }
    
    /**
     * Reads the given amount of bytes from the input stream and stores them into the given byte array at the given offset.
     * 
     * @param inputStream the input stream to read from.
     * @param bytes the byte array into which the input is read.
     * @param offset the offset in the byte array at which the data is written.
     * @param length the number of bytes that is read from the input stream.
     * 
     * @throws UnexpectedEndOfFileException if the end of the input stream has been reached before the indicated data could be read.
     * 
     * @require offset + length <= bytes.length : "The indicated section may not exceed the byte array.";
     */
    @SuppressWarnings("AssignmentToMethodParameter")
    private static void read(final @Nonnull InputStream inputStream, final @Nonnull byte[] bytes, @NonNegative int offset, @NonNegative int length) throws IOException {
        assert offset >= 0 && length >= 0 : "The offset and length are non-negative.";
        assert offset + length <= bytes.length : "The indicated section may not exceed the byte array.";
        
        while (length > 0) {
            final int read = inputStream.read(bytes, offset, length);
            if (read == -1) throw UnexpectedEndOfFileException.get();
            offset += read;
            length -= read;
        }
    }
    
    /**
     * Casts a long to an int and throws an exception if the value is too large.
     * 
     * @param value the value to cast.
     * 
     * @return the safely casted value.
     * 
     * @throws UnsupportedBlockLengthException if the value is larger than the maximum integer.
     */
    private static int longToInt(long value) throws UnsupportedBlockLengthException {
        if (value > (long) Integer.MAX_VALUE) throw UnsupportedBlockLengthException.get();
        return (int) value;
    }
    
}
