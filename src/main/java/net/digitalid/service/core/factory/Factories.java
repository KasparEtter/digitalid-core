package net.digitalid.service.core.factory;

import javax.annotation.Nonnull;
import net.digitalid.service.core.entity.Entity;
import net.digitalid.service.core.factory.encoding.AbstractEncodingFactory;
import net.digitalid.service.core.identity.SemanticType;
import net.digitalid.utility.annotations.state.Immutable;
import net.digitalid.utility.annotations.state.Pure;
import net.digitalid.utility.database.storing.AbstractStoringFactory;

/**
 * This class allows to store several factories in a single object.
 * 
 * @param <O> the type of the objects that the factories can convert, which is typically the surrounding class.
 * @param <E> the type of the external object that is needed to reconstruct an object, which is quite often an {@link Entity}.
 *            In case no external information is needed for the reconstruction of an object, declare it as an {@link Object}.
 * 
 * @see NonRequestingFactories
 */
@Immutable
public class Factories<O, E> {
    
    /* –––––––––––––––––––––––––––––––––––––––––––––––––– Encoding Factory –––––––––––––––––––––––––––––––––––––––––––––––––– */
    
    /**
     * Stores the encoding factory.
     */
    private final @Nonnull AbstractEncodingFactory<O, E> encodingFactory;
    
    /**
     * Returns the encoding factory.
     * 
     * @return the encoding factory.
     */
    @Pure
    public @Nonnull AbstractEncodingFactory<O, E> getEncodingFactory() {
        return encodingFactory;
    }
    
    /* –––––––––––––––––––––––––––––––––––––––––––––––––– Storing Factory –––––––––––––––––––––––––––––––––––––––––––––––––– */
    
    /**
     * Stores the storing factory.
     */
    private final @Nonnull AbstractStoringFactory<O, E> storingFactory;
    
    /**
     * Returns the storing factory.
     * 
     * @return the storing factory.
     */
    @Pure
    public final @Nonnull AbstractStoringFactory<O, E> getStoringFactory() {
        return storingFactory;
    }
    
    /* –––––––––––––––––––––––––––––––––––––––––––––––––– Constructor –––––––––––––––––––––––––––––––––––––––––––––––––– */
    
    /**
     * Creates a new object with the given factories.
     * 
     * @param encodingFactory the encoding factory.
     * @param storingFactory the storing factory.
     */
    protected Factories(@Nonnull AbstractEncodingFactory<O, E> encodingFactory, @Nonnull AbstractStoringFactory<O, E> storingFactory) {
        this.encodingFactory = encodingFactory;
        this.storingFactory = storingFactory;
    }
    
    /**
     * Creates a new object with the given factories.
     * 
     * @param encodingFactory the encoding factory.
     * @param storingFactory the storing factory.
     * 
     * @return a new object with the given factories.
     */
    @Pure
    public static @Nonnull <O, E> Factories<O, E> get(@Nonnull AbstractEncodingFactory<O, E> encodingFactory, @Nonnull AbstractStoringFactory<O, E> storingFactory) {
        return new Factories<>(encodingFactory, storingFactory);
    }
    
    /* –––––––––––––––––––––––––––––––––––––––––––––––––– Subtyping –––––––––––––––––––––––––––––––––––––––––––––––––– */
    
    /**
     * Returns factories with the encoding factory subtyping to the given type.
     * 
     * @return factories with the encoding factory subtyping to the given type.
     * 
     * @require type.isBasedOn(getEncodingFactory().getType()) : "The given type is based on the type of the encoding factory.";
     */
    @Pure
    public @Nonnull Factories<O, E> setType(@Nonnull SemanticType type) {
        return new Factories<>(getEncodingFactory().setType(type), getStoringFactory());
    }
    
}
