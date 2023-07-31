import lombok.AllArgsConstructor;
import lombok.Getter;
import person.PersonGenerator;
import person.Person;
import person.PersonAPI;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class Main {
  private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");;

  public static void main(String[] args) {
    //check number of args
    if (args != null && args.length < 1) {
      System.err.println(Error.ARGUMENTS_NUMBER.getMessage());
      return;
    }

    //check Task is integer
    //check DB connection
    int task = 0;
    DataSource dataSource = null;
    try {
      task = Integer.parseInt(args[0]);
      dataSource = DbUtils.buildDataSource();
    }
    catch (NumberFormatException e) {
      System.err.println(Error.TASK_NOT_A_NUMBER.getMessage());
      return;
    }
    catch (SQLException e) {
      System.err.println(Error.DB_CONNECTION.getMessage());
      return;
    }

    //task choice
    switch (task) {
      case 1:
        try {
          PersonAPI.createPersonTable(dataSource);
        }
        catch (SQLException e) {
          System.err.println(e.getMessage());
          return;
        }
        System.out.println("Table \"person.Person\" has been created or already exists");
        break;

      case 2:
        if (args.length != 4) {
          System.err.println(Error.ARGUMENTS_NUMBER.getMessage());
          return;
        }

        Person person = null;
        try {
          PersonAPI.createPerson(dataSource,
              person = new Person(args[1], stringToDate(args[2]), Person.Gender.valueOf(args[3])));
        }
        catch (SQLException | IllegalArgumentException | ParseException e) {
          System.err.println(e.getMessage());
          return;
        }
        System.out.println("person.Person \"" + person + "\" has been created");
        break;

      case 3:
        List<Person> personList = null;
        try {
          personList = PersonAPI.findUniquePersons(dataSource);
        }
        catch (SQLException e) {
          System.err.println(e.getMessage());
          return;
        }

        if (personList.isEmpty()) {
          System.out.println("Table has no unique values");
        }
        else {
          personList.forEach(System.out::println);
          System.out.println("Count: " + personList.size());
        }
        break;

      case 4:
        int count = 1000000;
        int specialCount = 100;
        try {
          PersonAPI.createPersons(dataSource, count, PersonGenerator::generateDefaultPerson);
          PersonAPI.createPersons(dataSource, specialCount, PersonGenerator::generateSpecialPerson);
        }
        catch (SQLException e) {
          System.err.println(e.getMessage());
          return;
        }
        System.out.println(count + " persons created");
        System.out.println(specialCount + " special persons created");
        break;

      case 5:
        long startTime = System.currentTimeMillis();
        long endTime = 0;

        List<Person> specialPersonList = null;
        try {
          specialPersonList = PersonAPI.findSpecialPersons(dataSource);
          endTime = System.currentTimeMillis();
        }
        catch (SQLException e) {
          System.err.println(e.getMessage());
          return;
        }

        if (specialPersonList.isEmpty()) {
          System.out.println("Table has no special values (male gender, full name begins with \"F\")");
        }
        else {
          specialPersonList.forEach(System.out::println);
          System.out.println("Count: " + specialPersonList.size());
          System.out.println("Execution time: " + (endTime - startTime) + "ms");
        }
        break;

      default:
        System.err.println(Error.TASK_NOT_EXIST.getMessage());
    }
  }

  private static LocalDate stringToDate(String dateStr) throws ParseException {
    return LocalDate.parse(dateStr, DATE_FORMATTER);
  }

  @Getter
  @AllArgsConstructor
  private enum Error {
    ARGUMENTS_NUMBER ("Invalid input: invalid number of arguments"),
    TASK_NOT_A_NUMBER ("Invalid input: the task number is not an integer"),
    TASK_NOT_EXIST ("Invalid input: task doesn't exist"),
    DB_CONNECTION ("Unable to connect to the database");

    private final String message;
  }
}
