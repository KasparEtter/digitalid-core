package net.digitalid.core.handler.reply;

import net.digitalid.utility.validation.annotations.type.Immutable;

import net.digitalid.core.entity.Entity;

/**
 * This class models a {@link Reply reply} to a {@link Query query}.
 * Query replies are read with getter methods on the handler.
 */
@Immutable
public abstract class QueryReply<E extends Entity> extends Reply<E> {}
