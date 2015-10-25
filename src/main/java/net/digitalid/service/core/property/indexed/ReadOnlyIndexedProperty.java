package net.digitalid.service.core.property.indexed;

import javax.annotation.Nonnull;
import net.digitalid.utility.collections.readonly.ReadOnlyCollection;
import net.digitalid.utility.collections.readonly.ReadOnlyMap;

/**
 * Description.
 */
public abstract interface ReadOnlyIndexedProperty<K, V, R extends ReadOnlyMap<K, V>> {
    
    public abstract @Nonnull V get(@Nonnull K key);
    
    public abstract @Nonnull ReadOnlyCollection<V> getAll();
    
    public abstract @Nonnull R getMap();
    
}
