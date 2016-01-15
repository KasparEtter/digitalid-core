package net.digitalid.service.core.concepts.error;

import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import javax.annotation.Nonnull;

import net.digitalid.utility.validation.state.Pure;
import net.digitalid.utility.validation.state.Stateless;

import net.digitalid.database.core.Database;
import net.digitalid.database.core.annotations.NonCommitting;
import net.digitalid.database.core.table.Site;

import net.digitalid.service.core.CoreService;
import net.digitalid.service.core.handler.Action;
import net.digitalid.service.core.storage.ClientModule;
import net.digitalid.service.core.storage.Service;

/**
 * This class provides database access to the errors generated by the core service.
 */
@Stateless
public final class ErrorModule implements ClientModule {
    
    /**
     * Stores an instance of this module.
     */
    public static final ErrorModule MODULE = new ErrorModule();
    
    @Pure
    @Override
    public @Nonnull Service getService() {
        return CoreService.SERVICE;
    }
    
    @Override
    @NonCommitting
    public void createTables(@Nonnull Site site) throws DatabaseException {
        try (@Nonnull Statement statement = Database.createStatement()) {
            // TODO: Create the tables of this module.
        }
    }
    
    @Override
    @NonCommitting
    public void deleteTables(@Nonnull Site site) throws DatabaseException {
        try (@Nonnull Statement statement = Database.createStatement()) {
            // TODO: Delete the tables of this module.
        }
    }
    
    
    /**
     * Stores the date formatter for the output.
     */
    private static final @Nonnull ThreadLocal<DateFormat> formatter = new ThreadLocal<DateFormat>() {
        @Override protected DateFormat initialValue() {
            return new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss: ");
        }
    };
    
    /**
     * Adds the given message and action to the list of errors.
     * 
     * @param message the message to be added.
     * @param action the action to be added.
     */
    public static void add(@Nonnull String message, @Nonnull Action action) {
        // TODO: Make a real implementation.
//        Log.warning(message + " '" + action + "'.");
    }
    
    static { CoreService.SERVICE.add(MODULE); }
    
}
