package database;

import controller.GestioneEventi;
import org.sqlite.SQLiteConfig;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class DatabaseManager {
    private final String dbPath;
    private Connection connection;

    private static final Map<String, DatabaseManager> instances = new HashMap<>();
    private static DatabaseManager defaultInstance = null;

    private DatabaseManager(String dbPath) {
        this.dbPath = dbPath;
    }

    public static synchronized DatabaseManager getInstance(String dbPath) {
        DatabaseManager instance = new DatabaseManager(dbPath);
        if(!instances.containsKey(dbPath)) {
            instances.put(dbPath, instance);
            instance.initialize();
        }
        return instances.get(dbPath);
    }

    public static DatabaseManager getInstance() {
        return defaultInstance != null ? defaultInstance : getInstance("jdbc:sqlite:db.sqlite");
    }

    public static void setDefaultInstance(DatabaseManager instance) {
        defaultInstance = instance;
    }

    public Connection getConnection() {
        if(connection == null) {
            final String dbPath = this.dbPath;
            try {
                SQLiteConfig config = new SQLiteConfig();
                // Abilitare questa opzione consente di far funzionare il "ON DELETE CASCADE" come previsto da schema.
                config.enforceForeignKeys(true);

                this.connection = DriverManager.getConnection(dbPath, config.toProperties());
            } catch(SQLException e) {
                //noinspection CallToPrintStackTrace
                e.printStackTrace();

                throw new RuntimeException(e);
            }
        }
        return connection;
    }

    public void initialize() {
        InputStream input = GestioneEventi.class.getClassLoader().getResourceAsStream("schema.sql");

        if(input == null) throw new RuntimeException("schema.sql non trovato");

        StringBuilder sb = new StringBuilder();
        try(BufferedReader reader = new BufferedReader(new InputStreamReader(input))) {
            String line;

            while((line = reader.readLine()) != null) {
                sb.append(line);
            }

            try(Statement statement = getConnection().createStatement()) {
                statement.executeUpdate(sb.toString());
            } catch(SQLException e) {
                //noinspection CallToPrintStackTrace
                e.printStackTrace();

                throw new RuntimeException(e);
            }
        } catch(IOException e) {
            System.err.println("Errore nella lettura del file: " + e.getMessage());
        }

    }
}
