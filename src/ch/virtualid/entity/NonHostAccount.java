package ch.virtualid.entity;

import ch.virtualid.annotations.Pure;
import ch.virtualid.concept.Aspect;
import ch.virtualid.concept.Instance;
import ch.virtualid.concept.Observer;
import ch.virtualid.database.Database;
import ch.virtualid.entity.NonHostEntity;
import ch.virtualid.identity.Identity;
import ch.virtualid.identity.IdentityClass;
import ch.virtualid.identity.InternalNonHostIdentity;
import ch.virtualid.interfaces.Immutable;
import ch.virtualid.interfaces.SQLizable;
import ch.virtualid.host.Host;
import ch.virtualid.util.ConcurrentHashMap;
import ch.virtualid.util.ConcurrentMap;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * This class models a non-host account.
 * 
 * @author Kaspar Etter (kaspar.etter@virtualid.ch)
 * @version 1.0
 */
public final class NonHostAccount extends Account implements NonHostEntity, Immutable, SQLizable {
    
    /**
     * Stores the identity of this non-host account.
     */
    private final @Nonnull InternalNonHostIdentity identity;
    
    /**
     * Creates a new non-host account with the given host and identity.
     * 
     * @param host the host of this non-host account.
     * @param identity the identity of this non-host account.
     */
    private NonHostAccount(@Nonnull Host host, @Nonnull InternalNonHostIdentity identity) {
        super(host);
        
        this.identity = identity;
    }
    
    @Pure
    @Override
    public @Nonnull InternalNonHostIdentity getIdentity() {
        return identity;
    }
    
    
    /**
     * Caches non-host accounts given their host and identity.
     */
    private static final @Nonnull ConcurrentMap<Host, ConcurrentMap<InternalNonHostIdentity, NonHostAccount>> index = new ConcurrentHashMap<Host, ConcurrentMap<InternalNonHostIdentity, NonHostAccount>>();
    
    static {
        if (Database.isSingleAccess()) {
            Instance.observeAspects(new Observer() {
                @Override public void notify(@Nonnull Aspect aspect, @Nonnull Instance instance) { index.remove(instance); }
            }, Host.DELETED);
        }
    }
    
    /**
     * Returns a potentially locally cached non-host account.
     * 
     * @param host the host of the non-host account to return.
     * @param identity the identity of the non-host account to return.
     * 
     * @return a new or existing non-host account with the given host and identity.
     */
    @Pure
    public static @Nonnull NonHostAccount get(@Nonnull Host host, @Nonnull InternalNonHostIdentity identity) {
        if (Database.isSingleAccess()) {
            @Nullable ConcurrentMap<InternalNonHostIdentity, NonHostAccount> map = index.get(host);
            if (map == null) map = index.putIfAbsentElseReturnPresent(host, new ConcurrentHashMap<InternalNonHostIdentity, NonHostAccount>());
            @Nullable NonHostAccount account = map.get(identity);
            if (account == null) account = map.putIfAbsentElseReturnPresent(identity, new NonHostAccount(host, identity));
            return account;
        } else {
            return new NonHostAccount(host, identity);
        }
    }
    
    /**
     * Returns the given column of the result set as an instance of this class.
     * 
     * @param host the host on which the non-host account is hosted.
     * @param resultSet the result set to retrieve the data from.
     * @param columnIndex the index of the column containing the data.
     * 
     * @return the given column of the result set as an instance of this class.
     */
    @Pure
    public static @Nonnull NonHostAccount getNotNull(@Nonnull Host host, @Nonnull ResultSet resultSet, int columnIndex) throws SQLException {
        final @Nonnull Identity identity = IdentityClass.getNotNull(resultSet, columnIndex);
        if (identity instanceof InternalNonHostIdentity) return get(host, (InternalNonHostIdentity) identity);
        else throw new SQLException("The identity of " + identity.getAddress() + " is not a non-host.");
    }
    
}
