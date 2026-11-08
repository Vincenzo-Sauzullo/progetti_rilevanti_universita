package database;

import entity.Cantante;
import entity.GenereMusicale;
import entity.RegistroVocale;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class CantanteDAO {
    /*
    create table Cantanti
    (
        id              INTEGER not null
            primary key
            unique
            references Musicisti
                on delete cascade,
        genere_musicale TEXT,
        registro_vocale TEXT
    )
    strict;
    */

    private final static DatabaseManager dbManager = DatabaseManager.getInstance();
    private final static Connection connection = dbManager.getConnection();

    public static boolean createCantante(Cantante cantante) {
        MusicistaDAO.createMusicista(cantante);

        try(PreparedStatement statement = connection.prepareStatement(
            "INSERT INTO Cantanti(id, genere_musicale, registro_vocale) VALUES (?, ?, ?)",
            PreparedStatement.RETURN_GENERATED_KEYS
        )) {
            statement.setLong(1, cantante.getId());
            statement.setString(2, cantante.getGenereMusicale().toString());
            RegistroVocale registroVocale = cantante.getRegistroVocale();
            statement.setString(3, registroVocale == null ? null : registroVocale.toString());

            return statement.executeUpdate() != 0;
        } catch(SQLException e) {
            //noinspection CallToPrintStackTrace
            e.printStackTrace();

            throw new RuntimeException(e);
        }
    }

    public static Cantante readCantante(long id) {
        try(PreparedStatement statement = connection.prepareStatement(
            "SELECT * FROM Musicisti NATURAL JOIN Cantanti WHERE id = ?"
        )) {
            statement.setLong(1, id);

            ResultSet rs = statement.executeQuery();

            String raw;
            GenereMusicale genereMusicale;
            RegistroVocale registroVocale;

            if(rs.next()) {
                List<LocalDate> disponibilita = DisponibilitaDAO.readDisponibilita(id);

                raw = rs.getString("genere_musicale");
                genereMusicale = raw == null ? null : GenereMusicale.valueOf(raw);

                raw = rs.getString("registro_vocale");
                registroVocale = raw == null ? null : RegistroVocale.valueOf(raw);

                return new Cantante(
                    rs.getLong("id"),
                    rs.getString("nome"),
                    rs.getString("cognome"),
                    rs.getString("email"),
                    rs.getString("password"),
                    rs.getString("telefono"),
                    rs.getInt("cachet"),
                    disponibilita,
                    genereMusicale,
                    registroVocale
                );
            }

            return null;
        } catch(SQLException e) {
            //noinspection CallToPrintStackTrace
            e.printStackTrace();

            throw new RuntimeException(e);
        }
    }

    public static boolean updateCantante(Cantante cantante) {
        if(!MusicistaDAO.updateMusicista(cantante))
            return false;

        try(PreparedStatement statement = connection.prepareStatement(
            "UPDATE Cantanti SET genere_musicale = ?, registro_vocale = ? WHERE id = ?"
        )) {
            statement.setString(1, cantante.getGenereMusicale().toString());
            statement.setString(2, cantante.getRegistroVocale().toString());
            statement.setLong(3, cantante.getId());

            return statement.executeUpdate() != 0;
        } catch(SQLException e) {
            //noinspection CallToPrintStackTrace
            e.printStackTrace();

            throw new RuntimeException(e);
        }
    }

    public static boolean deleteCantante(Cantante cantante) {
        return MusicistaDAO.deleteMusicista(cantante);
    }

    public static List<Cantante> getAllCantanti() {
        try(PreparedStatement statement = connection.prepareStatement(
            "SELECT * FROM Musicisti NATURAL JOIN Cantanti"
        )) {
            ResultSet rs = statement.executeQuery();

            long id;
            List<LocalDate> disponibilita;
            List<Cantante> cantanti = new ArrayList<>();
            Cantante cantante;
            String raw;
            GenereMusicale genereMusicale;
            RegistroVocale registroVocale;

            while(rs.next()) {
                id = rs.getLong("id");

                disponibilita = DisponibilitaDAO.readDisponibilita(id);

                raw = rs.getString("genere_musicale");
                genereMusicale = raw == null ? null : GenereMusicale.valueOf(raw);

                raw = rs.getString("registro_vocale");
                registroVocale = raw == null ? null : RegistroVocale.valueOf(raw);

                cantante = new Cantante(
                    id,
                    rs.getString("nome"),
                    rs.getString("cognome"),
                    rs.getString("email"),
                    rs.getString("password"),
                    rs.getString("telefono"),
                    rs.getInt("cachet"),
                    disponibilita,
                    genereMusicale,
                    registroVocale
                );
                cantanti.add(cantante);
            }

            return cantanti;
        } catch(SQLException e) {
            //noinspection CallToPrintStackTrace
            e.printStackTrace();

            throw new RuntimeException(e);
        }
    }
}
