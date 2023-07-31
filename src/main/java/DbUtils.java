import org.postgresql.ds.PGSimpleDataSource;

import javax.sql.DataSource;
import java.sql.SQLException;

public class DbUtils {
  public static DataSource buildDataSource() throws SQLException {
    PGSimpleDataSource dataSource = new PGSimpleDataSource();
    dataSource.setUser("postgres");
    dataSource.setPassword("postgres");
    dataSource.setDatabaseName("postgres");
    dataSource.getConnection().close();
    return dataSource;
  }
}
