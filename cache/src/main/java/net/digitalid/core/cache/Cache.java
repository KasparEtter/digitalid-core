package net.digitalid.core.cache;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.digitalid.utility.annotations.method.Pure;
import net.digitalid.utility.annotations.method.PureWithSideEffects;
import net.digitalid.utility.collaboration.annotations.TODO;
import net.digitalid.utility.collaboration.enumerations.Author;
import net.digitalid.utility.contracts.Require;
import net.digitalid.utility.conversion.interfaces.Converter;
import net.digitalid.utility.exceptions.ExternalException;
import net.digitalid.utility.time.Time;
import net.digitalid.utility.time.TimeBuilder;
import net.digitalid.utility.validation.annotations.elements.NonNullableElements;
import net.digitalid.utility.validation.annotations.math.NonNegative;
import net.digitalid.utility.validation.annotations.size.NonEmpty;
import net.digitalid.utility.validation.annotations.type.Utility;

import net.digitalid.database.annotations.transaction.NonCommitting;

import net.digitalid.core.cache.exceptions.AttributeNotFoundException;
import net.digitalid.core.cache.exceptions.AttributeNotFoundExceptionBuilder;
import net.digitalid.core.cache.exceptions.CertificateNotFoundException;
import net.digitalid.core.cache.exceptions.CertificateNotFoundExceptionBuilder;
import net.digitalid.core.entity.NonHostEntity;
import net.digitalid.core.identification.identifier.HostIdentifier;
import net.digitalid.core.identification.identity.HostIdentity;
import net.digitalid.core.identification.identity.Identity;
import net.digitalid.core.identification.identity.InternalIdentity;
import net.digitalid.core.identification.identity.SemanticType;
import net.digitalid.core.signature.attribute.AttributeValue;
import net.digitalid.core.unit.annotations.OnClient;

/**
 * This class caches the {@link AttributeValue attribute values} of {@link Identity identities} for the attribute-specific {@link SemanticType#getCachingPeriod() caching period}.
 */
@Utility
public abstract class Cache {
    
    /* -------------------------------------------------- Attribute Value -------------------------------------------------- */
    
    /**
     * Returns the expiration time of the given attribute value.
     * 
     * @param type the type that was queried.
     * @param value the value that was replied.
     * @param reply the reply containing the value.
     * 
     * @return the expiration time of the given attribute value.
     */
    @Pure
    private static @Nonnull Time getExpiration(@Nonnull SemanticType type, @Nullable AttributeValue value, @Nonnull AttributesReply reply) /* TODO: throws InvalidEncodingException */ {
        // TODO
        throw new UnsupportedOperationException();
//        if (value != null && !value.getContent().getType().equals(type)) { throw InvalidReplyParameterValueException.get(reply, "attribute type", type.getAddress(), value.getContent().getType().getAddress()); }
//        return type.getCachingPeriodNotNull().add(value instanceof CertifiedAttributeValue ? ((CertifiedAttributeValue) value).getTime() : reply.getSignatureNotNull().getNonNullableTime());
    }
    
