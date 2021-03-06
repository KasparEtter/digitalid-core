/*
 * Copyright (C) 2017 Synacts GmbH, Switzerland (info@synacts.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.digitalid.core.handler.reply;

import java.sql.SQLException;

import javax.annotation.Nonnull;

import net.digitalid.utility.annotations.method.Pure;
import net.digitalid.utility.annotations.method.PureWithSideEffects;
import net.digitalid.utility.validation.annotations.generation.Default;
import net.digitalid.utility.validation.annotations.generation.Provided;
import net.digitalid.utility.validation.annotations.type.Immutable;

import net.digitalid.database.annotations.transaction.NonCommitting;
import net.digitalid.database.exceptions.DatabaseException;

import net.digitalid.core.entity.NonHostEntity;
import net.digitalid.core.exceptions.request.RequestException;
import net.digitalid.core.handler.Auditable;
import net.digitalid.core.handler.method.action.ExternalAction;
import net.digitalid.core.service.Service;
import net.digitalid.core.unit.annotations.OnClientRecipient;

/**
 * This class models a {@link Reply reply} to an {@link ExternalAction external action}.
 * Action replies are added to the audit by the pusher on {@link Service services}.
 */
@Immutable
public abstract class ActionReply extends Reply<NonHostEntity> implements Auditable {
    
    /* -------------------------------------------------- Entity -------------------------------------------------- */
    
    @Pure
    @Override
    @Provided
    @Default("signature == null ? null : null /* Find a way to derive it from signature.getSubject(), probably make it injectable. */")
    public abstract @Nonnull NonHostEntity getEntity();
    
    // TODO: Use an @Derive on getProvidedSubject to derive the provided subject?
    
//    /**
//     * Creates an action reply that encodes the content of a packet.
//     * 
//     * @param account the account to which this action reply belongs.
//     */
//    protected ActionReply(@Nonnull Account account) {
//        super(account, account.getIdentity().getAddress());
//    }
    
    /* -------------------------------------------------- Execution -------------------------------------------------- */
    
    /**
     * Executes this action reply by the pusher.
     * 
     * @param action the external action that was sent.
     * 
     * @throws RequestException if the authorization is not sufficient.
     * 
     * @require hasSignature() : "This handler has a signature.";
     * @require action.getReplyClass().isInstance(this) : "This object is an instance of the action's reply class.";
     * @require getSubject().equals(action.getSubject()) : "The subjects of the reply and the action are the same.";
     * @require getEntityNotNull().equals(action.getEntityNotNull()) : "The entities of the reply and the action are the same.";
     * @require ((HostSignatureWrapper) getSignatureNotNull()).getSigner().equals(action.getRecipient()) : "The reply is signed by the action's recipient.";
     */
    @NonCommitting
    @PureWithSideEffects
    public abstract void executeByPusher(@Nonnull ExternalAction action) throws RequestException, SQLException;
    
    /**
     * Executes this action reply by the synchronizer.
     * 
     * @throws DatabaseException if this handler cannot be executed.
     */
    @NonCommitting
    @OnClientRecipient
    @PureWithSideEffects
    public abstract void executeBySynchronizer() throws DatabaseException;
    
}
