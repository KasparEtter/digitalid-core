package net.digitalid.core.expression;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.digitalid.utility.annotations.method.Pure;
import net.digitalid.utility.annotations.ownership.Capturable;
import net.digitalid.utility.collaboration.annotations.TODO;
import net.digitalid.utility.collaboration.enumerations.Author;
import net.digitalid.utility.collaboration.enumerations.Priority;
import net.digitalid.utility.collections.set.FreezableLinkedHashSet;
import net.digitalid.utility.collections.set.FreezableSet;
import net.digitalid.utility.contracts.Require;
import net.digitalid.utility.freezable.annotations.NonFrozen;
import net.digitalid.utility.generator.annotations.generators.GenerateSubclass;
import net.digitalid.utility.validation.annotations.type.Immutable;

import net.digitalid.core.expression.operators.BinaryOperator;
import net.digitalid.core.node.contact.Contact;
import net.digitalid.core.pack.Pack;
import net.digitalid.core.signature.credentials.CredentialsSignature;

/**
 * This class models contact expressions.
 */
@Immutable
@GenerateSubclass
abstract class ContactExpression extends Expression {
    
    /* -------------------------------------------------- Fields -------------------------------------------------- */
    
    /**
     * Returns the contact of this expression.
     */
    @Pure
    abstract @Nonnull Contact getContact();
    
    /* -------------------------------------------------- Queries -------------------------------------------------- */
    
    @Pure
    @Override
    boolean isPublic() {
        return false;
    }
    
    @Pure
    @Override
    boolean isActive() {
        return true;
    }
    
    @Pure
    @Override
    boolean isImpersonal() {
        return false;
    }
    
    /* -------------------------------------------------- Aggregations -------------------------------------------------- */
    
    @Pure
    @Override
    @Capturable @Nonnull @NonFrozen FreezableSet<@Nonnull Contact> getContacts() {
        Require.that(isActive()).orThrow("This expression has to be active but was $.", this);
        
        return FreezableLinkedHashSet.withElement(getContact());
    }
    
    @Pure
    @Override
    boolean matches(@Nonnull Pack attributeContent) {
        Require.that(isImpersonal()).orThrow("This expression has to be impersonal but was $.", this);
        
        return false;
    }
    
    @Pure
    @Override
    @TODO(task = "Implement the check.", date = "2016-12-02", author = Author.KASPAR_ETTER, priority = Priority.HIGH)
    boolean matches(@Nonnull CredentialsSignature<?> signature) {
        return true;
//        return signature.isIdentityBased() && !signature.isRoleBased() && signature.getIssuer().equals(contact.getPerson());
    }
    
    /* -------------------------------------------------- String -------------------------------------------------- */
    
    @Pure
    @Override
    @Nonnull String toString(@Nullable BinaryOperator operator, boolean right) {
        return ExpressionParser.addQuotesIfNecessary(getContact().getPerson());
    }
    
}
