package net.digitalid.core.property.generation;

import javax.annotation.Nonnull;

import net.digitalid.utility.annotations.method.Pure;
import net.digitalid.utility.generator.annotations.generators.GenerateConverter;
import net.digitalid.utility.generator.annotations.generators.GenerateSubclass;
import net.digitalid.utility.validation.annotations.generation.Default;
import net.digitalid.utility.validation.annotations.size.MaxSize;
import net.digitalid.utility.validation.annotations.type.Immutable;

import net.digitalid.database.auxiliary.None;
import net.digitalid.database.property.value.WritablePersistentValueProperty;

import net.digitalid.core.concept.CoreConcept;
import net.digitalid.core.concept.annotations.GenerateSynchronizedProperty;
import net.digitalid.core.entity.NonHostEntity;
import net.digitalid.core.property.value.ValuePropertyRequiredAuthorization;
import net.digitalid.core.property.value.ValuePropertyRequiredAuthorizationBuilder;
import net.digitalid.core.restrictions.Restrictions;
import net.digitalid.core.restrictions.RestrictionsBuilder;

@Immutable
@GenerateSubclass
@GenerateConverter
abstract class GeneratePropertyClass extends CoreConcept<NonHostEntity<?>, None> {
    
    static final @Nonnull ValuePropertyRequiredAuthorization<NonHostEntity<?>, None, GeneratePropertyClass, String> PASSWORD = ValuePropertyRequiredAuthorizationBuilder.<NonHostEntity<?>, None, GeneratePropertyClass, String>withRequiredRestrictionsToExecuteMethod((concept, value) -> RestrictionsBuilder.withOnlyForClients(true).withWriteToNode(true).build()).withRequiredRestrictionsToSeeMethod((concept, value) -> Restrictions.ONLY_FOR_CLIENTS).build();
    
    @Pure
    @Default("\"\"")
    @GenerateSynchronizedProperty()
    public abstract @Nonnull WritablePersistentValueProperty<GeneratePropertyClass, @Nonnull @MaxSize(50) String> password();
    
}

public class GeneratePropertyTest {
    
}
