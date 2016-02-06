package net.digitalid.core.conversion.wrappers.value.decimal;

import java.sql.SQLException;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.digitalid.utility.collections.freezable.FreezableArray;
import net.digitalid.utility.collections.index.MutableIndex;
import net.digitalid.utility.exceptions.external.InvalidEncodingException;
import net.digitalid.utility.exceptions.InternalException;
import net.digitalid.utility.freezable.NonFrozen;
import net.digitalid.utility.validation.annotations.reference.NonCapturable;
import net.digitalid.utility.validation.annotations.type.Immutable;
import net.digitalid.utility.validation.annotations.state.Matching;
import net.digitalid.utility.validation.annotations.method.Pure;

import net.digitalid.database.core.annotations.NonCommitting;
import net.digitalid.database.core.declaration.ColumnDeclaration;
import net.digitalid.database.core.exceptions.operation.FailedValueRestoringException;
import net.digitalid.database.core.exceptions.operation.FailedValueStoringException;
import net.digitalid.database.core.sql.statement.table.create.SQLType;

import net.digitalid.utility.conversion.None;

import net.digitalid.core.conversion.Block;

import net.digitalid.core.conversion.annotations.Encoding;
import net.digitalid.core.conversion.annotations.NonEncoding;

import net.digitalid.core.conversion.wrappers.AbstractWrapper;

import net.digitalid.core.conversion.wrappers.value.ValueWrapper;

import net.digitalid.service.core.converter.NonRequestingConverters;

import net.digitalid.core.conversion.exceptions.InvalidBlockLengthException;

import net.digitalid.service.core.identity.SemanticType;
import net.digitalid.service.core.identity.SyntacticType;
import net.digitalid.service.core.identity.annotations.BasedOn;
import net.digitalid.service.core.identity.annotations.Loaded;

/**
 * This class wraps a {@code float} for encoding and decoding a block of the syntactic type {@code float@core.digitalid.net}.
 */
@Immutable
public final class Decimal32Wrapper extends ValueWrapper<Decimal32Wrapper> {
    
    /* -------------------------------------------------- Value -------------------------------------------------- */
    
    /**
     * Stores the value of this wrapper.
     */
    private final float value;
    
    /**
     * Returns the value of this wrapper.
     * 
     * @return the value of this wrapper.
     */
    @Pure
    public float getValue() {
        return value;
    }
    
    /* -------------------------------------------------- Constructor -------------------------------------------------- */
    
    /**
     * Creates a new wrapper with the given type and value.
     * 
     * @param type the semantic type of the new wrapper.
     * @param value the value of the new wrapper.
     */
    private Decimal32Wrapper(@Nonnull @Loaded @BasedOn("float@core.digitalid.net") SemanticType type, float value) {
        super(type);
        
        this.value = value;
    }
    
    /* -------------------------------------------------- Encoding -------------------------------------------------- */
    
    /**
     * The byte length of a float.
     */
    public static final int LENGTH = 4;
    
    @Pure
    @Override
    public int determineLength() {
        return LENGTH;
    }
    
    @Pure
    @Override
    public void encode(@Nonnull @Encoding Block block) {
        Require.that(block.getLength() == determineLength()).orThrow("The block's length has to match the determined length.");
        Require.that(block.getType().isBasedOn(getSyntacticType())).orThrow("The block is based on the indicated syntactic type.");
        
        block.encodeValue(Float.floatToRawIntBits(value));
    }
    
    /* -------------------------------------------------- Syntactic Type -------------------------------------------------- */
    
    /**
     * Stores the syntactic type {@code float@core.digitalid.net}.
     */
    public static final @Nonnull SyntacticType XDF_TYPE = SyntacticType.map("float@core.digitalid.net").load(0);
    
    @Pure
    @Override
    public @Nonnull SyntacticType getSyntacticType() {
        return XDF_TYPE;
    }
    
    /* -------------------------------------------------- XDF Converter -------------------------------------------------- */
    
    /**
     * The XDF converter for this wrapper.
     */
    @Immutable
    public static final class XDFConverter extends AbstractWrapper.NonRequestingXDFConverter<Decimal32Wrapper> {
        