    /**
     * Returns the attribute values of the given identity with the given types.
     * The attribute values are returned in the same order as given by the types.
     * If an attribute value is not available, the value null is returned instead.
     * If an attribute value is certified, the certificate is verified and stripped
     * from the attribute values in case the signature or the delegation is invalid.
     * 
     * @param identity the identity whose attribute values are to be returned.
     * @param entity the entity that queries the attribute values or null for hosts.
     * @param time the time at which the cached attribute values have to be fresh.
     * @param types the types of the attribute values which are to be returned.
     * 
     * @return the attribute values of the given identity with the given types.
     * 
     * @require !Arrays.asList(types).contains(PublicKeyChain.TYPE) || types.length == 1 : "If the public key chain of a host is queried, it is the only type.";
     * @require for (SemanticType type : types) type != null && type.isAttributeFor(identity.getCategory()) : "Each type is not null and can be used as an attribute for the category of the given identity.";
     * 
     * @ensure return.length == types.length : "The returned attribute values are as many as the given types.";
     * @ensure for (i = 0; i < return.length; i++) return[i] == null || return[i].getContent().getType().equals(types[i])) : "Each returned attribute value is either null or matches the corresponding type.";
     */
    @Pure
    @NonCommitting
    public static @Nonnull AttributeValue[] getAttributeValues(@Nonnull InternalIdentity identity, @Nullable @OnClient NonHostEntity entity, @Nonnull @NonNegative Time time, @Nonnull @NonNullableElements @NonEmpty SemanticType... types) throws ExternalException {
        Require.that(time.isNonNegative()).orThrow("The given time is non-negative.");
        Require.that(types.length > 0).orThrow("At least one type is given.");
//        Require.that(!Arrays.asList(types).contains(PublicKeyChain.TYPE) || types.length == 1).orThrow("If the public key chain of a host is queried, it is the only type."); // TODO
        for (final @Nonnull SemanticType type : types) { Require.that(type != null && type.isAttributeFor(identity.getCategory())).orThrow("Each type is not null and can be used as an attribute for the category of the given identity."); }
        
        final @Nonnull AttributeValue[] attributeValues = new AttributeValue[types.length];
        
        // TODO
        
//        final @Nonnull FreezableAttributeTypeSet typesToRetrieve = new FreezableAttributeTypeSet();
//        final @Nonnull List<Integer> indexesToStore = new LinkedList<>();
//        for (int i = 0; i < types.length; i++) {
//            final @Nonnull Pair<Boolean, AttributeValue> cache = getCachedAttributeValue(identity, entity, time, types[i]);
//            if (cache.get0()) {
//                attributeValues[i] = cache.get1();
//            } else {
//                typesToRetrieve.add(types[i]);
//                indexesToStore.add(i);
//            }
//        }
//        
//        if (typesToRetrieve.size() > 0) {
//            if (typesToRetrieve.contains(PublicKeyChain.TYPE)) {
//                final @Nonnull AttributesReply reply = new Request(identity.getAddress().getHostIdentifier()).send(false).getReplyNotNull(0);
//                final @Nullable AttributeValue value = reply.getAttributeValues().getNullable(0);
//                setCachedAttributeValue(identity, null, getExpiration(PublicKeyChain.TYPE, value, reply), PublicKeyChain.TYPE, value, reply);
//                reply.getSignatureNotNull().verify();
//                attributeValues[0] = value;
//            } else {
//                final @Nonnull Response response = new AttributesQuery(entity, identity.getAddress(), typesToRetrieve.freeze(), true).send();
//                final @Nonnull AttributesReply reply = response.getReplyNotNull(0);
//                final @Nonnull ReadOnlyList<AttributeValue> values = reply.getAttributeValues();
//                if (values.size() != typesToRetrieve.size()) { throw InvalidReplyParameterValueException.get(reply, "number of attributes", typesToRetrieve.size(), values.size()); }
//                int i = 0;
//                for (final @Nonnull SemanticType type : typesToRetrieve) {
//                    final @Nullable AttributeValue value = values.getNullable(i);
//                    setCachedAttributeValue(identity, response.getRequest().isSigned() ? entity : null, getExpiration(type, value, reply), type, value, reply);
//                    attributeValues[indexesToStore.get(i)] = value;
//                    i++;
//                }
//            }
//        }
        
        return attributeValues;
    }
    
