package ch.virtualid.database;

import ch.virtualid.annotations.Pure;
import ch.virtualid.client.Client;
import ch.virtualid.interfaces.Immutable;
import ch.virtualid.io.Console;
import ch.virtualid.io.Directory;
import com.mysql.jdbc.Driver;
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
import java.sql.Statement;
import java.util.Properties;
import javax.annotation.Nonnull;

/**
 * This class configures a MySQL database.
 * 
 * @author Kaspar Etter (kaspar.etter@virtualid.ch)
 * @version 2.0
 */
public final class MySQLConfiguration extends Configuration implements Immutable {
    
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
     * Creates a new MySQL configuration by reading the properties from the indicated file or from the user's input.
     * 
     * @param name the name of the database configuration file (without the suffix).
     * 
     * @require Client.isValid(name) : "The name is valid for clients.";
     */
    public MySQLConfiguration(@Nonnull String name) throws SQLException, IOException {
        super(new Driver());
        
        assert Client.isValid(name) : "The name is valid for clients.";
        
        final @Nonnull File file = new File(Directory.DATA.getPath() + Directory.SEPARATOR + name + ".conf");
        if (file.exists()) {
            try (@Nonnull FileInputStream stream = new FileInputStream(file); @Nonnull InputStreamReader reader = new InputStreamReader(stream, "UTF-8")) {
                properties.load(reader);
                server = properties.getProperty("Server", "localhost");
                port = properties.getProperty("Port", "3306");
                database = properties.getProperty("Database", "virtualid");
                user = properties.getProperty("User", "root");
                password = properties.getProperty("Password", "");
            }
        } else {
            Console.write();
            Console.write("The MySQL database is not yet configured. Please provide the following information:");
            server = Console.readString("- Server (e.g. \"localhost\"): ");
            port = Console.readString("- Port (the default is 3306): ");
            database = Console.readString("- Database (e.g. \"virtualid\"): ");
            user = Console.readString("- User (e.g. \"root\"): ");
            password = Console.readString("- Password: ");
            
            properties.setProperty("Server", server);
            properties.setProperty("Port", port);
            properties.setProperty("Database", database);
            properties.setProperty("User", user);
            properties.setProperty("Password", password);
            
            try (@Nonnull FileOutputStream stream = new FileOutputStream(file); @Nonnull OutputStreamWriter writer = new OutputStreamWriter(stream, "UTF-8")) {
                properties.store(writer, "Configuration of the MySQL database");
            }
        }
        
        properties.clear();
        properties.setProperty("user", user);
        properties.setProperty("password", password);
        
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://" + server + ":" + port, properties); Statement statement = connection.createStatement()) {
            statement.executeUpdate("CREATE DATABASE IF NOT EXISTS " + database);
        }
    }
    
    /**
     * Creates a new MySQL configuration by reading the properties from the default file or from the user's input.
     */
    public MySQLConfiguration() throws SQLException, IOException {
        this("MySQL");
    }
    
    
    @Pure
    @Override
    protected @Nonnull String getURL() {
        return "jdbc:mysql://" + server + ":" + port + "/" + database + "?rewriteBatchedStatements=true";
    }
    
    @Pure
    @Override
    protected @Nonnull Properties getProperties() {
        return properties;
    }
    
    
    @Pure
    @Override
    public @Nonnull String PRIMARY_KEY() {
        return "BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY";
    }
    
    @Pure
    @Override
    public @Nonnull String TINYINT() {
        return "TINYINT";
    }
    
    @Pure
    @Override
    public @Nonnull String BINARY() {
        return "utf16_bin";
    }
    
    @Pure
    @Override
    public @Nonnull String NOCASE() {
        return "utf16_general_ci";
    }
    
    @Pure
    @Override
    public @Nonnull String CITEXT() {
        return "TEXT";
    }
    
    @Pure
    @Override
    public @Nonnull String BLOB() {
        return "LONGBLOB";
    }
    
    @Pure
    @Override
    public @Nonnull String REPLACE() {
        return "REPLACE";
    }
    
    @Pure
    @Override
    public @Nonnull String IGNORE() {
        return " IGNORE";
    }
    
    @Pure
    @Override
    public @Nonnull String GREATEST() {
        return "GREATEST";
    }
    
    @Pure
    @Override
    public @Nonnull String CURRENT_TIME() {
        return "UNIX_TIMESTAMP(SYSDATE()) * 1000 + MICROSECOND(SYSDATE(3)) DIV 1000";
    }
    
    
    @Override
    public long executeInsert(@Nonnull Statement statement, @Nonnull String SQL) throws SQLException {
        statement.executeUpdate(SQL, Statement.RETURN_GENERATED_KEYS);
        try (@Nonnull ResultSet resultSet = statement.getGeneratedKeys()) {
            if (resultSet.next()) return resultSet.getLong(1);
            else throw new SQLException("The given SQL statement did not generate a key.");
        }
    }
    
}
