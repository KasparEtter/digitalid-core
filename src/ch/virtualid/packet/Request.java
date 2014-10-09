package ch.virtualid.packet;

import ch.virtualid.annotations.Pure;
import ch.virtualid.annotations.RawRecipient;
import ch.virtualid.client.SecretCommitment;
import ch.virtualid.contact.AttributeSet;
import ch.virtualid.credential.Credential;
import ch.virtualid.cryptography.PublicKeyChain;
import ch.virtualid.cryptography.SymmetricKey;
import ch.virtualid.exceptions.external.ExternalException;
import ch.virtualid.exceptions.external.IdentityNotFoundException;
import ch.virtualid.exceptions.external.InvalidDeclarationException;
import ch.virtualid.exceptions.external.InvalidEncodingException;
import ch.virtualid.exceptions.external.InvalidSignatureException;
import static ch.virtualid.exceptions.packet.PacketError.KEYROTATION;
import ch.virtualid.exceptions.packet.PacketException;
import ch.virtualid.handler.Method;
import ch.virtualid.handler.query.external.AttributesQuery;
import ch.virtualid.identity.HostIdentifier;
import ch.virtualid.identity.Identifier;
import ch.virtualid.identity.Mapper;
import ch.virtualid.identity.NonHostIdentifier;
import ch.virtualid.identity.SemanticType;
import ch.virtualid.server.Server;
import ch.virtualid.util.FreezableArrayList;
import ch.virtualid.util.FreezableList;
import ch.virtualid.util.ReadonlyList;
import ch.xdf.Block;
import ch.xdf.ClientSignatureWrapper;
import ch.xdf.CredentialsSignatureWrapper;
import ch.xdf.HostSignatureWrapper;
import ch.xdf.SelfcontainedWrapper;
import ch.xdf.SignatureWrapper;
import ch.xdf.TupleWrapper;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.net.Socket;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.javatuples.Pair;

/**
 * This class compresses, signs and encrypts requests.
 * The subject of a request is given as identifier and not as an identity in order to be able to retrieve identity information as well as creating new accounts.
 * 
 * @invariant getSize() == methods.size() : "The number of elements equals the number of methods.";
 * @invariant getEncryption().getRecipient() != null : "The recipient of the request is not null.";
 * 
 * @see HostRequest
 * @see ClientRequest
 * @see CredentialsRequest
 * 
 * @author Kaspar Etter (kaspar.etter@virtualid.ch)
 * @version 1.4
 */
public class Request extends Packet {
    
    /**
     * Stores the methods of this request.
     * 
     * @invariant methods.isFrozen() : "The methods are frozen.";
     * @invariant methods.isNotEmpty() : "The methods are not empty.";
     * @invariant methods.doesNotContainNull() : "The methods do not contain null.";
     */
    private @Nonnull FreezableList<Method> methods;
    
    /**
     * Creates a new request with a query for the public key chain of the given host that is neither encrypted nor signed.
     * 
     * @param identifier the identifier of the host whose public key chain is to be retrieved.
     * 
     * @ensure getSize() == 1 : "The size of this request is 1.";
     */
    public Request(@Nonnull HostIdentifier identifier) throws SQLException, IOException, PacketException, ExternalException {
        this((FreezableList<Method>) new FreezableArrayList<Method>(new AttributesQuery(null, identifier, new AttributeSet(PublicKeyChain.TYPE).freeze())).freeze(), identifier, null, identifier, null, null, null, null, null, false, null);
    }
    
    /**
     * Packs the given methods with the given arguments with encrypting but without signing.
     * 
     * @param methods the methods of this request.
     * @param recipient the recipient of this request.
     * @param subject the subject of this request.
     * 
     * @require methods.isFrozen() : "The methods are frozen.";
     * @require methods.isNotEmpty() : "The methods are not empty.";
     * @require methods.doesNotContainNull() : "The methods do not contain null.";
     */
    public Request(@Nonnull FreezableList<Method> methods, @Nonnull HostIdentifier recipient, @Nonnull Identifier subject) throws SQLException, IOException, PacketException, ExternalException {
        this(methods, recipient, new SymmetricKey(), subject, null, null, null, null, null, false, null);
    }
    
