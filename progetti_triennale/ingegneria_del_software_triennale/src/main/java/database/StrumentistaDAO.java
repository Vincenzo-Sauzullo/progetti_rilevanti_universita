package database;

import entity.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class StrumentistaDAO {
    /*
    create table Strumentisti
    (
        id INTEGER not null
            primary key
            unique
            constraint id
                references Musicisti
                on delete cascade
    )
        strict;
    */

    private final static DatabaseManager dbManager = DatabaseManager.getInstance();
    private final static Connection connection = dbManager.getConnection();

    public static boolean createStrumentista(Strumentista strumentista) {
        MusicistaDAO.createMusicista(strumentista);

        try(PreparedStatement statement = connection.prepareStatement(
            "INSERT INTO Strumentisti(id) VALUES (?)",
                PreparedStatement.RETURN_GENERATED_KEYS
        )) {
            statement.setLong(1, strumentista.getId());

            if(!statement.execute()) return false;
        } catch(SQLException e) {
            //noinspection CallToPrintStackTrace
            e.printStackTrace();

            throw new RuntimeException(e);
        }

        return StrumentiDAO.createStrumenti(strumentista);
    }

    // read
    public static Strumentista readStrumentista(long id) {
        try(PreparedStatement statement = connection.prepareStatement(
            "SELECT * FROM Musicisti NATURAL JOIN Strumentisti WHERE id = ?"
        )) {
            statement.setLong(1, id);

            ResultSet rs = statement.executeQuery();

            List<LocalDate> disponibilita = DisponibilitaDAO.readDisponibilita(id);

            List<Strumento> strumenti = StrumentiDAO.readStrumenti(id);

            if(rs.next()) {
                return new Strumentista(
                    rs.getLong("id"),
                    rs.getString("nome"),
                    rs.getString("cognome"),
                    rs.getString("email"),
                    rs.getString("password"),
                    rs.getString("telefono"),
                    rs.getInt("cachet"),
                    disponibilita,
                    strumenti
                );
            }

            return null;
        } catch(SQLException e) {
            //noinspection CallToPrintStackTrace
            e.printStackTrace();

            throw new RuntimeException(e);
        }
    }

    // update
    public static boolean updateStrumentista(Strumentista strumentista) {
        return MusicistaDAO.updateMusicista(strumentista) && StrumentiDAO.updateStrumenti(strumentista);
    }

    public static boolean deleteStrumentista(Strumentista strumentista) {
        return MusicistaDAO.deleteMusicista(strumentista);
    }

    public static List<Strumentista> getAllStrumentisti() {
        try(PreparedStatement statement = connection.prepareStatement(
            "SELECT * FROM Musicisti NATURAL JOIN Strumentisti"
        )) {
            ResultSet rs = statement.executeQuery();

            long id;
            List<LocalDate> disponibilita;
            List<Strumentista> strumentisti = new ArrayList<>();
            Strumentista strumentista;
            List<Strumento> strumenti;

            while(rs.next()) {
                id = rs.getLong("id");

                disponibilita = DisponibilitaDAO.readDisponibilita(id);

                strumenti = StrumentiDAO.readStrumenti(id);

                strumentista = new Strumentista(
                    id,
                    rs.getString("nome"),
                    rs.getString("cognome"),
                    rs.getString("email"),
                    rs.getString("password"),
                    rs.getString("telefono"),
                    rs.getInt("cachet"),
                    disponibilita,
                    strumenti
                );
                strumentisti.add(strumentista);
            }

            return strumentisti;
        } catch(SQLException e) {
            //noinspection CallToPrintStackTrace
            e.printStackTrace();

            throw new RuntimeException(e);
        }
    }
}
