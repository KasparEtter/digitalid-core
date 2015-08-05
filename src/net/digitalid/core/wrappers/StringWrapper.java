package net.digitalid.core.wrappers;

import java.nio.charset.Charset;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.digitalid.core.annotations.BasedOn;
import net.digitalid.core.annotations.Encoding;
import net.digitalid.core.annotations.Immutable;
import net.digitalid.core.annotations.Loaded;
import net.digitalid.core.annotations.NonEncoding;
import net.digitalid.core.annotations.Pure;
import net.digitalid.core.database.Column;
import net.digitalid.core.database.SQLType;
import net.digitalid.core.exceptions.external.InvalidEncodingException;
import net.digitalid.core.identity.SemanticType;
import net.digitalid.core.identity.SyntacticType;
import net.digitalid.core.io.Log;

/**
 * Wraps values of the syntactic type {@code string@core.digitalid.net} for encoding and decoding.
 * <p>
 * <em>Important:</em> SQL injections have to be prevented by the caller of this class!
 * Only a warning is issued when the character might be used in an unprepared SQL statement.
 * 
 * @author Kaspar Etter (kaspar.etter@digitalid.net)
 * @version 1.0
 */
@Immutable
public final class StringWrapper extends Wrapper<StringWrapper> {
    
    /* –––––––––––––––––––––––––––––––––––––––––––––––––– Types –––––––––––––––––––––––––––––––––––––––––––––––––– */
    
    /**
     * Stores the syntactic type {@code string@core.digitalid.net}.
     */
    public static final @Nonnull SyntacticType TYPE = SyntacticType.map("string@core.digitalid.net").load(0);
    
    /**
     * Stores the semantic type {@code semantic.string@core.digitalid.net}.
     */
    private static final @Nonnull SemanticType SEMANTIC = SemanticType.map("semantic.string@core.digitalid.net").load(TYPE);
    
    @Pure
    @Override
    public @Nonnull SyntacticType getSyntacticType() {
        return TYPE;
    }
    
    /* –––––––––––––––––––––––––––––––––––––––––––––––––– Value –––––––––––––––––––––––––––––––––––––––––––––––––– */
    
    /**
     * Stores the value as a byte array.
     */
    private final @Nonnull byte[] bytes;
    
    /**
     * Stores the value of this wrapper.
     */
    private final @Nonnull String value;
    
    /**
     * Returns the value of this wrapper.
     * 
     * @return the value of this wrapper.
     */
    @Pure
    public @Nonnull String getValue() {
        return value;
    }
    
    /* –––––––––––––––––––––––––––––––––––––––––––––––––– Constructor –––––––––––––––––––––––––––––––––––––––––––––––––– */
    
    /**
     * Stores the character set used to encode and decode strings.
     */
    public static final @Nonnull Charset CHARSET = Charset.forName("UTF-16BE");
    
    /**
     * Creates a new wrapper with the given type and bytes.
     * 
     * @param type the semantic type of the new wrapper.
     * @param bytes the value as a byte array.
     */
    private StringWrapper(@Nonnull @Loaded @BasedOn("string@core.digitalid.net") SemanticType type, @Nonnull byte[] bytes) {
        super(type);
        
        this.bytes = bytes;
        this.value = new String(bytes, 0, bytes.length, CHARSET);
    }
    
    /**
     * Creates a new wrapper with the given type and value.
     * 
     * @param type the semantic type of the new wrapper.
     * @param value the value of the new wrapper.
     */
    private StringWrapper(@Nonnull @Loaded @BasedOn("string@core.digitalid.net") SemanticType type, @Nonnull String value) {
        super(type);
        
        this.bytes = value.getBytes(CHARSET);
        this.value = value;
    }
    
    /* –––––––––––––––––––––––––––––––––––––––––––––––––– Utility –––––––––––––––––––––––––––––––––––––––––––––––––– */
    
    /**
     * Stores the factory of this class.
     */
    private static final Wrapper.Factory<StringWrapper> FACTORY = new Factory(SEMANTIC);
    
    /**
     * Encodes the given value into a new block of the given type.
     * 
     * @param type the semantic type of the new block.
     * @param value the value to encode into the new block.
     * 
     * @return a new block containing the given value.
     */
    @Pure
    public static @Nonnull @NonEncoding Block encode(@Nonnull @Loaded @BasedOn("string@core.digitalid.net") SemanticType type, @Nonnull String value) {
        return FACTORY.encodeNonNullable(new StringWrapper(type, value));
    }
    
