package net.digitalid.core.tuples;

import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.digitalid.core.annotations.Capturable;
import net.digitalid.core.annotations.Frozen;
import net.digitalid.core.annotations.NonFrozen;
import net.digitalid.core.annotations.NonFrozenRecipient;
import net.digitalid.core.annotations.NullableElements;
import net.digitalid.core.annotations.Pure;
import net.digitalid.core.collections.Freezable;
import net.digitalid.core.collections.FreezableObject;

/**
 * This class models a {@link Freezable freezable} pair.
 * 
 * @see FreezableTriplet
 * 
 * @author Kaspar Etter (kaspar.etter@digitalid.net)
 * @version 1.0
 */
public class FreezablePair<E0, E1> extends FreezableObject implements ReadOnlyPair<E0, E1> {
    
    /* –––––––––––––––––––––––––––––––––––––––––––––––––– Fields –––––––––––––––––––––––––––––––––––––––––––––––––– */
    
    /**
     * Stores the first element of this tuple.
     */
    private @Nullable E0 element0;
    
    /**
     * Stores the second element of this tuple.
     */
    private @Nullable E1 element1;
    
    /* –––––––––––––––––––––––––––––––––––––––––––––––––– Constructors –––––––––––––––––––––––––––––––––––––––––––––––––– */
    
    /**
     * Creates a new pair with the given elements.
     * 
     * @param element0 the first element of this tuple.
     * @param element1 the second element of this tuple.
     */
    protected FreezablePair(@Nullable E0 element0, @Nullable E1 element1) {
        this.element0 = element0;
        this.element1 = element1;
    }
    
    /**
     * Creates a new pair with the given elements.
     * 
     * @param element0 the first element of this tuple.
     * @param element1 the second element of this tuple.
     * 
     * @return a new pair with the given elements.
     */
    @Pure
    public static @Capturable @Nonnull @NonFrozen <E0, E1> FreezablePair<E0, E1> get(@Nullable E0 element0, @Nullable E1 element1) {
        return new FreezablePair<>(element0, element1);
    }
    
    /**
     * Creates a new pair from the given pair.
     * 
     * @param pair the pair containing the elements.
     * 
     * @return a new pair from the given pair.
     */
    @Pure
    public static @Capturable @Nonnull @NonFrozen <E0, E1> FreezablePair<E0, E1> getNonNullable(@Nonnull @NullableElements ReadOnlyPair<E0, E1> pair) {
        return get(pair.getElement0(), pair.getElement1());
    }
    
    /**
     * Creates a new pair from the given pair.
     * 
     * @param pair the pair containing the elements.
     * 
     * @return a new pair from the given pair.
     */
    @Pure
    public static @Capturable @Nullable @NonFrozen <E0, E1> FreezablePair<E0, E1> getNullable(@Nullable @NullableElements ReadOnlyPair<E0, E1> pair) {
        return pair == null ? null : getNonNullable(pair);
    }
    
    /* –––––––––––––––––––––––––––––––––––––––––––––––––– Getters –––––––––––––––––––––––––––––––––––––––––––––––––– */
    
    @Pure
    @Override
    public final @Nullable E0 getElement0() {
        return element0;
    }
    
    @Pure
    @Override
    public final @Nullable E1 getElement1() {
        return element1;
    }
    
    /* –––––––––––––––––––––––––––––––––––––––––––––––––– Setters –––––––––––––––––––––––––––––––––––––––––––––––––– */
    
    /**
     * Sets the first element of this tuple.
     * 
     * @param element0 the element to be set.
     */
    @NonFrozenRecipient
    public final void setElement0(@Nullable E0 element0) {
        assert !isFrozen() : "This object is not frozen.";
        
        this.element0 = element0;
    }
    
    /**
     * Sets the second element of this tuple.
     * 
     * @param element1 the element to be set.
     */
    @NonFrozenRecipient
    public final void setElement1(@Nullable E1 element1) {
        assert !isFrozen() : "This object is not frozen.";
        
        this.element1 = element1;
    }
    
    /* –––––––––––––––––––––––––––––––––––––––––––––––––– Freezable –––––––––––––––––––––––––––––––––––––––––––––––––– */
    
    @Override
    public @Nonnull @Frozen ReadOnlyPair<E0, E1> freeze() {
        super.freeze();
        return this;
    }
    
    /* –––––––––––––––––––––––––––––––––––––––––––––––––– Cloneable –––––––––––––––––––––––––––––––––––––––––––––––––– */
    
    @Pure
    @Override
    public @Capturable @Nonnull @NonFrozen FreezablePair<E0, E1> clone() {
        return FreezablePair.getNonNullable(this);
    }
    
    /* –––––––––––––––––––––––––––––––––––––––––––––––––– Object –––––––––––––––––––––––––––––––––––––––––––––––––– */
    
    @Pure
    @Override
    @SuppressWarnings("rawtypes")
    public boolean equals(@Nullable Object object) {
        if (object == this) return true;
        if (object == null) return false;
        if (!(object instanceof FreezablePair)) return object.equals(this);
        final @Nonnull FreezablePair other = (FreezablePair) object;
        return Objects.equals(this.element0, other.element0) && Objects.equals(this.element1, other.element1);
    }
    
    @Pure
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 83 * hash + Objects.hashCode(element0);
        hash = 83 * hash + Objects.hashCode(element1);
        return hash;
    }
    
    @Pure
    @Override
    public @Nonnull String toString() {
        return element0 + ", " + element1;
    }
    
}
