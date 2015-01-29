package ch.virtualid.credential;

import ch.virtualid.agent.RandomizedAgentPermissions;
import ch.virtualid.agent.Restrictions;
import ch.virtualid.annotations.Pure;
import ch.virtualid.auxiliary.Time;
import ch.virtualid.cache.Cache;
import ch.virtualid.cryptography.Element;
import ch.virtualid.cryptography.Exponent;
import ch.virtualid.cryptography.PublicKey;
import ch.virtualid.entity.NonHostAccount;
import ch.virtualid.entity.NonHostEntity;
import ch.virtualid.exceptions.external.ExternalException;
import ch.virtualid.exceptions.external.InvalidEncodingException;
import ch.virtualid.exceptions.packet.PacketException;
import ch.virtualid.handler.Reply;
import ch.virtualid.identity.InternalNonHostIdentity;
import ch.virtualid.identity.InternalPerson;
import ch.virtualid.identity.SemanticType;
import ch.virtualid.service.CoreServiceQueryReply;
import ch.xdf.Block;
import ch.xdf.HostSignatureWrapper;
import ch.xdf.TupleWrapper;
import java.io.IOException;
import java.math.BigInteger;
import java.sql.SQLException;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Replies the parameters of a new credential.
 * 
 * @see CredentialInternalQuery
 * @see CredentialExternalQuery
 * 
 * @author Kaspar Etter (kaspar.etter@virtualid.ch)
 * @version 2.0
 */
final class CredentialReply extends CoreServiceQueryReply {
    
    /**
     * Stores the semantic type {@code c.credential@virtualid.ch}.
     */
    private static final @Nonnull SemanticType C = SemanticType.create("c.credential@virtualid.ch").load(Element.TYPE);
    
    /**
     * Stores the semantic type {@code e.credential@virtualid.ch}.
     */
    private static final @Nonnull SemanticType E = SemanticType.create("e.credential@virtualid.ch").load(Exponent.TYPE);
    
    /**
     * Stores the semantic type {@code i.credential@virtualid.ch}.
     */
    private static final @Nonnull SemanticType I = SemanticType.create("i.credential@virtualid.ch").load(Exponent.TYPE);
    
    /**
     * Stores the semantic type {@code reply.credential@virtualid.ch}.
     */
    private static final @Nonnull SemanticType TYPE = SemanticType.create("reply.credential@virtualid.ch").load(TupleWrapper.TYPE, Restrictions.TYPE, Time.TYPE, C, E, I);
    
    
    /**
     * Stores the public key of the host that issued the credential.
     */
    private final @Nonnull PublicKey publicKey;
    
    /**
     * Stores the restrictions for which the credential is issued.
     */
    private final @Nullable Restrictions restrictions;
    
    /**
     * Stores the issuance time rounded down to the last half-hour.
     */
    private final @Nonnull Time issuance;
    
    /**
     * Stores the certifying base of the issued credential.
     */
    private final @Nonnull Element c;
    
    /**
     * Stores the certifying exponent of the issued credential.
     */
    private final @Nonnull Exponent e;
    
    /**
     * Stores the serial number of the issued credential.
     */
    private final @Nonnull Exponent i;
    
    /**
     * Creates a query reply for the parameters of a new credential.
     * 
     * @param account the account to which this query reply belongs.
     * @param publicKey the public key of the host that issued the credential.
     * @param restrictions the restrictions for which the credential is issued.
     * @param issuance the issuance time rounded down to the last half-hour.
     * @param c the certifying base of the issued credential.
     * @param e the certifying exponent of the issued credential.
     * @param i the serial number of the issued credential.
     */
    CredentialReply(@Nonnull NonHostAccount account, @Nonnull PublicKey publicKey, @Nullable Restrictions restrictions, @Nonnull Time issuance, @Nonnull Element c, @Nonnull Exponent e, @Nonnull Exponent i) {
        super(account);
        
        this.publicKey = publicKey;
        this.restrictions = restrictions;
        this.issuance = issuance;
        this.c = c;
        this.e = e;
        this.i = i;
    }
    
    /**
     * Creates a query reply that decodes a packet with the given signature for the given entity.
     * 
     * @param entity the entity to which this handler belongs.
     * @param signature the host signature of this handler.
     * @param number the number that references this reply.
     * @param block the content which is to be decoded.
     * 
     * @ensure hasSignature() : "This handler has a signature.";
     * @ensure !isOnHost() : "Query replies are never decoded on hosts.";
     */
    private CredentialReply(@Nullable NonHostEntity entity, @Nonnull HostSignatureWrapper signature, long number, @Nonnull Block block) throws SQLException, IOException, PacketException, ExternalException {
        super(entity, signature, number);
        
        if (!hasEntity()) throw new InvalidEncodingException("A credential reply must have an entity.");
        
        final @Nonnull TupleWrapper tuple = new TupleWrapper(block);
        this.restrictions = tuple.isElementNotNull(0) ? new Restrictions(entity, tuple.getElementNotNull(0)) : null;
        this.issuance = new Time(tuple.getElementNotNull(1));
        this.publicKey = Cache.getPublicKey(signature.getSubjectNotNull().getHostIdentifier(), issuance);
        this.c = publicKey.getCompositeGroup().getElement(tuple.getElementNotNull(2));
        this.e = new Exponent(tuple.getElementNotNull(3));
        this.i = new Exponent(tuple.getElementNotNull(4));
    }
    
