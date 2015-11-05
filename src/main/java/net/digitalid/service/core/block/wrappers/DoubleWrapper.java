package net.digitalid.service.core.block.wrappers;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.digitalid.service.core.auxiliary.None;
import net.digitalid.service.core.block.Block;
import net.digitalid.service.core.block.annotations.Encoding;
import net.digitalid.service.core.block.annotations.NonEncoding;
import net.digitalid.service.core.block.wrappers.ValueWrapper.ValueXDFConverter;
import net.digitalid.service.core.block.wrappers.ValueWrapper.ValueSQLConverter;
import net.digitalid.service.core.exceptions.external.InvalidEncodingException;
import net.digitalid.service.core.converter.Converters;
import net.digitalid.service.core.identity.SemanticType;
import net.digitalid.service.core.identity.SyntacticType;
import net.digitalid.service.core.identity.annotations.BasedOn;
import net.digitalid.service.core.identity.annotations.Loaded;
import net.digitalid.utility.annotations.state.Immutable;
import net.digitalid.utility.annotations.state.Pure;
import net.digitalid.utility.database.annotations.NonCommitting;
import net.digitalid.utility.database.column.Column;
import net.digitalid.utility.database.column.SQLType;

/**
 * This class wraps a {@code double} for encoding and decoding a block of the syntactic type {@code double@core.digitalid.net}.
 */
@Immutable
public final class DoubleWrapper extends Wrapper<DoubleWrapper> {
    
    /* –––––––––––––––––––––––––––––––––––––––––––––––––– Types –––––––––––––––––––––––––––––––––––––––––––––––––– */
    
    /**
     * Stores the syntactic type {@code double@core.digitalid.net}.
     */
    public static final @Nonnull SyntacticType TYPE = SyntacticType.map("double@core.digitalid.net").load(0);
    
    @Pure
    @Override
    public @Nonnull SyntacticType getSyntacticType() {
        return TYPE;
    }
    
    /* –––––––––––––––––––––––––––––––––––––––––––––––––– Value –––––––––––––––––––––––––––––––––––––––––––––––––– */
    
    /**
     * Stores the value of this wrapper.
     */
    private final double value;
    
    /**
     * Returns the value of this wrapper.
     * 
     * @return the value of this wrapper.
     */
    @Pure
    public double getValue() {
        return value;
    }
    
    /* –––––––––––––––––––––––––––––––––––––––––––––––––– Constructor –––––––––––––––––––––––––––––––––––––––––––––––––– */
    
    /**
     * Creates a new wrapper with the given type and value.
     * 
     * @param type the semantic type of the new wrapper.
     * @param value the value of the new wrapper.
     */
    private DoubleWrapper(@Nonnull @Loaded @BasedOn("double@core.digitalid.net") SemanticType type, double value) {
        super(type);
        
        this.value = value;
    }
    
    /* –––––––––––––––––––––––––––––––––––––––––––––––––– Utility –––––––––––––––––––––––––––––––––––––––––––––––––– */
    
    /**
     * Encodes the given value into a new block of the given type.
     * 
     * @param type the semantic type of the new block.
     * @param value the value to encode into the new block.
     * 
     * @return a new block containing the given value.
     */
    @Pure
    public static @Nonnull @NonEncoding Block encode(@Nonnull @Loaded @BasedOn("double@core.digitalid.net") SemanticType type, double value) {
        return new XDFConverter(type).encodeNonNullable(new DoubleWrapper(type, value));
    }
    
    /**
     * Decodes the given block. 
     * 
     * @param block the block to be decoded.
     * 
     * @return the value contained in the given block.
     */
    @Pure
    public static double decode(@Nonnull @NonEncoding @BasedOn("double@core.digitalid.net") Block block) throws InvalidEncodingException {
        return new XDFConverter(block.getType()).decodeNonNullable(None.OBJECT, block).value;
    }
    
    /* –––––––––––––––––––––––––––––––––––––––––––––––––– Encoding –––––––––––––––––––––––––––––––––––––––––––––––––– */
    
    /**
     * The byte length of a double.
     */
    public static final int LENGTH = 8;
    
    @Pure
    @Override
    public int determineLength() {
        return LENGTH;
    }
    
    @Pure
    @Override
    public void encode(@Nonnull @Encoding Block block) {
        assert block.getLength() == determineLength() : "The block's length has to match the determined length.";
        assert block.getType().isBasedOn(getSyntacticType()) : "The block is based on the indicated syntactic type.";
        
        block.encodeValue(Double.doubleToRawLongBits(value));
    }
    
    /* –––––––––––––––––––––––––––––––––––––––––––––––––– Encodable –––––––––––––––––––––––––––––––––––––––––––––––––– */
    
    /**
     * The XDF converter for this class.
     */
    @Immutable
    public static final class XDFConverter extends Wrapper.NonRequestingXDFConverter<DoubleWrapper> {
        
