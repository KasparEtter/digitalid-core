package net.digitalid.core.compression;

//package net.digitalid.core.conversion.wrappers;
//
//import java.io.ByteArrayOutputStream;
//import java.io.IOException;
//import java.util.zip.DeflaterOutputStream;
//import java.util.zip.InflaterOutputStream;
//
//import javax.annotation.Nonnull;
//import javax.annotation.Nullable;
//
//import net.digitalid.utility.conversion.None;
//import net.digitalid.utility.exceptions.InternalException;
//import net.digitalid.utility.exceptions.UnexpectedFailureException;
//import net.digitalid.utility.exceptions.external.InvalidEncodingException;
//import net.digitalid.utility.exceptions.external.MaskingInvalidEncodingException;
//import net.digitalid.utility.system.logger.Log;
//import net.digitalid.utility.annotations.method.Pure;
//import net.digitalid.utility.validation.annotations.type.Immutable;
//
//import net.digitalid.core.conversion.Block;
//import net.digitalid.core.conversion.annotations.Encoded;
//import net.digitalid.core.conversion.annotations.Encoding;
//import net.digitalid.core.conversion.annotations.NonEncoding;
//
//import net.digitalid.service.core.auxiliary.Time;
//import net.digitalid.service.core.converter.xdf.Encode;
//import net.digitalid.service.core.converter.xdf.XDF;
//import net.digitalid.service.core.identity.SemanticType;
//import net.digitalid.service.core.identity.SyntacticType;
//import net.digitalid.service.core.identity.annotations.BasedOn;
//import net.digitalid.service.core.identity.annotations.Loaded;
//
///**
// * This class wraps an {@link Block element} for encoding and decoding a block of the syntactic type {@code compression@core.digitalid.net}.
// */
//@Immutable
//public final class CompressionWrapper extends BlockBasedWrapper<CompressionWrapper> {
//    
//    /* -------------------------------------------------- Element -------------------------------------------------- */
//    
//    /**
//     * Stores the element of this wrapper.
//     * 
//     * @invariant element.getType().isBasedOn(getSemanticType().getParameters().getNotNull(0)) : "The element is based on the parameter of the semantic type.";
//     */
//    private final @Nonnull Block element;
//    
//    /**
//     * Returns the element of this wrapper.
//     * 
//     * @return the element of this wrapper.
//     * 
//     * @ensure element.getType().isBasedOn(getSemanticType().getParameters().getNotNull(0)) : "The element is based on the parameter of the semantic type.";
//     */
//    @Pure
//    public @Nonnull Block getElement() {
//        return element;
//    }
//    
//    /* -------------------------------------------------- Constructors -------------------------------------------------- */
//    
//    /**
//     * Creates a new compression wrapper with the given type and element.
//     * 
//     * @param type the semantic type of the new compression wrapper.
//     * @param element the element of the new compression wrapper.
//     * 
//     * @require element.getType().isBasedOn(type.getParameters().getNonNullable(0)) : "The element is based on the parameter of the given type.";
//     */
//    private CompressionWrapper(@Nonnull @Loaded @BasedOn("compression@core.digitalid.net") SemanticType type, @Nonnull @NonEncoding Block element) {
//        super(type);
//        
//        Require.that(element.getType().isBasedOn(type.getParameters().getNonNullable(0))).orThrow("The element is based on the parameter of the given type.");
//        
//        this.element = element;
//    }
//    
//    /* -------------------------------------------------- Encoding -------------------------------------------------- */
//    
//    /**
//     * Stores the compression of the element.
//     */
//    private @Nullable ByteArrayOutputStream cache;
//    
//    /**
//     * Returns the cached compression of the element.
//     * 
//     * @return the cached compression of the element.
//     */
//    @Pure
//    private @Nonnull ByteArrayOutputStream getCache() {
//        if (cache == null) {
//            try {
//                final @Nonnull Time start = Time.getCurrent();
//                cache = new ByteArrayOutputStream(element.getLength());
//                element.writeTo(new DeflaterOutputStream(cache), true);
//                Log.verbose("Element with " + element.getLength() + " bytes compressed in " + start.ago().getValue() + " ms.");
//            } catch (@Nonnull IOException exception) {
//                throw UnexpectedFailureException.with("The given element could not be compressed.", exception);
//            }
//        }
//        return cache;
//    }
//    
//    @Pure
//    @Override
//    public int determineLength() {
//        return getCache().size();
//    }
//    
//    @Pure
//    @Override
//    public void encode(@Nonnull @Encoding Block block) {
//        Require.that(block.getLength() == determineLength()).orThrow("The block's length has to match the determined length.");
//        Require.that(block.getType().isBasedOn(getSyntacticType())).orThrow("The block is based on the indicated syntactic type.");
//        
//        try {
//            getCache().writeTo(block.getOutputStream());
//        } catch (@Nonnull IOException exception) {
//            throw UnexpectedFailureException.with("The compressed element could not be written.", exception);
//        }
//    }
//    
//    /* -------------------------------------------------- Syntactic Type -------------------------------------------------- */
//    
//    /**
//     * Stores the syntactic type {@code compression@core.digitalid.net}.
//     */
//    public static final @Nonnull SyntacticType XDF_TYPE = SyntacticType.map("compression@core.digitalid.net").load(1);
//    
//    @Pure
//    @Override
//    public @Nonnull SyntacticType getSyntacticType() {
//        return XDF_TYPE;
//    }
//    
//    /* -------------------------------------------------- XDF Converter -------------------------------------------------- */
//    
//    /**
//     * The XDF converter for this class.
//     */
//    @Immutable
//    public static final class XDFConverter extends AbstractWrapper.XDFConverter<CompressionWrapper> {
//        
//        /**
//         * Creates a new XDF converter with the given type.
//         * 
//         * @param type the semantic type of the encoded blocks and decoded wrappers.
//         */
//        private XDFConverter(@Nonnull @Loaded @BasedOn("compression@core.digitalid.net") SemanticType type) {
//            super(type);
//        }
//        
//        @Pure
//        @Override
//        public @Nonnull CompressionWrapper decodeNonNullable(@Nonnull Object none, @Nonnull @NonEncoding @BasedOn("compression@core.digitalid.net") Block block) throws InvalidEncodingException, InternalException {
//            final @Nonnull SemanticType parameter = block.getType().getParameters().getNonNullable(0);
//            try {
//                final @Nonnull Time start = Time.getCurrent();
//                final @Nonnull ByteArrayOutputStream uncompressed = new ByteArrayOutputStream(2 * block.getLength());
//                block.writeTo(new InflaterOutputStream(uncompressed), true);
//                final @Nonnull @Encoded Block element = Block.get(parameter, uncompressed.toByteArray());
//                Log.verbose("Element with " + element.getLength() + " bytes decompressed in " + start.ago().getValue() + " ms.");
//                return new CompressionWrapper(block.getType(), element);
//            } catch (@Nonnull IOException exception) {
//                throw MaskingInvalidEncodingException.get(exception);
//            }
//        }
//        
//    }
//    
//    @Pure
//    @Override
//    public @Nonnull XDFConverter getXDFConverter() {
//        return new XDFConverter(getSemanticType());
//    }
//    
//    /* -------------------------------------------------- XDF Utility -------------------------------------------------- */
//    
//    /**
//     * Stores the semantic type {@code semantic.compression@core.digitalid.net}.
//     */
//    private static final @Nonnull SemanticType SEMANTIC = SemanticType.map("semantic.compression@core.digitalid.net").load(XDF_TYPE);
//    
//    /**
//     * Stores a static XDF converter for performance reasons.
//     */
//    private static final @Nonnull XDFConverter XDF_CONVERTER = new XDFConverter(SEMANTIC);
//
//    /**
//     * Compresses the given element into a new non-nullable block of the given type.
//     * 
//     * @param type the semantic type of the new block.
//     * @param element the element to compress into the new block.
//     * 
//     * @return a new non-nullable block containing the given element.
//     * 
//     * @require element.getFactory().getType().isBasedOn(type.getParameters().getNonNullable(0)) : "The element is based on the parameter of the given type.";
//     */
//    @Pure
//    public static @Nonnull @NonEncoding <V extends XDF<V, ?>> Block compressNonNullable(@Nonnull @Loaded @BasedOn("compression@core.digitalid.net") SemanticType type, @Nonnull V element) {
//        return XDF_CONVERTER.encodeNonNullable(new CompressionWrapper(type, Encode.nonNullable(element)));
//    }
//    
//    /**
//     * Compresses the given element into a new nullable block of the given type.
//     * 
//     * @param type the semantic type of the new block.
//     * @param element the element to compress into the new block.
//     * 
//     * @return a new nullable block containing the given element.
//     * 
//     * @require element.getFactory().getType().isBasedOn(type.getParameters().getNonNullable(0)) : "The element is based on the parameter of the given type.";
//     */
//    @Pure
//    public static @Nullable @NonEncoding <V extends XDF<V, ?>> Block compressNullable(@Nonnull @Loaded @BasedOn("compression@core.digitalid.net") SemanticType type, @Nullable V element) {
//        return element == null ? null : compressNonNullable(type, element);
//    }
//    
//    /**
//     * Decompresses the given non-nullable block. 
//     * 
//     * @param block the block to be decompressed.
//     * 
//     * @return the element contained in the given block.
//     */
//    @Pure
//    public static @Nonnull @NonEncoding Block decompressNonNullable(@Nonnull @NonEncoding @BasedOn("compression@core.digitalid.net") Block block) throws InvalidEncodingException, InternalException {
//        return XDF_CONVERTER.decodeNonNullable(None.OBJECT, block).element;
//    }
//    
//    /**
//     * Decompresses the given nullable block. 
//     * 
//     * @param block the block to be decompressed.
//     * 
//     * @return the element contained in the given block.
//     */
//    @Pure
//    public static @Nullable @NonEncoding Block decompressNullable(@Nullable @NonEncoding @BasedOn("compression@core.digitalid.net") Block block) throws InvalidEncodingException, InternalException {
//        return block == null ? null : decompressNonNullable(block);
//    }
//    
//    /* -------------------------------------------------- SQL Converter -------------------------------------------------- */
//    
//    @Pure
//    @Override
//    public @Nonnull SQLConverter<CompressionWrapper> getSQLConverter() {
//        return new SQLConverter<>(getXDFConverter());
//    }
//    
//}