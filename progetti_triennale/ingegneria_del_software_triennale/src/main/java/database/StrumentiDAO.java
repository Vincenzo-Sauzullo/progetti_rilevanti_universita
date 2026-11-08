package database;

import entity.Strumentista;
import entity.Strumento;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class StrumentiDAO {
    /*
    create table Strumenti
    (
        strumentista INTEGER not null
            constraint id
                references Strumentisti
                on delete cascade,
        strumento    TEXT    not null,
        primary key (strumentista, strumento)
    )
        strict;
    */

    private final static DatabaseManager dbManager = DatabaseManager.getInstance();
    private final static Connection connection = dbManager.getConnection();

    public static boolean createStrumenti(Strumentista strumentista) {
        boolean ok = true;

        for(Strumento strumento : strumentista.getStrumenti()) {
            try(PreparedStatement statement = connection.prepareStatement(
                "INSERT INTO Strumenti(strumentista, strumento) VALUES (?, ?)"
            )) {
                statement.setLong(1, strumentista.getId());
                statement.setString(2, strumento.toString());

                ok = ok && (statement.executeUpdate() != 0);
            } catch(SQLException e) {
                //noinspection CallToPrintStackTrace
                e.printStackTrace();

                throw new RuntimeException(e);
            }
        }

        return ok;
    }

    public static List<Strumento> readStrumenti(long id) {
        try(PreparedStatement statement = connection.prepareStatement(
            "SELECT * FROM Strumenti WHERE strumentista = ?"
        )) {
            statement.setLong(1, id);
            ResultSet rs = statement.executeQuery();

            List<Strumento> strumenti = new ArrayList<>();
            String raw;

            while(rs.next()) {
                raw = rs.getString("strumento");
                Strumento strumento = Strumento.valueOf(raw);

                strumenti.add(strumento);
            }

            return strumenti;
        } catch(SQLException e) {
            //noinspection CallToPrintStackTrace
            e.printStackTrace();

            throw new RuntimeException(e);
        }
    }

    public static boolean updateStrumenti(Strumentista strumentista) {
        return StrumentiDAO.deleteStrumenti(strumentista) && StrumentiDAO.createStrumenti(strumentista);
    }

    public static boolean deleteStrumenti(Strumentista strumentista) {
        try(PreparedStatement statement = connection.prepareStatement(
            "DELETE FROM Strumenti WHERE strumentista = ?"
        )) {
            statement.setLong(1, strumentista.getId());

            statement.execute();

            return true;
        } catch(SQLException e) {
            //noinspection CallToPrintStackTrace
            e.printStackTrace();

            throw new RuntimeException(e);
        }
    }
}