        /**
         * Creates a new XDF converter with the given type.
         * 
         * @param type the semantic type of the encoded blocks and decoded wrappers.
         */
        private XDFConverter(@Nonnull @BasedOn("double@core.digitalid.net") SemanticType type) {
            super(type);
        }
        
        @Pure
        @Override
        public @Nonnull DoubleWrapper decodeNonNullable(@Nonnull Object none, @Nonnull @NonEncoding @BasedOn("double@core.digitalid.net") Block block) throws InvalidEncodingException {
            if (block.getLength() != LENGTH) throw new InvalidEncodingException("The block's length is invalid.");
            
            return new DoubleWrapper(block.getType(), Double.longBitsToDouble(block.decodeValue()));
        }
        
    }
    
    @Pure
    @Override
    public @Nonnull XDFConverter getXDFConverter() {
        return new XDFConverter(getSemanticType());
    }
    
    /* –––––––––––––––––––––––––––––––––––––––––––––––––– Storable –––––––––––––––––––––––––––––––––––––––––––––––––– */
    
    /**
     * The SQL converter for this class.
     */
    @Immutable
    public static final class SQLConverter extends Wrapper.SQLConverter<DoubleWrapper> {
        
        /**
         * Stores the column for the wrapper.
         */
        private static final @Nonnull Column COLUMN = Column.get("value", SQLType.DOUBLE);
        
        /**
         * Creates a new factory with the given type.
         * 
         * @param type the semantic type of the wrapper.
         */
        private SQLConverter(@Nonnull @Loaded @BasedOn("double@core.digitalid.net") SemanticType type) {
            super(COLUMN, type);
        }
        
        @Override
        @NonCommitting
        public void storeNonNullable(@Nonnull DoubleWrapper wrapper, @Nonnull PreparedStatement preparedStatement, int parameterIndex) throws SQLException {
            preparedStatement.setDouble(parameterIndex, wrapper.value);
        }
        
        @Pure
        @Override
        @NonCommitting
        public @Nullable DoubleWrapper restoreNullable(@Nonnull Object none, @Nonnull ResultSet resultSet, int columnIndex) throws SQLException {
            final double value = resultSet.getDouble(columnIndex);
            if (resultSet.wasNull()) return null;
            else return new DoubleWrapper(getType(), value);
        }
        
    }
    
    @Pure
    @Override
    public @Nonnull SQLConverter getSQLConverter() {
        return new SQLConverter(getSemanticType());
    }
    
    @Pure
    @Override
    public @Nonnull String toString() {
        return String.valueOf(value);
    }
    
    /* –––––––––––––––––––––––––––––––––––––––––––––––––– Factory –––––––––––––––––––––––––––––––––––––––––––––––––– */
    
    /**
     * The factory for this wrapper.
     */
    @Immutable
    public static class Factory extends ValueWrapper.Factory<Double, DoubleWrapper> {
        
        @Pure
        @Override
        protected @Nonnull DoubleWrapper wrap(@Nonnull SemanticType type, @Nonnull Double value) {
            return new DoubleWrapper(type, value);
        }
        
        @Pure
        @Override
        protected @Nonnull Double unwrap(@Nonnull DoubleWrapper wrapper) {
            return wrapper.value;
        }
        
    }
    
    /**
     * Stores the factory of this class.
     */
    private static final @Nonnull Factory FACTORY = new Factory();
    
    /* –––––––––––––––––––––––––––––––––––––––––––––––––– Value Converters –––––––––––––––––––––––––––––––––––––––––––––––––– */
    
    /**
     * Returns the value XDF converter of this wrapper.
     * 
     * @param type the semantic type of the encoded blocks.
     * 
     * @return the value XDF converter of this wrapper.
     */
    @Pure
    public static @Nonnull ValueXDFConverter<Double, DoubleWrapper> getValueXDFConverter(@Nonnull @BasedOn("double@core.digitalid.net") SemanticType type) {
        return new ValueXDFConverter<>(FACTORY, new XDFConverter(type));
    }
    
    /**
     * Returns the value SQL converter of this wrapper.
     * 
     * @param type any semantic type that is based on the syntactic type of this wrapper.
     * 
     * @return the value SQL converter of this wrapper.
     */
    @Pure
    public static @Nonnull ValueSQLConverter<Double, DoubleWrapper> getValueSQLConverter(@Nonnull @BasedOn("double@core.digitalid.net") SemanticType type) {
        return new ValueSQLConverter<>(FACTORY, new SQLConverter(type));
    }
    
    /**
     * Returns the value converters of this wrapper.
     * 
     * @param type the semantic type of the encoded blocks.
     * 
     * @return the value converters of this wrapper.
     */
    @Pure
    public static @Nonnull Converters<Double, Object> getValueConverters(@Nonnull @BasedOn("double@core.digitalid.net") SemanticType type) {
        return Converters.get(getValueXDFConverter(type), getValueSQLConverter(type));
    }
    
}