    /**
     * Packs the given methods with the given arguments for encrypting and signing.
     * 
     * @param methods a list of methods whose blocks are to be packed as a request.
     * @param recipient the identifier of the host for which the content is to be encrypted.
     * @param symmetricKey the symmetric key used for encryption or null if the content is not encrypted.
     * @param subject the identifier of the identity about which a statement is made.
     * @param audit the audit with the time of the last retrieval or null in case of external requests.
     * @param signer the identifier of the signing host or null if the element is not signed by a host.
     * @param commitment the commitment containing the client secret or null if the element is not signed by a client.
     * @param credentials the credentials with which the content is signed or null if the content is not signed with credentials.
     * @param certificates the certificates that are appended to an identity-based authentication or null.
     * @param lodged whether the hidden content of the credentials is verifiably encrypted to achieve liability.
     * @param value the value b' or null if the credentials are not shortened.
     * 
     * @require ... : "This list of preconditions is not complete but the public constructors make sure that all requirements for packing the given handlers are met.";
     */
    Request(@Nonnull FreezableList<Method> methods, @Nonnull HostIdentifier recipient, @Nullable SymmetricKey symmetricKey, @Nonnull Identifier subject, @Nullable Audit audit, @Nullable HostIdentifier signer, @Nullable SecretCommitment commitment, @Nullable ReadonlyList<Credential> credentials, @Nullable ReadonlyList<HostSignatureWrapper> certificates, boolean lodged, @Nullable BigInteger value) throws SQLException, IOException, PacketException, ExternalException {
        super(methods, methods.size(), recipient, symmetricKey, subject, audit, signer, commitment, credentials, certificates, lodged, value);
        
        assert getEncryption().getRecipient() != null : "The recipient of the request is not null.";
    }
    
    
    /**
     * Reads and unpacks the request from the given input stream on hosts.
     * 
     * @param inputStream the input stream to read the request from.
     */
    public Request(@Nonnull InputStream inputStream) throws SQLException, IOException, PacketException, ExternalException {
        super(inputStream, null, true);
        
        assert getEncryption().getRecipient() != null : "The recipient of the request is not null.";
    }
    
    
    @Override
    @RawRecipient
    @SuppressWarnings("unchecked")
    void setLists(@Nonnull Object object) {
        this.methods = (FreezableList<Method>) object;
    }
    
    @Pure
    @Override
    @RawRecipient
    @Nonnull Block getBlock(int index) {
        return methods.get(index).toBlock();
    }
    
    @Override
    @RawRecipient
    void initialize(int size) {
        this.methods = new FreezableArrayList<Method>(size);
    }
    
    @Override
    @RawRecipient
    void freeze() {
        methods.freeze();
    }
    
    
    /**
     * Returns the method at the given position in this request.
     * 
     * @param index the index of the method which is to be returned.
     * 
     * @return the method at the given position in this request.
     * 
     * @require index >= 0 && index < getSize() : "The index is valid.";
     */
    @Pure
    public final @Nonnull Method getMethod(int index) {
        return methods.get(index);
    }
    
    /**
     * Sets the method at the given position during the packet constructor.
     * 
     * @param index the index of the method which is to be set.
     * @param method the method which is to set at the index.
     * 
     * @require index >= 0 && index < getSize() : "The index is valid.";
     */
    @RawRecipient
    final void setMethod(int index, @Nonnull Method method) {
        methods.set(index, method);
    }
    
    
    /**
     * Returns the recipient of this request.
     * 
     * @return the recipient of this request.
     */
    @Pure
    public final @Nonnull HostIdentifier getRecipient() {
        final @Nullable HostIdentifier recipient = getEncryption().getRecipient();
        assert recipient != null : "See the class invariant.";
        return recipient;
    }
    
    
    /**
     * Sends this request and returns the response with verifying the signature.
     * 
     * @return the response to this request.
     */
    public @Nonnull Response send() throws SQLException, IOException, PacketException, ExternalException {
        return send(true);
    }
    
