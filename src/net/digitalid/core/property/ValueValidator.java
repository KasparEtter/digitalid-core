package net.digitalid.core.property;

import javax.annotation.Nonnull;
import net.digitalid.core.annotations.Pure;

/**
 * A value validator checks whether a value is valid.
 * 
 * @author Kaspar Etter (kaspar.etter@digitalid.net)
 * @version 1.0
 */
public interface ValueValidator<V> {
    
    public final static ValueValidator<Object> DEFAULT = new ValueValidator<Object>() {
        @Pure
        @Override
        public boolean isValid(@Nonnull Object value) {
            return true;
        }
    };
    
    @Pure
    public boolean isValid(@Nonnull V value);
    
}
