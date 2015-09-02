package net.digitalid.core.property;

import java.sql.SQLException;
import javax.annotation.Nonnull;
import net.digitalid.core.annotations.Pure;
import net.digitalid.core.auxiliary.Time;
import net.digitalid.core.concept.Concept;
import net.digitalid.core.property.extensible.ExtensibleConceptProperty;
import net.digitalid.core.property.indexed.IndexedConceptProperty;
import net.digitalid.core.property.nonnullable.NonNullableConceptProperty;
import net.digitalid.core.property.nullable.NullableConceptProperty;

/**
 * This interface is implemented by all {@link ReadOnlyProperty properties} that belong to a {@link Concept concept}.
 * 
 * @see NullableConceptProperty
 * @see NonNullableConceptProperty
 * @see ExtensibleConceptProperty
 * @see IndexedConceptProperty
 * 
 * @author Kaspar Etter (kaspar.etter@digitalid.net)
 * @version 1.0
 */
public interface ConceptProperty<C extends Concept<C>> {
    
    /**
     * Returns the concept of this property.
     * 
     * @return the concept of this property.
     */
    @Pure
    public @Nonnull C getConcept();
    
    /**
     * Returns the time of the last modification.
     * 
     * @return the time of the last modification.
     */
    @Pure
    public @Nonnull Time getTime() throws SQLException;
    
}
