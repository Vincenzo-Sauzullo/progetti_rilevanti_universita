package database;

import entity.*;

import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class EventoDAO {
    /*
    create table Eventi
    (
        id           INTEGER
            primary key
            unique,
        data         TEXT,
        luogo        TEXT,
        costo_totale INTEGER,
        tipologia    TEXT,
        stato        TEXT
    )
        strict;
    */

    private final static DatabaseManager dbManager = DatabaseManager.getInstance();
    private final static Connection connection = dbManager.getConnection();

    private final static Map<Long, Evento> cache = new HashMap<>();

    // create
    public static boolean createEvento(Evento evento) {
        try(PreparedStatement statement = connection.prepareStatement(
            "INSERT INTO Eventi(data, luogo, costo_totale, tipologia, stato) VALUES (?, ?, ?, ?, ?)",
            PreparedStatement.RETURN_GENERATED_KEYS
        )) {
            statement.setString(1, evento.getData().toString());
            statement.setString(2, evento.getLuogo());
            statement.setInt(3, evento.getCostoTotale());
            statement.setString(4, evento.getTipologia().toString());
            statement.setString(5, evento.getStato().toString());

            if(statement.executeUpdate() == 0)
                return false;

            ResultSet rs = statement.getGeneratedKeys();
            if(!rs.next())
                return false;

            final long id = rs.getLong(1);
            evento.setId(id);
            cache.put(id, evento);
        } catch(SQLException e) {
            //noinspection CallToPrintStackTrace
            e.printStackTrace();

            throw new RuntimeException(e);
        }

        return PartecipazioneDAO.createPartecipazioni(evento);
    }

    // read
    public static Evento readEvento(long id) {
        if(cache.containsKey(id)) return cache.get(id);

        try(PreparedStatement statement = connection.prepareStatement(
            "SELECT * FROM Eventi WHERE id = ?"
        )) {
            statement.setLong(1, id);

            ResultSet rs = statement.executeQuery();

            if(rs.next()) {
                Evento evento = new Evento(
                    rs.getLong("id"),
                    LocalDate.parse(rs.getString("data")),
                    rs.getString("luogo"),
                    rs.getInt("costo_totale"),
                    TipologiaEvento.valueOf(rs.getString("tipologia")),
                    StatoEvento.valueOf(rs.getString("stato")),
                    null
                );

                Set<Partecipazione> partecipanti = PartecipazioneDAO.getPartecipazioni(evento);

                evento.setPartecipanti(partecipanti);

                return evento;
            }

            return null;
        } catch(SQLException | DateTimeParseException e) {
            //noinspection CallToPrintStackTrace
            e.printStackTrace();

            throw new RuntimeException(e);
        }
    }

    public static Set<Evento> readAllEventi() {
        try(PreparedStatement statement = connection.prepareStatement(
            "SELECT * FROM Eventi"
        )) {
            ResultSet rs = statement.executeQuery();

            Set<Evento> eventi = new HashSet<>();
            while(rs.next()) {
                Evento evento = new Evento(
                    rs.getLong("id"),
                    LocalDate.parse(rs.getString("data")),
                    rs.getString("luogo"),
                    rs.getInt("costo_totale"),
                    TipologiaEvento.valueOf(rs.getString("tipologia")),
                    StatoEvento.valueOf(rs.getString("stato")),
                    null
                );

                Set<Partecipazione> partecipanti = PartecipazioneDAO.getPartecipazioni(evento);

                evento.setPartecipanti(partecipanti);

                eventi.add(evento);

                cache.put(evento.getId(), evento);
            }

            return eventi;
        } catch (SQLException | DateTimeParseException e) {
            //noinspection CallToPrintStackTrace
            e.printStackTrace();

            throw new RuntimeException(e);
        }
    }

    // update
    public static boolean updateEvento(Evento evento) {
        try(PreparedStatement statement = connection.prepareStatement(
            "UPDATE Eventi SET data = ?, luogo = ?, costo_totale = ?, tipologia = ?, stato = ? WHERE id = ?",
            PreparedStatement.RETURN_GENERATED_KEYS
        )) {
            statement.setString(1, evento.getData().toString());
            statement.setString(2, evento.getLuogo());
            statement.setInt(3, evento.getCostoTotale());
            statement.setString(4, evento.getTipologia().toString());
            statement.setString(5, evento.getStato().toString());
            statement.setLong(6, evento.getId());

            statement.execute();
        } catch(SQLException e) {
            //noinspection CallToPrintStackTrace
            e.printStackTrace();

            throw new RuntimeException(e);
        }

        // azione: PartecipazioneDAO.updatePartecipazione(partecipazione);
        Set<Partecipazione> inDb = PartecipazioneDAO.getPartecipazioni(evento);
        Set<Partecipazione> inMemory = evento.getPartecipanti();
        Set<Partecipazione> both = Stream.concat(inDb.stream(), inMemory.stream()).collect(Collectors.toUnmodifiableSet());

        both.forEach(p -> {
            if(!inDb.contains(p) || !inMemory.contains(p)) {
                if(inDb.contains(p)) PartecipazioneDAO.deletePartecipazione(p);
                else PartecipazioneDAO.createPartecipazioni(p);
            }
        });

        cache.put(evento.getId(), evento);

        return true;
    }

    // delete
    public static boolean deleteEvento(Evento evento) {
        cache.remove(evento.getId());

        try(PreparedStatement statement = connection.prepareStatement(
            "DELETE FROM Eventi WHERE id = ?"
        )) {
            statement.setLong(1, evento.getId());

            return true;
        } catch(SQLException e) {
            //noinspection CallToPrintStackTrace
            e.printStackTrace();

            throw new RuntimeException(e);
        }
    }
}
