package ch.virtualid.database;

import ch.virtualid.annotations.Pure;
import ch.virtualid.interfaces.Immutable;
import ch.virtualid.io.Console;
import ch.virtualid.io.Directory;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Savepoint;
import java.sql.Statement;
import java.util.Properties;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.postgresql.Driver;

/**
 * This class configures a PostgreSQL database.
 * 
 * @author Kaspar Etter (kaspar.etter@virtualid.ch)
 * @version 1.0
 */
public final class PostgreSQLConfiguration extends Configuration implements Immutable {
    
    /**
     * Stores the server address of the database.
     */
    private final @Nonnull String server;
    
    /**
     * Stores the port number of the database.
     */
    private final @Nonnull String port;
    
    /**
     * Stores the name of the database.
     */
    private final @Nonnull String database;
    
    /**
     * Stores the user of the database.
     */
    private final @Nonnull String user;
    
    /**
     * Stores the password of the database.
     */
    private final @Nonnull String password;
    
    /**
     * Stores the user and the password as properties.
     */
    private final @Nonnull Properties properties = new Properties();
    
    /**
     * Creates a new PostgreSQL configuration by reading the properties from the indicated file or from the user's input.
     * 
     * @param name the name of the database configuration file (without the suffix).
     * @param reset whether the database is to be dropped first before creating it again.
     * 
     * @require Database.isValid(name) : "The name is valid for a database.";
     */
    public PostgreSQLConfiguration(@Nonnull String name, boolean reset) throws SQLException, IOException {
        super(new Driver());
        
        assert Database.isValid(name) : "The name is valid for a database.";
        
        final @Nonnull File file = new File(Directory.DATA.getPath() + Directory.SEPARATOR + name + ".conf");
        if (file.exists()) {
            try (@Nonnull FileInputStream stream = new FileInputStream(file); @Nonnull InputStreamReader reader = new InputStreamReader(stream, "UTF-8")) {
                properties.load(reader);
                server = properties.getProperty("Server", "localhost");
                port = properties.getProperty("Port", "5432");
                database = properties.getProperty("Database", "virtualid");
                user = properties.getProperty("User", "root");
                password = properties.getProperty("Password", "");
            }
        } else {
            Console.write();
            Console.write("The PostgreSQL database is not yet configured. Please provide the following information:");
            server = Console.readString("- Server (e.g. \"localhost\"): ");
            port = Console.readString("- Port (the default is 5432): ");
            database = Console.readString("- Database (e.g. \"virtualid\"): ");
            user = Console.readString("- User (e.g. \"root\"): ");
            password = Console.readString("- Password: ");
            
            properties.setProperty("Server", server);
            properties.setProperty("Port", port);
            properties.setProperty("Database", database);
            properties.setProperty("User", user);
            properties.setProperty("Password", password);
            
            try (@Nonnull FileOutputStream stream = new FileOutputStream(file); @Nonnull OutputStreamWriter writer = new OutputStreamWriter(stream, "UTF-8")) {
                properties.store(writer, "Configuration of the PostgreSQL database");
            }
        }
        
        properties.clear();
        properties.setProperty("user", user);
        properties.setProperty("password", password);
        
        try (@Nonnull Connection connection = DriverManager.getConnection("jdbc:postgresql://" + server + ":" + port + "/", properties); @Nonnull Statement statement = connection.createStatement()) {
            if (reset) statement.executeUpdate("DROP DATABASE IF EXISTS " + database);
            final @Nonnull ResultSet resultSet = statement.executeQuery("SELECT EXISTS (SELECT * FROM pg_catalog.pg_database WHERE datname = '" + database + "')");
            if (resultSet.next() && !resultSet.getBoolean(1)) statement.executeUpdate("CREATE DATABASE " + database);
        }
    }
    
    @Override
    public void dropDatabase() throws SQLException {
        Database.close();
        try (@Nonnull Connection connection = DriverManager.getConnection("jdbc:postgresql://" + server + ":" + port + "/", properties); @Nonnull Statement statement = connection.createStatement()) {
            statement.executeUpdate("DROP DATABASE IF EXISTS " + database);
        }
    }
    
    /**
     * Creates a new PostgreSQL configuration by reading the properties from the default file or from the user's input.
     * 
     * @param reset whether the database is to be dropped first before creating it again.
     */
    public PostgreSQLConfiguration(boolean reset) throws SQLException, IOException {
        this("PostgreSQL", reset);
    }
    
    /**
     * Returns whether a PostgreSQL configuration exists.
     * 
     * @return whether a PostgreSQL configuration exists.
     */
    public static boolean exists() {
        return new File(Directory.DATA.getPath() + Directory.SEPARATOR + "PostgreSQL.conf").exists();
    }
    
    
    @Pure
    @Override
    protected @Nonnull String getURL() {
        return "jdbc:postgresql://" + server + ":" + port + "/" + database;
    }
    