    /**
     * Sends this request and returns the response, optionally verifying the signature.
     * 
     * @param verified determines whether the signature of the response is verified (if not, it needs to be checked by the caller).
     * 
     * @return the response to this request.
     * 
     * @throws FailedRequestException if the request could not be sent or the response could not be decoded.
     * @throws PacketException if the sending and the receiving of the packet went smooth but the recipient responded with a packet error.
     * 
     * @ensure response.getSize() == getSize() : "The response has the same number of signed contents (otherwise a {@link PacketException} is thrown).";
     */
    public @Nonnull Response send(boolean verified) throws SQLException, IOException, PacketException, ExternalException {
        final @Nonnull HostIdentifier recipient = getRecipient();
        
        // Send the request and retrieve the response.
        @Nonnull Response response;
        try (Socket socket = new Socket("vid." + recipient.getString(), Server.PORT)) {
            write(socket.getOutputStream());
            response = new Response(new SelfcontainedWrapper(socket.getInputStream(), false), getEncryption().getSymmetricKey(), verified);
        }
        
        try {
            // Verify that the response was signed by the right host.
            @Nonnull List<SignatureWrapper> responseSignatures = response.getSignatures();
            for (@Nonnull SignatureWrapper responseSignature : responseSignatures) {
                if (responseSignature.isSigned()) {
                    assert responseSignature instanceof HostSignatureWrapper : "Responses can only be signed by hosts (see the postcondition of the response constructor).";
                    @Nonnull Identifier signer = ((HostSignatureWrapper) responseSignature).getSigner();
                    if (!signer.equals(recipient)) throw new InvalidSignatureException("The response from the host " + recipient + " was signed by " + signer + ".");
                    break; // Only one signature needs to be verified since all signatures are signed alike.
                }
            }
            
            // Verify that the response was about the right subject.
            @Nullable Identifier requestSubject = getSignature(0).getSubject();
            @Nullable Identifier responseSubject = response.getSignature(0).getSubject();
            assert requestSubject != null && responseSubject != null : "Neither the request nor the response subject may be null (see the invariant of the packet class).";
            if (!responseSubject.equals(requestSubject)) throw new InvalidSignatureException("The subject of the request was " + requestSubject + ", the response from " + recipient + " was about " + responseSubject + " though.");
            @Nonnull Identifier subject = requestSubject;
            
            boolean resend = false;
            @Nullable SelfcontainedWrapper content = null;
            
            try {
                // The following statement throws a PacketException if the responding host encountered a (general) packet error.
                content = response.getContent(0);
            } catch (@Nonnull PacketException exception) {
                if (exception.getError() == KEYROTATION) {
                    // TODO: Rotate the key of the client.
                    resend = true;
                } else {
                    throw exception;
                }
            }
            
            if (content != null) {
                // Check whether the subject has been relocated as indicated by an unexpected identity response.
                @Nonnull SemanticType requestType = getContents().get(0).getIdentifier().getIdentity().toSemanticType();
                @Nonnull SemanticType responseType = content.getIdentifier().getIdentity().toSemanticType();
                if (!requestType.equals(SemanticType.IDENTITY_REQUEST) && responseType.equals(SemanticType.IDENTITY_RESPONSE)) {
                    @Nonnull Block element = content.getElement();
                    // TODO: Rewrite with the corresponding handler.
                    @Nonnull Block[] elements = new TupleWrapper(element).getElementsNotNull(3);
                    if (elements[2].isEmpty()) throw new InvalidEncodingException("The successor of the unexpected identity response to a request for " + subject + " is not null.");
                    @Nonnull NonHostIdentifier successor = new NonHostIdentifier(elements[2]);
                    if (!(subject instanceof NonHostIdentifier)) throw new InvalidEncodingException("An unexpected identity response may only be returned for non-host identities and not for " + subject + ".");
                    Mapper.setSuccessor((NonHostIdentifier) subject, successor); // TODO: Also pass the reference to the stored response.
                    if (!successor.getIdentity().equals(subject.getIdentity())) throw new InvalidDeclarationException("The indicated successor " + successor + " is not an identifier of the identity denoted by " + subject + ".");
                    subject = successor;
                    recipient = subject.getHostIdentifier();
                    resend = true;
                }
            }
            
            if (resend) {
                // Resend the request to the subject's successor or with the rotated key.
                @Nonnull SignatureWrapper signature = getSignature(getSize() - 1);
                if (signature instanceof HostSignatureWrapper) {
                    return new HostRequest(getContents(), recipient, subject, (HostIdentifier) ((HostSignatureWrapper) signature).getSigner()).send(verified);
                } else if (signature instanceof ClientSignatureWrapper) {
                    if (!subject.equals(requestSubject)) {
                        // TODO: Recommit to the (potentially) new host with the same client secret.
                    }
                    return new ClientRequest(getContents(), subject, signature.getAudit(), ((ClientSignatureWrapper) signature).getCommitment()).send(verified);
                } else if (signature instanceof CredentialsSignatureWrapper) {
                    @Nonnull CredentialsSignatureWrapper credentialsSignature = (CredentialsSignatureWrapper) signature;
                    return new CredentialsRequest(getContents(), recipient, subject, signature.getAudit(), credentialsSignature.getCredentials(), credentialsSignature.getCertificates(), credentialsSignature.isLodged(), credentialsSignature.getValue()).send(verified);
                } else {
                    return new Request(getContents(), recipient, subject).send(verified);
                }
            }
            
            if (response.getSize() != getSize()) throw new InvalidEncodingException("The response contains fewer (or more) contents than the request.");
            
        } catch (@Nonnull IdentityNotFoundException exception) {
            throw new FailedRequestException("Could not find the identity " + exception.getIdentifier() + ".", exception);
        } catch (@Nonnull InvalidEncodingException exception) {
            throw new FailedRequestException("Could not decode the response from the host " + recipient + ".", exception);
        } catch (@Nonnull InvalidSignatureException exception) {
            throw new FailedRequestException("The signature of the response was missing or invalid.", exception);
        } catch (@Nonnull SQLException exception) {
            throw new FailedRequestException("The successor could not be written to the database.", exception);
        } catch (@Nonnull InvalidDeclarationException exception) {
            throw new FailedRequestException("The declaration of the successor is invalid.", exception);
        } catch (@Nonnull FailedEncodingException exception) {
            throw new FailedRequestException("The request could not be re-encoded for the successor or with the rotated key.", exception);
        }
        
        return response;
    }
    
    
    /**
     * Stores a cached symmetric key for every recipient of host and client requests.
     */
    private static final @Nonnull HashMap<HostIdentifier, Pair<Long, SymmetricKey>> symmetricKeys = new HashMap<HostIdentifier, Pair<Long, SymmetricKey>>();
    
