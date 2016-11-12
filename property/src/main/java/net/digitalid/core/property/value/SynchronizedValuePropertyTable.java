package net.digitalid.core.property.value;

import javax.annotation.Nonnull;

import net.digitalid.utility.annotations.method.Pure;
import net.digitalid.utility.collaboration.annotations.TODO;
import net.digitalid.utility.collaboration.enumerations.Author;
import net.digitalid.utility.generator.annotations.generators.GenerateBuilder;
import net.digitalid.utility.generator.annotations.generators.GenerateSubclass;
import net.digitalid.utility.validation.annotations.generation.Derive;
import net.digitalid.utility.validation.annotations.type.Immutable;

import net.digitalid.database.property.value.PersistentValuePropertyEntry;
import net.digitalid.database.property.value.PersistentValuePropertyEntryConverter;
import net.digitalid.database.property.value.PersistentValuePropertyTable;

import net.digitalid.core.concept.Concept;
import net.digitalid.core.entity.Entity;
import net.digitalid.core.property.SynchronizedPropertyTable;

/**
 * The synchronized value property table stores the {@link PersistentValuePropertyEntry value property entries}.
 */
@Immutable
@GenerateBuilder
@GenerateSubclass
public interface SynchronizedValuePropertyTable<E extends Entity, K, C extends Concept<E, K>, V, T> extends PersistentValuePropertyTable<C, V, T>, SynchronizedPropertyTable<E, K, C, PersistentValuePropertyEntry<C, V>, ValueRequiredAuthorization<E, K, C, V>> {
    
    /* -------------------------------------------------- Entry Converter -------------------------------------------------- */
    
    @Pure
    @Override
    @TODO(task = "Is it really necessary to override this method manually?", date = "2016-11-12", author = Author.KASPAR_ETTER)
    @Derive("net.digitalid.database.property.value.PersistentValuePropertyEntryConverterBuilder.<C, V, T>withName(getFullNameWithUnderlines()).withPropertyTable(this).build()")
    public @Nonnull PersistentValuePropertyEntryConverter<C, V, T> getEntryConverter();
    
}