    @Pure
    @Override
    protected @Nonnull Properties getProperties() {
        return properties;
    }
    
    
    @Pure
    @Override
    public @Nonnull String PRIMARY_KEY() {
        return "BIGSERIAL PRIMARY KEY";
    }
    
    @Pure
    @Override
    public @Nonnull String TINYINT() {
        return "SMALLINT";
    }
    
    @Pure
    @Override
    public @Nonnull String BINARY() {
        return "\"en_US.UTF-8\"";
    }
    
    @Pure
    @Override
    public @Nonnull String NOCASE() {
        return "\"en_US.UTF-8\"";
    }
    
    @Pure
    @Override
    public @Nonnull String CITEXT() {
        return "CITEXT";
    }
    
    @Pure
    @Override
    public @Nonnull String BLOB() {
        return "BYTEA";
    }
    
    @Pure
    @Override
    public @Nonnull String HASH() {
        return "BYTEA";
    }
    
    @Pure
    @Override
    public @Nonnull String VECTOR() {
        return "BYTEA";
    }
    
    @Pure
    @Override
    public @Nonnull String REPLACE() {
        return "INSERT";
    }
    
    @Pure
    @Override
    public @Nonnull String IGNORE() {
        return "";
    }
    
    @Pure
    @Override
    public @Nonnull String GREATEST() {
        return "GREATEST";
    }
    
    @Pure
    @Override
    public @Nonnull String CURRENT_TIME() {
        return "ROUND(EXTRACT(EPOCH FROM CLOCK_TIMESTAMP()) * 1000)";
    }
    
    
    @Override
    public @Nonnull Savepoint setSavepoint(@Nonnull Connection connection) throws SQLException {
        return connection.setSavepoint();
    }
    
    @Override
    public void rollback(@Nonnull Connection connection, @Nullable Savepoint savepoint) throws SQLException {
        connection.rollback(savepoint);
        connection.releaseSavepoint(savepoint);
    }
    
    
    @Override
    public void onInsertIgnore(@Nonnull Statement statement, @Nonnull String table, @Nonnull String... columns) throws SQLException {
        assert columns.length > 0 : "At least one column is provided.";
        
        final @Nonnull StringBuilder string = new StringBuilder("CREATE OR REPLACE RULE ").append(table).append("_on_insert_ignore ");
        string.append("AS ON INSERT TO ").append(table).append(" WHERE EXISTS(SELECT 1 FROM ").append(table).append(" WHERE (");
        boolean first = true;
        for (@Nonnull String column : columns) {
            if (first) first = false;
            else string.append(", ");
            string.append(column);
        }
        string.append(") = (");
        first = true;
        for (@Nonnull String column : columns) {
            if (first) first = false;
            else string.append(", ");
            string.append("NEW.").append(column);
        }
        string.append(")) DO INSTEAD NOTHING");
        statement.executeUpdate(string.toString());
    }
    
    @Override
    public void onInsertNotIgnore(@Nonnull Statement statement, @Nonnull String table) throws SQLException {
        statement.executeUpdate("DROP RULE IF EXISTS " + table + "_on_insert_ignore ON " + table);
    }
    
    
    @Override
    public void onInsertUpdate(@Nonnull Statement statement, @Nonnull String table, int key, @Nonnull String... columns) throws SQLException {
        assert key > 0 : "The number of columns in the primary key is positive.";
        assert columns.length >= key : "At least as many columns as in the primary key are provided.";
        
        final @Nonnull StringBuilder string = new StringBuilder("CREATE OR REPLACE RULE ").append(table).append("_on_insert_update ");
        string.append("AS ON INSERT TO ").append(table).append(" WHERE EXISTS(SELECT 1 FROM ").append(table);
        
        final @Nonnull StringBuilder condition = new StringBuilder(" WHERE (");
        for (int i = 0; i < key; i++) {
            if (i > 0) condition.append(", ");
            condition.append(columns[i]);
        }
        condition.append(") = (");
        for (int i = 0; i < key; i++) {
            if (i > 0) condition.append(", ");
            condition.append("NEW.").append(columns[i]);
        }
        condition.append(")");
        
        string.append(condition).append(") DO INSTEAD UPDATE ").append(table).append(" SET ");
        for (int i = key; i < columns.length; i++) {
            if (i > key) string.append(", ");
            string.append(columns[i]).append(" = NEW.").append(columns[i]);
        }
        string.append(condition);
        statement.executeUpdate(string.toString());
    }
    
    @Override
    public void onInsertNotUpdate(@Nonnull Statement statement, @Nonnull String table) throws SQLException {
        statement.executeUpdate("DROP RULE IF EXISTS " + table + "_on_insert_update ON " + table);
    }
    
}
