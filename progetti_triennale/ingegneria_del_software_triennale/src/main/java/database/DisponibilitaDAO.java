package database;

import entity.Musicista;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

public class DisponibilitaDAO {
    /*
    create table Disponibilità
    (
        musicista     integer not null
            constraint musicista
                references Musicisti
                on delete cascade,
        disponibilità TEXT    not null,
        constraint Disponibilità_pk
            primary key (musicista, disponibilità)
    );
    */

    private final static DatabaseManager dbManager = DatabaseManager.getInstance();
    private final static Connection connection = dbManager.getConnection();

    public static boolean createDisponibilita(Musicista musicista) {
        boolean ok = true;

        for(LocalDate disponibilita : musicista.getDisponibilita()) {
            try(PreparedStatement statement = connection.prepareStatement(
                "INSERT INTO Disponibilità(musicista, disponibilità) VALUES (?, ?)"
            )) {
                statement.setLong(1, musicista.getId());
                statement.setString(2, disponibilita.toString());

                ok = ok && (statement.executeUpdate() != 0);
            } catch(SQLException e) {
                //noinspection CallToPrintStackTrace
                e.printStackTrace();

                throw new RuntimeException(e);
            }
        }

        return ok;
    }

    public static List<LocalDate> readDisponibilita(long id) {
        try(PreparedStatement statement = connection.prepareStatement(
            "SELECT * FROM Disponibilità WHERE musicista = ?"
        )) {
            statement.setLong(1, id);
            ResultSet rs = statement.executeQuery();

            List<LocalDate> strumenti = new ArrayList<>();
            String raw;

            while(rs.next()) {
                raw = rs.getString("disponibilità");
                LocalDate disponibilita = LocalDate.parse(raw);

                strumenti.add(disponibilita);
            }

            return strumenti;
        } catch(SQLException | DateTimeParseException e) {
            //noinspection CallToPrintStackTrace
            e.printStackTrace();

            throw new RuntimeException(e);
        }
    }

    public static boolean updateDisponibilita(Musicista musicista) {
        return DisponibilitaDAO.deleteDisponibilita(musicista) && DisponibilitaDAO.createDisponibilita(musicista);
    }

    public static boolean deleteDisponibilita(Musicista musicista) {
        try(PreparedStatement statement = connection.prepareStatement(
            "DELETE FROM Disponibilità WHERE musicista = ?"
        )) {
            statement.setLong(1, musicista.getId());

            statement.execute();

            return true;
        } catch(SQLException e) {
            //noinspection CallToPrintStackTrace
            e.printStackTrace();

            throw new RuntimeException(e);
        }
    }
}
