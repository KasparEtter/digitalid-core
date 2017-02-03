package net.digitalid.core.attribute;

import javax.annotation.Nonnull;

import net.digitalid.utility.validation.annotations.type.Utility;

import net.digitalid.database.auxiliary.Time;

import net.digitalid.core.annotations.type.Loaded;
import net.digitalid.core.identification.identity.Category;
import net.digitalid.core.identification.identity.SemanticType;
import net.digitalid.core.identification.identity.SemanticTypeAttributesBuilder;
import net.digitalid.core.identification.identity.SyntacticType;

/**
 * This class stores commonly used attribute types.
 */
@Utility
public abstract class AttributeTypes {
    
    /**
     * Stores the semantic type {@code name@core.digitalid.net}.
     */
    public static final @Nonnull @Loaded SemanticType NAME = SemanticType.map("name@core.digitalid.net").load(SemanticTypeAttributesBuilder.withSyntacticBase(SyntacticType.STRING).withCategories(Category.INTERNAL_NON_HOST_IDENTITIES).withCachingPeriod(Time.MONTH).build());
    
    /**
     * Stores the semantic type {@code prename@core.digitalid.net}.
     */
    public static final @Nonnull @Loaded SemanticType PRENAME = SemanticType.map("prename@core.digitalid.net").load(SemanticTypeAttributesBuilder.withSyntacticBase(SyntacticType.STRING).withCategories(Category.ONLY_NATURAL_PERSON).withCachingPeriod(Time.MONTH).build());
    
    /**
     * Stores the semantic type {@code surname@core.digitalid.net}.
     */
    public static final @Nonnull @Loaded SemanticType SURNAME = SemanticType.map("surname@core.digitalid.net").load(SemanticTypeAttributesBuilder.withSyntacticBase(SyntacticType.STRING).withCategories(Category.ONLY_NATURAL_PERSON).withCachingPeriod(Time.MONTH).build());
    
    /**
     * Stores the semantic type {@code email@core.digitalid.net}.
     */
    public static final @Nonnull @Loaded SemanticType EMAIL = SemanticType.map("email@core.digitalid.net").load(SemanticTypeAttributesBuilder.withSyntacticBase(SyntacticType.STRING).withCategories(Category.PERSONS).withCachingPeriod(Time.HALF_DAY).build());
    
    /**
     * Stores the semantic type {@code phone@core.digitalid.net}.
     */
    public static final @Nonnull @Loaded SemanticType PHONE = SemanticType.map("phone@core.digitalid.net").load(SemanticTypeAttributesBuilder.withSyntacticBase(SyntacticType.STRING).withCategories(Category.PERSONS).withCachingPeriod(Time.HALF_DAY).build());
    
    /**
     * Stores the semantic type {@code skype@core.digitalid.net}.
     */
    public static final @Nonnull @Loaded SemanticType SKYPE = SemanticType.map("skype@core.digitalid.net").load(SemanticTypeAttributesBuilder.withSyntacticBase(SyntacticType.STRING).withCategories(Category.PERSONS).withCachingPeriod(Time.HALF_DAY).build());
    
    /**
     * Stores the semantic type {@code address@core.digitalid.net}.
     */
    public static final @Nonnull @Loaded SemanticType ADDRESS = SemanticType.map("address@core.digitalid.net").load(SemanticTypeAttributesBuilder.withSyntacticBase(SyntacticType.STRING).withCategories(Category.PERSONS).withCachingPeriod(Time.WEEK).build());
    
    /**
     * Stores the semantic type {@code website@core.digitalid.net}.
     */
    public static final @Nonnull @Loaded SemanticType WEBSITE = SemanticType.map("website@core.digitalid.net").load(SemanticTypeAttributesBuilder.withSyntacticBase(SyntacticType.STRING).withCategories(Category.PERSONS).withCachingPeriod(Time.WEEK).build());
    
    /**
     * Stores the semantic type {@code birthday@core.digitalid.net}.
     */
    public static final @Nonnull @Loaded SemanticType BIRTHDAY = SemanticType.map("birthday@core.digitalid.net").load(SemanticTypeAttributesBuilder.withSyntacticBase(SyntacticType.STRING).withCategories(Category.ONLY_NATURAL_PERSON).withCachingPeriod(Time.TROPICAL_YEAR).build());
    
}