    /**
     * Returns the attribute value of the given identity with the given type.
     * If the attribute value is certified, the certificate is verified and stripped
     * from the attribute value in case the signature or the delegation is invalid.
     * 
     * @param identity the identity whose attribute value is to be returned.
     * @param entity the entity that queries the attribute value or null for hosts.
     * @param time the time at which the cached attribute value has to be fresh.
     * @param type the type of the attribute value which is to be returned.
     * 
     * @return the attribute value of the given identity with the given type.
     * 
     * @throws AttributeNotFoundException if the attribute is not available.
     * 
     * @require type.isAttributeFor(identity.getCategory()) : "The type can be used as an attribute for the category of the given identity.";
     * 
     * @ensure return.getContent().getType().equals(type)) : "The returned attribute value matches the given type.";
     */
    @Pure
    @NonCommitting
    public static @Nonnull AttributeValue getAttributeValue(@Nonnull InternalIdentity identity, @Nullable @OnClient NonHostEntity entity, @Nonnull @NonNegative Time time, @Nonnull SemanticType type) throws ExternalException {
        final @Nonnull AttributeValue[] attributeValues = getAttributeValues(identity, entity, time, type);
        if (attributeValues[0] == null) { throw AttributeNotFoundExceptionBuilder.withIdentity(identity).withType(type).build(); }
        else { return attributeValues[0]; }
    }
    
    /* -------------------------------------------------- Attribute Content -------------------------------------------------- */
    
    // TODO: Adapt the Javadoc of the following methods.
    
    /**
     * Returns the attribute content of the given identity with the given type.
     * 
     * @param identity the identity whose attribute content is to be returned.
     * @param entity the entity that queries the attribute content or null for hosts.
     * @param time the time at which the cached attribute content has to be fresh.
     * @param type the type of the attribute content which is to be returned.
     * @param certified whether the attribute content should be certified.
     * 
     * @return the attribute content of the given identity with the given type.
     * 
     * @throws AttributeNotFoundException if the attribute value is not available.
     * @throws CertificateNotFoundException if the value should be certified but is not.
     * 
     * @require type.isAttributeFor(identity.getCategory()) : "The type can be used as an attribute for the category of the given identity.";
     * 
     * @ensure return.getType().equals(type) : "The returned content has the given type.";
     */
    @Pure
    @NonCommitting
    @TODO(task = "Provide a second method that derives the semantic type from the converter (and thus has less method parameters)?", date = "2016-12-03", author = Author.KASPAR_ETTER)
    public static <T> @Nonnull T getAttributeContent(@Nonnull InternalIdentity identity, @Nullable @OnClient NonHostEntity entity, @Nonnull @NonNegative Time time, @Nonnull SemanticType type, @Nonnull Converter<T, Void> converter, boolean certified) throws ExternalException {
        final @Nonnull AttributeValue value = getAttributeValue(identity, entity, time, type);
        if (certified && !value.isCertified()) { throw CertificateNotFoundExceptionBuilder.withIdentity(identity).withType(type).build(); }
        final @Nullable T content = value.getSignature().getObject().unpack(converter, null);
        if (content == null) { throw AttributeNotFoundExceptionBuilder.withIdentity(identity).withType(type).build(); }
        return content;
    }
    
    /**
     * Returns the fresh attribute content of the given identity with the given type.
     * 
     * @param identity the identity whose attribute content is to be returned.
     * @param entity the entity that queries the attribute content or null for hosts.
     * @param type the type of the attribute content which is to be returned.
     * @param certified whether the attribute content should be certified.
     * 
     * @return the attribute content of the given identity with the given type.
     * 
     * @throws AttributeNotFoundException if the attribute value is not available.
     * @throws CertificateNotFoundException if the value should be certified but is not.
     * 
     * @require type.isAttributeFor(identity.getCategory()) : "The type can be used as an attribute for the category of the given identity.";
     * 
     * @ensure return.getType().equals(type) : "The returned content has the given type.";
     */
    @Pure
    @NonCommitting
    public static <T> @Nonnull T getFreshAttributeContent(@Nonnull InternalIdentity identity, @Nullable @OnClient NonHostEntity entity, @Nonnull SemanticType type, @Nonnull Converter<T, Void> converter, boolean certified) throws ExternalException {
        return getAttributeContent(identity, entity, TimeBuilder.build(), type, converter, certified);
    }
    
