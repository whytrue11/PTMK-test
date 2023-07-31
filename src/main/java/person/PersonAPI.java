package person;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class PersonAPI {
  private static final String CREATE_PERSON_TABLE_QUERY =
      "CREATE TABLE IF NOT EXISTS Person (" +
          "full_name varchar(128)," +
          "birth_date date," +
          "gender varchar(128))";

  private static final String CREATE_PERSON_QUERY =
      "INSERT INTO Person " +
          "VALUES (?, ?, ?)";

  private static final String SELECT_PERSONS_QUERY =
      "SELECT DISTINCT ON (full_name, birth_date) * from person ORDER BY full_name";

  private static final String SELECT_SPECIAL_PERSONS_QUERY =
      "SELECT * from person WHERE gender = 'Male' AND full_name LIKE 'F%'";

  public static void createPersonTable(DataSource dataSource) throws SQLException {
    try (Connection connection = dataSource.getConnection();
         Statement statement = connection.createStatement()) {
      statement.execute(CREATE_PERSON_TABLE_QUERY);
    }
  }

  public static void createPerson(DataSource dataSource, Person person)
      throws SQLException, IllegalArgumentException {
    try (Connection connection = dataSource.getConnection();
         PreparedStatement insertPersonStatement = connection.prepareStatement(CREATE_PERSON_QUERY)) {

      insertPersonStatement.setString(1, person.getFullName());
      insertPersonStatement.setDate(2, Date.valueOf(person.getBirthDate()));
      insertPersonStatement.setString(3, person.getGender().toString());

      insertPersonStatement.executeUpdate();
    }
  }

  public static void createPersons(DataSource dataSource, int count, Supplier<Person> generatePerson) throws SQLException {
    try (Connection connection = dataSource.getConnection();
         PreparedStatement insertPersonStatement = connection.prepareStatement(CREATE_PERSON_QUERY)) {

      int BATCH_SIZE = 250;
      connection.setAutoCommit(false);
      int queryCounter = 0;
      for (int i = 0; i < count; ++i) {
        Person person = generatePerson.get();
        insertPersonStatement.setString(1, person.getFullName());
        insertPersonStatement.setDate(2, Date.valueOf(person.getBirthDate()));
        insertPersonStatement.setString(3, person.getGender().toString());
        insertPersonStatement.addBatch();
        ++queryCounter;
        if (queryCounter == BATCH_SIZE) {
          try {
            insertPersonStatement.executeBatch();
            connection.commit();
          }
          catch (BatchUpdateException e) {
            connection.rollback();
            throw e;
          }
          queryCounter = 0;
        }
      }
      if (queryCounter != 0) {
        try {
          insertPersonStatement.executeBatch();
          connection.commit();
        }
        catch (BatchUpdateException e) {
          connection.rollback();
          throw e;
        }
      }

      connection.setAutoCommit(true);
    }
  }

  //unique value of full name + date
  public static List<Person> findUniquePersons(DataSource dataSource) throws SQLException {
    return findRequest(dataSource, SELECT_PERSONS_QUERY);
  }

  //male gender, full name begins with "F"
  public static List<Person> findSpecialPersons(DataSource dataSource) throws SQLException {
    return findRequest(dataSource, SELECT_SPECIAL_PERSONS_QUERY);
  }

  private static List<Person> findRequest(DataSource dataSource, String selectPersonsQuery) throws SQLException {
    try (Connection connection = dataSource.getConnection();
         PreparedStatement selectStatement = connection.prepareStatement(selectPersonsQuery);
         ResultSet rs = selectStatement.executeQuery()) {
      List<Person> result = new ArrayList<>();
      while (rs.next()) {
        result.add(new Person(
            rs.getString(1),
            rs.getDate(2).toLocalDate(),
            Person.Gender.valueOf(rs.getString(3))
        ));
      }
      return result;
    }
  }
}
