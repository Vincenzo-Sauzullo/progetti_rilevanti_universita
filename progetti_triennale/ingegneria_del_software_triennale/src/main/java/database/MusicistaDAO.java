package database;

import entity.Cantante;
import entity.Musicista;
import entity.Strumentista;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


public class MusicistaDAO {
    /*
    create table Musicisti
    (
        id       INTEGER not null
            primary key autoincrement
            unique,
        nome     TEXT,
        cognome  TEXT,
        email    TEXT,
        password TEXT,
        telefono TEXT,
        cachet   INTEGER,
        tipo     TEXT
    )
        strict;
    */
    private final static DatabaseManager dbManager = DatabaseManager.getInstance();
    private final static Connection connection = dbManager.getConnection();

    public static void createMusicista(Musicista musicista) {
        try(PreparedStatement statement = connection.prepareStatement(
            "INSERT INTO Musicisti(nome, cognome, email, password, telefono, cachet, tipo) VALUES (?, ?, ?, ?, ?, ?, ?)",
            PreparedStatement.RETURN_GENERATED_KEYS
        )) {
            statement.setString(1, musicista.getNome());
            statement.setString(2, musicista.getCognome());
            statement.setString(3, musicista.getEmail());
            statement.setString(4, musicista.getPassword());
            statement.setString(5, musicista.getTelefono());
            statement.setInt(6, musicista.getCachetPerSerata());
            statement.setString(7, musicista.getClass().getSimpleName());

            statement.executeUpdate();

            ResultSet rs = statement.getGeneratedKeys();
            if(rs.next()) {
                final long id = rs.getLong(1);
                musicista.setId(id);
            }
        } catch(SQLException e) {
            //noinspection CallToPrintStackTrace
            e.printStackTrace();

            throw new RuntimeException(e);
        }

        DisponibilitaDAO.createDisponibilita(musicista);
    }

    public static Musicista readMusicista(long id) {
        try(PreparedStatement statement = connection.prepareStatement(
            "SELECT tipo FROM Musicisti WHERE id = ?"
        )) {
            statement.setLong(1, id);

            ResultSet rs = statement.executeQuery();

            if(rs.next()) {
                final String tipo = rs.getString("tipo");

                Musicista musicista = null;

                if(tipo.equals(Cantante.class.getSimpleName())) {
                    musicista = CantanteDAO.readCantante(id);
                } else if(tipo.equals(Strumentista.class.getSimpleName())) {
                    musicista = StrumentistaDAO.readStrumentista(id);
                }

                if(musicista == null) {
                    throw new NullPointerException();
                }

                musicista.setDisponibilita(DisponibilitaDAO.readDisponibilita(id));

                return musicista;
            }
        } catch(SQLException | NullPointerException e) {
            //noinspection CallToPrintStackTrace
            e.printStackTrace();

            throw new RuntimeException(e);
        }

        return null;
    }

    public static boolean updateMusicista(Musicista musicista) {
        try(PreparedStatement statement = connection.prepareStatement(
            "UPDATE Musicisti SET nome = ?, cognome = ?, email = ?, password = ?, telefono = ?, cachet = ?, tipo = ? WHERE id = ?"
        )) {
            statement.setString(1, musicista.getNome());
            statement.setString(2, musicista.getCognome());
            statement.setString(3, musicista.getEmail());
            statement.setString(4, musicista.getPassword());
            statement.setString(5, musicista.getTelefono());
            statement.setInt(6, musicista.getCachetPerSerata());
            statement.setString(7, musicista.getClass().getSimpleName());
            statement.setLong(8, musicista.getId());

            if(statement.executeUpdate() == 0) return false;
        } catch(SQLException e) {
            //noinspection CallToPrintStackTrace
            e.printStackTrace();

            throw new RuntimeException(e);
        }

        return DisponibilitaDAO.updateDisponibilita(musicista);
    }

    public static boolean deleteMusicista(Musicista musicista) {
        try(PreparedStatement statement = connection.prepareStatement(
            "DELETE FROM Musicisti WHERE id = ?"
        )) {
            statement.setLong(1, musicista.getId());

            if(!statement.execute()) return true;
        } catch(SQLException e) {
            //noinspection CallToPrintStackTrace
            e.printStackTrace();

            throw new RuntimeException(e);
        }

        return DisponibilitaDAO.deleteDisponibilita(musicista);
    }
}