    /**
     * Decodes the given block. 
     * 
     * @param block the block to be decoded.
     * 
     * @return the value contained in the given block.
     */
    @Pure
    public static @Nonnull String decode(@Nonnull @NonEncoding @BasedOn("string@core.digitalid.net") Block block) throws InvalidEncodingException {
        return FACTORY.decodeNonNullable(block).value;
    }
    
    /* –––––––––––––––––––––––––––––––––––––––––––––––––– Encoding –––––––––––––––––––––––––––––––––––––––––––––––––– */
    
    @Pure
    @Override
    protected int determineLength() {
        return bytes.length + 1;
    }
    
    @Pure
    @Override
    protected void encode(@Encoding @Nonnull Block block) {
        assert block.getLength() == determineLength() : "The block's length has to match the determined length.";
        assert block.getType().isBasedOn(getSyntacticType()) : "The block is based on the indicated syntactic type.";
        
        block.setBytes(1, bytes);
    }
    
    /* –––––––––––––––––––––––––––––––––––––––––––––––––– Storable –––––––––––––––––––––––––––––––––––––––––––––––––– */
    
    /**
     * The factory for this class.
     */
    private static class Factory extends Wrapper.Factory<StringWrapper> {
        
        /**
         * Stores the column for the wrapper.
         */
        private static final @Nonnull Column COLUMN = Column.get("value", SQLType.STRING);
        
        /**
         * Creates a new factory with the given type.
         * 
         * @param type the semantic type that corresponds to the wrapper.
         */
        protected Factory(@Nonnull @Loaded @BasedOn("string@core.digitalid.net") SemanticType type) {
            super(type, COLUMN);
            
            assert type.isBasedOn(TYPE) : "The given semantic type is based on the indicated syntactic type.";
        }
        
        @Pure
        @Override
        public @Nonnull StringWrapper decodeNonNullable(@Nonnull @NonEncoding Block block) throws InvalidEncodingException {
            final @Nonnull byte[] bytes = block.getBytes(1);
            return new StringWrapper(block.getType(), bytes);
        }
        
        @Override
        public void setNonNullable(@Nonnull StringWrapper wrapper, @Nonnull PreparedStatement preparedStatement, int parameterIndex) throws SQLException {
            preparedStatement.setString(parameterIndex, wrapper.value);
        }
        
        @Pure
        @Override
        public @Nullable StringWrapper getNullable(@Nonnull ResultSet resultSet, int columnIndex) throws SQLException {
            final @Nullable String value = resultSet.getString(columnIndex);
            return value == null ? null : new StringWrapper(getType(), value);
        }
        
    }
    
    @Pure
    @Override
    public @Nonnull Wrapper.Factory<StringWrapper> getFactory() {
        return new Factory(getSemanticType());
    }
    
    @Pure
    @Override
    public @Nonnull String toString() {
        Log.warning("The string '" + value + "' might be used in an unprepared SQL statement and might cause an injection.", new Exception());
        return "'" + value + "'";
    }
    
    /* –––––––––––––––––––––––––––––––––––––––––––––––––– Factory –––––––––––––––––––––––––––––––––––––––––––––––––– */
    
    /**
     * The factory for the value type of this wrapper.
     */
    public static class ValueFactory extends Wrapper.ValueFactory<String, StringWrapper> {
        
        /**
         * Creates a new factory with the given type.
         * 
         * @param type the semantic type that corresponds to the wrapper.
         */
        protected ValueFactory(@Nonnull @Loaded @BasedOn("string@core.digitalid.net") SemanticType type) {
            super(type, FACTORY);
            
            assert type.isBasedOn(TYPE) : "The given semantic type is based on the indicated syntactic type.";
        }
        
        @Pure
        @Override
        protected @Nonnull StringWrapper wrap(@Nonnull String value) {
            return new StringWrapper(getType(), value);
        }
        
        @Pure
        @Override
        protected @Nonnull String unwrap(@Nonnull StringWrapper wrapper) {
            return wrapper.value;
        }
        
    }
    
    /**
     * Stores the factory for the value type of this wrapper.
     */
    public static final @Nonnull ValueFactory VALUE_FACTORY = new ValueFactory(SEMANTIC);
    
}