    /**
     * Returns the stale attribute content of the given identity with the given type.
     * 
     * @param identity the identity whose attribute content is to be returned.
     * @param entity the entity that queries the attribute content or null for hosts.
     * @param type the type of the attribute content which is to be returned.
     * 
     * @return the attribute content of the given identity with the given type.
     * 
     * @throws AttributeNotFoundException if the attribute value is not available.
     * 
     * @require type.isAttributeFor(identity.getCategory()) : "The type can be used as an attribute for the category of the given identity.";
     * 
     * @ensure return.getType().equals(type) : "The returned content has the given type.";
     */
    @Pure
    @NonCommitting
    public static <T> @Nonnull T getStaleAttributeContent(@Nonnull InternalIdentity identity, @Nullable @OnClient NonHostEntity entity, @Nonnull SemanticType type, @Nonnull Converter<T, Void> converter) throws ExternalException {
        return getAttributeContent(identity, entity, Time.MIN, type, converter, false);
    }
    
    /**
     * Returns the reloaded attribute content of the given identity with the given type.
     * 
     * @param identity the identity whose attribute content is to be returned.
     * @param entity the entity that queries the attribute content or null for hosts.
     * @param type the type of the attribute content which is to be returned.
     * @param certified whether the attribute content should be certified.
     * 
     * @return the attribute content of the given identity with the given type.
     * 
     * @throws AttributeNotFoundException if the attribute value is not available.
     * @throws CertificateNotFoundException if the value should be certified but is not.
     * 
     * @require type.isAttributeFor(identity.getCategory()) : "The type can be used as an attribute for the category of the given identity.";
     * 
     * @ensure return.getType().equals(type) : "The returned content has the given type.";
     */
    @Pure
    @NonCommitting
    public static <T> @Nonnull T getReloadedAttributeContent(@Nonnull InternalIdentity identity, @Nullable @OnClient NonHostEntity entity, @Nonnull SemanticType type, @Nonnull Converter<T, Void> converter, boolean certified) throws ExternalException {
        return getAttributeContent(identity, entity, Time.MAX, type, converter, certified);
    }
    
    /* -------------------------------------------------- Host Lookup -------------------------------------------------- */
    
    /**
     * Establishes the identity of the given host identifier by checking its existence and requesting its public key chain.
     * (This method is only to be called by the identifier resolver implementation.)
     * 
     * @param identifier the host identifier whose identity is to be established.
     * 
     * @return the newly established identity of the given host identifier.
     * 
     * @require !identifier.isMapped() : "The identifier is not mapped.";
     */
    @NonCommitting
    @PureWithSideEffects
    public static @Nonnull HostIdentity establishHostIdentity(@Nonnull /* @NonMapped */HostIdentifier identifier) throws ExternalException {
        // TODO: Is this still necessary and at the right place?
        
        throw new UnsupportedOperationException();
        
//        Require.that(!identifier.isMapped()).orThrow("The identifier is not mapped.");
//        
//        final @Nonnull HostIdentity identity = Mapper.mapHostIdentity(identifier);
//        final @Nonnull Response response;
//        try {
//            response = new Request(identifier).send(false);
//        } catch (@Nonnull IOException exception) {
//            throw IdentityNotFoundException.get(identifier);
//        }
//        final @Nonnull AttributesReply reply = response.getReplyNotNull(0);
//        final @Nullable AttributeValue value = reply.getAttributeValues().getNullable(0);
//        if (value == null) { throw AttributeNotFoundException.get(identity, PublicKeyChain.TYPE); }
//        if (!value.isCertified()) { throw CertificateNotFoundException.get(identity, PublicKeyChain.TYPE); }
//        setCachedAttributeValue(identity, null, getExpiration(PublicKeyChain.TYPE, value, reply), PublicKeyChain.TYPE, value, reply);
//        reply.getSignatureNotNull().verify();
//        return identity;
    }
    
}
