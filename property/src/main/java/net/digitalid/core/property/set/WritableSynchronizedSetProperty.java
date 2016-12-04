package net.digitalid.core.property.set;

import javax.annotation.Nonnull;

import net.digitalid.utility.annotations.method.Impure;
import net.digitalid.utility.annotations.method.Pure;
import net.digitalid.utility.annotations.ownership.Captured;
import net.digitalid.utility.annotations.ownership.NonCaptured;
import net.digitalid.utility.annotations.parameter.Unmodified;
import net.digitalid.utility.annotations.type.ThreadSafe;
import net.digitalid.utility.collaboration.annotations.TODO;
import net.digitalid.utility.collaboration.enumerations.Author;
import net.digitalid.utility.collaboration.enumerations.Priority;
import net.digitalid.utility.collections.set.FreezableSet;
import net.digitalid.utility.collections.set.ReadOnlySet;
import net.digitalid.utility.concurrency.exceptions.ReentranceException;
import net.digitalid.utility.freezable.annotations.NonFrozen;
import net.digitalid.utility.generator.annotations.generators.GenerateBuilder;
import net.digitalid.utility.generator.annotations.generators.GenerateSubclass;
import net.digitalid.utility.validation.annotations.type.Mutable;
import net.digitalid.utility.validation.annotations.value.Valid;

import net.digitalid.database.annotations.transaction.Committing;
import net.digitalid.database.annotations.transaction.NonCommitting;
import net.digitalid.database.conversion.SQL;
import net.digitalid.database.exceptions.DatabaseException;
import net.digitalid.database.property.set.PersistentSetPropertyEntry;
import net.digitalid.database.property.set.PersistentSetPropertyEntryBuilder;
import net.digitalid.database.property.set.ReadOnlyPersistentSetProperty;
import net.digitalid.database.property.set.WritablePersistentSetProperty;

import net.digitalid.core.concept.Concept;
import net.digitalid.core.entity.Entity;
import net.digitalid.core.property.SynchronizedProperty;
import net.digitalid.core.synchronizer.Synchronizer;

/**
 * This synchronized property synchronizes a value across sites.
 */
@ThreadSafe
@GenerateBuilder
@GenerateSubclass
@Mutable(ReadOnlyPersistentSetProperty.class)
public abstract class WritableSynchronizedSetProperty<E extends Entity, K, C extends Concept<E, K>, V, R extends ReadOnlySet<@Nonnull @Valid V>, F extends FreezableSet<@Nonnull @Valid V>> extends WritablePersistentSetProperty<C, V, R, F> implements SynchronizedProperty<E, K, C, PersistentSetPropertyEntry<C, V>, ReadOnlyPersistentSetProperty.Observer<C, V, R>> {
    
    /* -------------------------------------------------- Set -------------------------------------------------- */
    
    /**
     * This method has to be overridden like this because otherwise the equals method in the generated subclass cannot access the set of the other property.
     */
    @Pure
    @Override
    protected abstract @Nonnull @NonFrozen F getSet();
    
    /* -------------------------------------------------- Table -------------------------------------------------- */
    
    @Pure
    @Override
    public abstract @Nonnull SynchronizedSetPropertyTable<E, K, C, V, ?> getTable();
    
    /* -------------------------------------------------- Operations -------------------------------------------------- */
    
    @Impure
    @Override
    @Committing
    public boolean add(@Captured @Nonnull @Valid V value) throws DatabaseException, ReentranceException {
        lock.lock();
        try {
            if (!loaded) { load(false); }
            if (getSet().contains(value)) {
                return false;
            } else {
                Synchronizer.execute(SetPropertyInternalActionBuilder.withProperty(this).withValue(value).withAdded(true).build());
                return true;
            }
        } finally {
            lock.unlock();
        }
    }
    
    @Impure
    @Override
    @Committing
    public boolean remove(@NonCaptured @Unmodified @Nonnull @Valid V value) throws DatabaseException, ReentranceException {
        lock.lock();
        try {
            if (!loaded) { load(false); }
            if (getSet().contains(value)) {
                Synchronizer.execute(SetPropertyInternalActionBuilder.withProperty(this).withValue(value).withAdded(false).build());
                return true;
            } else {
                return false;
            }
        } finally {
            lock.unlock();
        }
    }
    
    /* -------------------------------------------------- Action -------------------------------------------------- */
    
    /**
     * Adds or removes the given value to or from this property from the {@link SetPropertyInternalAction}.
     */
    @Impure
    @NonCommitting
    @TODO(task = "Implement and use SQL.delete().", date = "2016-11-12", author = Author.KASPAR_ETTER, assignee = Author.STEPHANIE_STROKA, priority = Priority.HIGH)
    protected void modify(@Nonnull @Valid V value, boolean added) throws DatabaseException {
        lock.getReentrantLock().lock();
        try {
            final @Nonnull PersistentSetPropertyEntry<C, V> entry = PersistentSetPropertyEntryBuilder.<C, V>withSubject(getSubject()).withValue(value).build();
            if (added) {
                SQL.insert(entry, getTable().getEntryConverter(), getSubject().getSite());
                getSet().add(value);
            } else {
                SQL.insert(entry, getTable().getEntryConverter(), getSubject().getSite()); // TODO: SQL.delete()
                getSet().remove(value);
            }
            notifyObservers(value, added);
        } finally {
            lock.unlock();
        }
    }
    
}