package person;

import java.time.LocalDate;

public class PersonGenerator {
  private static final int MIN_NAME_LENGTH = 2;
  private static final int MAX_NAME_LENGTH = 5;

  private static final int MIN_BIRTH_YEAR = 1950;
  private static final int MAX_BIRTH_YEAR = 2020;

  public static Person generateDefaultPerson() {
    return new Person(
        generateFullName(),
        LocalDate.now().withYear(generateInt(MAX_BIRTH_YEAR, MIN_BIRTH_YEAR)),
        Person.Gender.values()[(int) (Math.random() * Person.Gender.values().length)]
    );
  }

  //male gender, full name begins with "F"
  public static Person generateSpecialPerson() {
    return new Person(
        "F" + generateFullName().substring(1),
        LocalDate.now().withYear(generateInt(MAX_BIRTH_YEAR, MIN_BIRTH_YEAR)),
        Person.Gender.Male
    );
  }

  private static String generateName() {
    int length = generateInt(MAX_NAME_LENGTH, MIN_NAME_LENGTH);
    StringBuilder result = new StringBuilder();

    result.append(Character.toUpperCase((char) generateInt(122, 97)));
    --length;
    for (int i = 0; i < length; ++i) {
      result.append((char) generateInt(122, 97));
    }
    return result.toString();
  }

  private static String generateFullName() {
    StringBuilder result = new StringBuilder();
    for (int i = 0; i < 2; ++i) {
      result.append(generateName()).append(" ");
    }
    result.append(generateName());
    return result.toString();
  }

  private static int generateInt(int max, int min) {
    return (int) (Math.random() * (max - min + 1) + min);
  }
}
