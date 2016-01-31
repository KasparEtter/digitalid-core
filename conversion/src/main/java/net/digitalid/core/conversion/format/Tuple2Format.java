package net.digitalid.core.conversion.format;

import javax.annotation.Nonnull;

import net.digitalid.utility.exceptions.InternalException;

import net.digitalid.database.core.exceptions.operation.FailedValueStoringException;

public interface Tuple2Format<R, O1, E1, O2, E2> extends Format<R> {
    
    public abstract void consume1(O1 object) throws FailedValueStoringException, InternalException;
    
    public abstract void consume2(O2 object) throws FailedValueStoringException, InternalException;
    
    public abstract O1 produce1(E1 external);
    
    public abstract O2 produce2(E2 external);
    
    public abstract @Nonnull R finish();
    
}
