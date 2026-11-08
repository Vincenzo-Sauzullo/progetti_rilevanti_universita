package database;

import entity.Evento;
import entity.Musicista;
import entity.Partecipazione;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PartecipazioneDAO {
    /*
    create table Partecipazioni
    (
        musicista  INTEGER           not null
            references Musicisti
                on delete cascade,
        evento     INTEGER           not null
            references Eventi
                on delete cascade,
        costo      INTEGER,
        confermato int default false not null,
        primary key (musicista, evento)
    )
        strict;
    */
    private final static DatabaseManager dbManager = DatabaseManager.getInstance();
    private final static Connection connection = dbManager.getConnection();

    public static boolean createPartecipazioni(Evento evento) {
        boolean ok = true;

        for(Partecipazione partecipazione : evento.getPartecipanti())
            ok = ok && PartecipazioneDAO.createPartecipazioni(partecipazione);

        return ok;
    }

    public static boolean createPartecipazioni(Partecipazione partecipazione) {
        try(PreparedStatement statement = connection.prepareStatement(
            "INSERT INTO Partecipazioni(musicista, evento, costo, confermato) VALUES (?, ?, ?, ?)"
        )) {
            statement.setLong(1, partecipazione.getMusicista().getId());
            statement.setLong(2, partecipazione.getEvento().getId());
            statement.setInt(3, partecipazione.getCosto());
            statement.setBoolean(4, partecipazione.isConfermato());

            statement.execute();

            return true;
        } catch(SQLException e) {
            //noinspection CallToPrintStackTrace
            e.printStackTrace();

            throw new RuntimeException(e);
        }
    }

    public static void updatePartecipazione(Partecipazione partecipazione) {
        try(PreparedStatement statement = connection.prepareStatement(
            "UPDATE Partecipazioni SET confermato = ? WHERE musicista = ? AND evento = ?",
            PreparedStatement.RETURN_GENERATED_KEYS
        )) {
            statement.setBoolean(1, partecipazione.isConfermato());
            statement.setLong(2, partecipazione.getMusicista().getId());
            statement.setLong(3, partecipazione.getEvento().getId());

            statement.execute();
        } catch(SQLException e) {
            //noinspection CallToPrintStackTrace
            e.printStackTrace();

            throw new RuntimeException(e);
        }
    }

    public static boolean deletePartecipazione(Partecipazione partecipazione) {
        try(PreparedStatement statement = connection.prepareStatement(
            "DELETE FROM Partecipazioni WHERE musicista = ? AND evento = ?"
        )) {
            statement.setLong(1, partecipazione.getMusicista().getId());
            statement.setLong(2, partecipazione.getEvento().getId());

            statement.execute();

            return true;
        } catch(SQLException e) {
            //noinspection CallToPrintStackTrace
            e.printStackTrace();

            throw new RuntimeException(e);
        }
    }

    public static Set<Partecipazione> getPartecipazioni(Evento evento) {
        try(PreparedStatement statement = connection.prepareStatement(
            "SELECT * FROM Partecipazioni WHERE evento = ?",
            PreparedStatement.RETURN_GENERATED_KEYS
        )) {
            statement.setLong(1, evento.getId());

            ResultSet rs = statement.executeQuery();

            Set<Partecipazione> partecipazioni = new HashSet<>();
            Partecipazione partecipazione;
            Musicista musicista;

            while(rs.next()) {
                musicista = MusicistaDAO.readMusicista(rs.getLong("musicista"));

                partecipazione = new Partecipazione(
                    musicista,
                    evento,
                    rs.getInt("costo"),
                    rs.getBoolean("confermato")
                );

                partecipazioni.add(partecipazione);
            }

            return partecipazioni;
        } catch(SQLException e) {
            //noinspection CallToPrintStackTrace
            e.printStackTrace();

            throw new RuntimeException(e);
        }
    }

    public static List<Partecipazione> getPartecipazioni(Musicista musicista) {
        try(PreparedStatement statement = connection.prepareStatement(
            "SELECT * FROM Partecipazioni WHERE musicista = ?",
            PreparedStatement.RETURN_GENERATED_KEYS
        )) {
            statement.setLong(1, musicista.getId());

            ResultSet rs = statement.executeQuery();

            List<Partecipazione> partecipazioni = new ArrayList<>();
            Partecipazione partecipazione;
            Evento evento;

            while(rs.next()) {
                evento = EventoDAO.readEvento(rs.getLong("evento"));

                partecipazione = new Partecipazione(
                    musicista,
                    evento,
                    rs.getInt("costo"),
                    rs.getBoolean("confermato")
                );

                partecipazioni.add(partecipazione);
            }

            return partecipazioni;
        } catch(SQLException e) {
            //noinspection CallToPrintStackTrace
            e.printStackTrace();

            throw new RuntimeException(e);
        }
    }
}