    @Pure
    @Override
    public @Nonnull Block toBlock() {
        return new TupleWrapper(TYPE, Block.toBlock(restrictions), issuance.toBlock(), c.toBlock().setType(C), e.toBlock().setType(E), i.toBlock().setType(I)).toBlock();
    }
    
    @Pure
    @Override
    public @Nonnull String toString() {
        return "Replies the parameters of a new credential.";
    }
    
    
    /**
     * Returns an internal credential with the given parameters.
     * 
     * @param randomizedPermissions the client's randomized permissions.
     * @param role the role that is assumed or null in case no role is assumed.
     * @param b the blinding exponent of the issued credential.
     * @param u the client's secret of the issued credential.
     * 
     * @return an internal credential with the given parameters.
     * 
     * @require hasSignature() : "This handler has a signature.";
     */
    @Nonnull ClientCredential getInternalCredential(@Nonnull RandomizedAgentPermissions randomizedPermissions, @Nullable SemanticType role, @Nonnull BigInteger b, @Nonnull Exponent u) throws SQLException, IOException, PacketException, ExternalException {
        assert hasSignature() : "This handler has a signature.";
        
        if (restrictions == null) throw new InvalidEncodingException("The restrictions may not be null for internal credentials.");
        final @Nonnull Exponent v = new Exponent(restrictions.toBlock().getHash());
        
        final @Nonnull InternalPerson issuer = getSignatureNotNull().getSubjectNotNull().getIdentity().toInternalPerson();
        return new ClientCredential(publicKey, issuer, issuance, randomizedPermissions, role, restrictions, c, e, new Exponent(b), u, i, v);
    }
    
    /**
     * Returns an external credential with the given parameters.
     * 
     * @param randomizedPermissions the client's randomized permissions.
     * @param attributeContent the attribute content for access control.
     * @param b the blinding exponent of the issued credential.
     * @param u the client's secret of the issued credential.
     * @param v the hash of the requester's identifier.
     * 
     * @return an external credential with the given parameters.
     * 
     * @require hasSignature() : "This handler has a signature.";
     */
    @Nonnull ClientCredential getExternalCredential(@Nonnull RandomizedAgentPermissions randomizedPermissions, @Nonnull Block attributeContent, @Nonnull BigInteger b, @Nonnull Exponent u, @Nonnull Exponent v) throws SQLException, IOException, PacketException, ExternalException {
        assert hasSignature() : "This handler has a signature.";
        
        if (restrictions != null) throw new InvalidEncodingException("The restrictions must be null for external credentials.");
        
        final @Nonnull InternalNonHostIdentity issuer = getSignatureNotNull().getSubjectNotNull().getIdentity().toInternalNonHostIdentity();
        return new ClientCredential(publicKey, issuer, issuance, randomizedPermissions, attributeContent, c, e, new Exponent(b), u, i, v, false);
    }
    
    
    @Pure
    @Override
    public boolean equals(@Nullable Object object) {
        if (protectedEquals(object) && object instanceof CredentialReply) {
            final @Nonnull CredentialReply other = (CredentialReply) object;
            return this.publicKey.equals(other.publicKey) && Objects.equals(this.restrictions, other.restrictions) && this.issuance.equals(other.issuance) && this.c.equals(other.c) && this.e.equals(other.e) && this.i.equals(other.i);
        }
        return false;
    }
    
    @Pure
    @Override
    public int hashCode() {
        int hash = protectedHashCode();
        hash = 89 * hash + publicKey.hashCode();
        hash = 89 * hash + Objects.hashCode(restrictions);
        hash = 89 * hash + issuance.hashCode();
        hash = 89 * hash + c.hashCode();
        hash = 89 * hash + e.hashCode();
        hash = 89 * hash + i.hashCode();
        return hash;
    }
    
    
    @Pure
    @Override
    public @Nonnull SemanticType getType() {
        return TYPE;
    }
    
    /**
     * The factory class for the surrounding method.
     */
    private static final class Factory extends Reply.Factory {
        
        static { Reply.add(TYPE, new Factory()); }
        
        @Pure
        @Override
        protected @Nonnull Reply create(@Nullable NonHostEntity entity, @Nonnull HostSignatureWrapper signature, long number, @Nonnull Block block) throws SQLException, IOException, PacketException, ExternalException {
            return new CredentialReply(entity, signature, number, block);
        }
        
    }
    
}