        /**
         * Creates a new XDF converter with the given type.
         * 
         * @param type the semantic type of the encoded blocks and decoded wrappers.
         */
        private XDFConverter(@Nonnull @BasedOn("float@core.digitalid.net") SemanticType type) {
            super(type);
        }
        
        @Pure
        @Override
        public @Nonnull Decimal32Wrapper decodeNonNullable(@Nonnull Object none, @Nonnull @NonEncoding @BasedOn("float@core.digitalid.net") Block block) throws InvalidEncodingException, InternalException {
            if (block.getLength() != LENGTH) { throw InvalidBlockLengthException.get(LENGTH, block.getLength()); }
            
            return new Decimal32Wrapper(block.getType(), Float.intBitsToFloat((int) block.decodeValue()));
        }
        
    }
    
    @Pure
    @Override
    public @Nonnull XDFConverter getXDFConverter() {
        return new XDFConverter(getSemanticType());
    }
    
    /* -------------------------------------------------- XDF Utility -------------------------------------------------- */
    
    /**
     * Stores the semantic type {@code semantic.float@core.digitalid.net}.
     */
    private static final @Nonnull SemanticType SEMANTIC = SemanticType.map("semantic.float@core.digitalid.net").load(XDF_TYPE);
    
    /**
     * Stores a static XDF converter for performance reasons.
     */
    private static final @Nonnull XDFConverter XDF_CONVERTER = new XDFConverter(SEMANTIC);
    
    /**
     * Encodes the given value into a new block of the given type.
     * 
     * @param type the semantic type of the new block.
     * @param value the value to encode into the new block.
     * 
     * @return a new block containing the given value.
     */
    @Pure
    public static @Nonnull @NonEncoding Block encode(@Nonnull @Loaded @BasedOn("float@core.digitalid.net") SemanticType type, float value) {
        return XDF_CONVERTER.encodeNonNullable(new Decimal32Wrapper(type, value));
    }
    
    /**
     * Decodes the given block. 
     * 
     * @param block the block to be decoded.
     * 
     * @return the value contained in the given block.
     */
    @Pure
    public static float decode(@Nonnull @NonEncoding @BasedOn("float@core.digitalid.net") Block block) throws InvalidEncodingException, InternalException {
        return XDF_CONVERTER.decodeNonNullable(None.OBJECT, block).value;
    }
    
    /* -------------------------------------------------- SQL Utility -------------------------------------------------- */
    
    @Pure
    @Override
    public @Nonnull String toString() {
        return String.valueOf(value);
    }
    
    /**
     * Stores the given value at the given index in the given array.
     * 
     * @param value the value which is to be stored in the values array.
     * @param values a mutable array in which the value is to be stored.
     * @param index the array index at which the value is to be stored.
     */
    public static void store(float value, @NonCapturable @Nonnull @NonFrozen FreezableArray<String> values, @Nonnull MutableIndex index) {
        values.set(index.getAndIncrementValue(), String.valueOf(value));
    }
    
    /**
     * Stores the given value at the given index in the given prepared statement.
     * 
     * @param value the value which is to be stored in the given prepared statement.
     * @param preparedStatement the prepared statement whose parameter is to be set.
     * @param parameterIndex the statement index at which the value is to be stored.
     */
    @NonCommitting
    public static void store(float value, @NonCapturable @Nonnull ValueCollector collector) throws FailedValueStoringException {
        try {
            preparedStatement.setFloat(parameterIndex.getAndIncrementValue(), value);
        } catch (@Nonnull SQLException exception) {
            throw FailedValueStoringException.get(exception);
        }
    }
    
    /**
     * Returns the value from the given column of the given result set.
     * 
     * @param resultSet the set from which the value is to be retrieved.
     * @param columnIndex the index from which the value is to be retrieved.
     * 
     * @return the value from the given column of the given result set.
     */
    @Pure
    @NonCommitting
    public static float restore(@NonCapturable @Nonnull SelectionResult result) throws FailedValueRestoringException {
        try {
            return resultSet.getFloat(columnIndex.getAndIncrementValue());
        } catch (@Nonnull SQLException exception) {
            throw FailedValueRestoringException.get(exception);
        }
    }
    
    /* -------------------------------------------------- SQL Converter -------------------------------------------------- */
    
