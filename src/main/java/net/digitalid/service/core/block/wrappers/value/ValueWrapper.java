package net.digitalid.service.core.block.wrappers.value;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.digitalid.database.core.annotations.Locked;
import net.digitalid.database.core.annotations.NonCommitting;
import net.digitalid.database.core.converter.AbstractSQLConverter;
import net.digitalid.database.core.exceptions.operation.FailedValueRestoringException;
import net.digitalid.database.core.exceptions.operation.FailedValueStoringException;
import net.digitalid.database.core.exceptions.state.CorruptStateException;
import net.digitalid.service.core.block.Block;
import net.digitalid.service.core.block.annotations.NonEncoding;
import net.digitalid.service.core.block.wrappers.AbstractWrapper;
import net.digitalid.service.core.converter.xdf.AbstractNonRequestingXDFConverter;
import net.digitalid.service.core.exceptions.external.encoding.InvalidEncodingException;
import net.digitalid.service.core.identity.SemanticType;
import net.digitalid.service.core.identity.annotations.Loaded;
import net.digitalid.utility.annotations.reference.NonCapturable;
import net.digitalid.utility.annotations.state.Immutable;
import net.digitalid.utility.annotations.state.Pure;
import net.digitalid.utility.annotations.state.Validated;
import net.digitalid.utility.collections.annotations.freezable.NonFrozen;
import net.digitalid.utility.collections.freezable.FreezableArray;
import net.digitalid.utility.collections.index.MutableIndex;
import net.digitalid.utility.system.exceptions.InternalException;

/**
 * A value wrapper wraps a primitive value.
 */
@Immutable
public abstract class ValueWrapper<W extends ValueWrapper<W>> extends AbstractWrapper<W> {
    
    /* -------------------------------------------------- Constructor -------------------------------------------------- */
    
    /**
     * Creates a new value wrapper with the given semantic type.
     * 
     * @param semanticType the semantic type of the new wrapper.
     * 
     * @require semanticType.isBasedOn(getSyntacticType()) : "The given semantic type is based on the indicated syntactic type.";
     */
    protected ValueWrapper(@Nonnull @Loaded SemanticType semanticType) {
        super(semanticType);
    }
    
    /* -------------------------------------------------- XDF Converter -------------------------------------------------- */
    
    @Pure
    @Override
    public abstract @Nonnull AbstractWrapper.NonRequestingXDFConverter<W> getXDFConverter();
    
    /* -------------------------------------------------- Wrapper -------------------------------------------------- */
    
    /**
     * The wrapper for value wrappers.
     */
    @Immutable
    public abstract static class Wrapper<V, W extends ValueWrapper<W>> {
        
        /**
         * Returns whether the given value is valid.
         * 
         * @param value the value to check.
         * 
         * @return whether the given value is valid.
         */
        @Pure
        protected boolean isValid(@Nonnull V value) {
            return true;
        }
        
        /**
         * Wraps the given value.
         * 
         * @param type the type of the wrapper.
         * @param value the value to wrap.
         * 
         * @return the wrapper around the value.
         */
        @Pure
        protected abstract @Nonnull W wrap(@Nonnull SemanticType type, @Nonnull @Validated V value);
        
        /**
         * Unwraps the given wrapper.
         * 
         * @param wrapper the wrapper to unwrap.
         * 
         * @return the value wrapped in the given wrapper.
         */
        @Pure
        protected abstract @Nonnull @Validated V unwrap(@Nonnull W wrapper);
        
    }
    
    /* -------------------------------------------------- Value XDF Converter -------------------------------------------------- */
    
    /**
     * The XDF converter for encoding and decoding values.
     */
    @Immutable
    public final static class ValueXDFConverter<V, W extends ValueWrapper<W>> extends AbstractNonRequestingXDFConverter<V, Object> {
        
        /**
         * Stores the wrapper to wrap and unwrap the values.
         */
        private final @Nonnull Wrapper<V, W> wrapper;
        
        /**
         * Stores the XDF converter to encode and decode the wrapped values.
         */
        private final @Nonnull NonRequestingXDFConverter<W> XDFConverter;
        
        /**
         * Creates a new value XDF converter with the given parameters.
         * 
         * @param wrapper the wrapper that allows to wrap and unwrap the values.
         * @param XDFConverter the XDF converter that allows to encode and decode the wrapped values.
         */
        protected ValueXDFConverter(@Nonnull Wrapper<V, W> wrapper, @Nonnull NonRequestingXDFConverter<W> XDFConverter) {
            super(XDFConverter.getType());
            
            this.wrapper = wrapper;
            this.XDFConverter = XDFConverter;
        }
        
        @Pure
        @Override
        public final @Nonnull @NonEncoding Block encodeNonNullable(@Nonnull V value) {
            return XDFConverter.encodeNonNullable(wrapper.wrap(XDFConverter.getType(), value));
        }
        
        @Pure
        @Locked
        @Override
        @NonCommitting
        public final @Nonnull V decodeNonNullable(@Nonnull Object none, @Nonnull @NonEncoding Block block) throws InvalidEncodingException, InternalException {
            assert block.getType().isBasedOn(getType()) : "The block is based on the type of this converter.";
            
            return wrapper.unwrap(XDFConverter.decodeNonNullable(none, block));
        }
        
    }
    
    /* -------------------------------------------------- Value SQL Converter -------------------------------------------------- */
    
    /**
     * The SQL converter for storing and restoring values.
     */
    @Immutable
    public final static class ValueSQLConverter<V, W extends ValueWrapper<W>> extends AbstractSQLConverter<V, Object> {
        
        /**
         * Stores the wrapper to wrap and unwrap the values.
         */
        private final @Nonnull Wrapper<V, W> wrapper;
        
        /**
         * Stores the SQL converter to store and restore the wrapped values.
         */
        private final @Nonnull SQLConverter<W> SQLConverter;
        
        /**
         * Creates a new SQL converter with the given parameters.
         * 
         * @param wrapper the wrapper that allows to wrap and unwrap the values.
         * @param SQLConverter the SQL converter that allows to store and restore the wrapped values.
         */
        protected ValueSQLConverter(@Nonnull Wrapper<V, W> wrapper, @Nonnull SQLConverter<W> SQLConverter) {
            super(SQLConverter.getDeclaration());
            
            this.wrapper = wrapper;
            this.SQLConverter = SQLConverter;
        }
        
        @Pure
        @Override
        public final void storeNonNullable(@Nonnull V value, @NonCapturable @Nonnull @NonFrozen FreezableArray<String> values, @Nonnull MutableIndex index) {
            values.set(index.getAndIncrementValue(), wrapper.wrap(SQLConverter.getType(), value).toString());
        }
        
        @Override
        @NonCommitting
        public final void storeNonNullable(@Nonnull V value, @Nonnull PreparedStatement preparedStatement, @Nonnull MutableIndex parameterIndex) throws FailedValueStoringException {
            SQLConverter.storeNonNullable(wrapper.wrap(SQLConverter.getType(), value), preparedStatement, parameterIndex);
        }
        
        @Pure
        @Override
        @NonCommitting
        public final @Nullable V restoreNullable(@Nonnull Object none, @Nonnull ResultSet resultSet, @Nonnull MutableIndex columnIndex) throws FailedValueRestoringException, CorruptStateException, InternalException {
            final @Nullable W wrapper = SQLConverter.restoreNullable(none, resultSet, columnIndex);
            return wrapper == null ? null : this.wrapper.unwrap(wrapper);
        }
        
    }
    
}