    /**
     * Stores whether the symmetric keys are cached for host and client requests.
     */
    private static boolean cachingKeys = true;
    
    /**
     * Returns whether the symmetric keys are cached for host and client requests.
     * 
     * @return whether the symmetric keys are cached for host and client requests.
     */
    public static boolean isCachingKeys() {
        return cachingKeys;
    }
    
    /**
     * Sets whether the symmetric keys are cached for host and client requests.
     * 
     * @param cachingKeys whether the symmetric keys are cached for host and client requests.
     */
    public static void setCachingKeys(boolean cachingKeys) {
        Request.cachingKeys = cachingKeys;
    }
    
    /**
     * Returns a new or cached symmetric key for the given recipient.
     * 
     * @param recipient the recipient for which a symmetric key is to be returned.
     * @param rotation determines how often the cached symmetric keys are rotated.
     * 
     * @return a new or cached symmetric key for the given recipient.
     */
    protected static @Nonnull SymmetricKey getSymmetricKey(@Nonnull HostIdentifier recipient, long rotation) {
        if (cachingKeys) {
            @Nullable Pair<Long, SymmetricKey> value = symmetricKeys.get(recipient);
            long currentTime = System.currentTimeMillis();
            if (value == null || value.getValue0() < currentTime - rotation) {
                value = new Pair<Long, SymmetricKey>(currentTime, new SymmetricKey());
                symmetricKeys.put(recipient, value);
            }
            return value.getValue1();
        } else {
            return new SymmetricKey();
        }
    }
    
}