    /**
     * Stores the SQL type of this wrapper.
     */
    public static final @Nonnull SQLType SQL_TYPE = SQLType.DECIMAL32;
    
    /**
     * The SQL converter for this wrapper.
     */
    @Immutable
    public static final class SQLConverter extends AbstractWrapper.SQLConverter<Decimal32Wrapper> {
        
        /**
         * Creates a new SQL converter with the given column declaration.
         *
         * @param declaration the declaration used to store instances of the wrapper.
         */
        private SQLConverter(@Nonnull @Matching ColumnDeclaration declaration) {
            super(declaration, SEMANTIC);
            
            Require.that(declaration.getType() == SQL_TYPE).orThrow("The declaration matches the SQL type of the wrapper.");
        }
        
        @Override
        @NonCommitting
        public void storeNonNullable(@Nonnull Decimal32Wrapper wrapper, @NonCapturable @Nonnull ValueCollector collector) throws FailedValueStoringException {
            store(wrapper.value, preparedStatement, parameterIndex);
        }
        
        @Pure
        @Override
        @NonCommitting
        public @Nullable Decimal32Wrapper restoreNullable(@Nonnull Object none, @NonCapturable @Nonnull SelectionResult result) throws FailedValueRestoringException, CorruptValueException, InternalException {
            try {
                final float value = restore(resultSet, columnIndex);
                return resultSet.wasNull() ? null : new Decimal32Wrapper(getType(), value);
            } catch (@Nonnull SQLException exception) {
                throw FailedValueRestoringException.get(exception);
            }
        }
        
    }
    
    /**
     * Stores the default declaration of this wrapper.
     */
    private static final @Nonnull ColumnDeclaration DECLARATION = ColumnDeclaration.get("value", SQL_TYPE);
    
    @Pure
    @Override
    public @Nonnull SQLConverter getSQLConverter() {
        return new SQLConverter(DECLARATION);
    }
    
    /* -------------------------------------------------- Wrapper -------------------------------------------------- */
    
    /**
     * The wrapper for this wrapper.
     */
    @Immutable
    public static class Wrapper extends ValueWrapper.Wrapper<Float, Decimal32Wrapper> {
        
        @Pure
        @Override
        protected @Nonnull Decimal32Wrapper wrap(@Nonnull SemanticType type, @Nonnull Float value) {
            return new Decimal32Wrapper(type, value);
        }
        
        @Pure
        @Override
        protected @Nonnull Float unwrap(@Nonnull Decimal32Wrapper wrapper) {
            return wrapper.value;
        }
        
    }
    
    /**
     * Stores the wrapper of this wrapper.
     */
    public static final @Nonnull Wrapper WRAPPER = new Wrapper();
    
    /* -------------------------------------------------- Value Converters -------------------------------------------------- */
    
    /**
     * Returns the value XDF converter of this wrapper.
     * 
     * @param type the semantic type of the encoded blocks.
     * 
     * @return the value XDF converter of this wrapper.
     */
    @Pure
    public static @Nonnull ValueXDFConverter<Float, Decimal32Wrapper> getValueXDFConverter(@Nonnull @BasedOn("float@core.digitalid.net") SemanticType type) {
        return new ValueXDFConverter<>(WRAPPER, new XDFConverter(type));
    }
    
    /**
     * Returns the value SQL converter of this wrapper.
     * 
     * @param declaration the declaration of the converter.
     *
     * @return the value SQL converter of this wrapper.
     */
    @Pure
    public static @Nonnull ValueSQLConverter<Float, Decimal32Wrapper> getValueSQLConverter(@Nonnull @Matching ColumnDeclaration declaration) {
        return new ValueSQLConverter<>(WRAPPER, new SQLConverter(declaration));
    }
    
    /**
     * Returns the value converters of this wrapper.
     * 
     * @param type the semantic type of the encoded blocks.
     * @param declaration the declaration of the converter.
     *
     * @return the value converters of this wrapper.
     */
    @Pure
    public static @Nonnull NonRequestingConverters<Float, Object> getValueConverters(@Nonnull @BasedOn("float@core.digitalid.net") SemanticType type, @Nonnull @Matching ColumnDeclaration declaration) {
        return NonRequestingConverters.get(getValueXDFConverter(type), getValueSQLConverter(declaration));
    }
    
}
