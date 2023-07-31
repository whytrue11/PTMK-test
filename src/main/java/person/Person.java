package person;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Getter
@AllArgsConstructor
public class Person {
  private String fullName;
  private LocalDate birthDate;
  private Gender gender;

  @Override
  public String toString() {
    return "Full name: " + fullName +
        ", Birth date: " + birthDate +
        ", Gender: " + gender +
        ", Age: " + ChronoUnit.YEARS.between(birthDate, LocalDate.now());
  }

  public enum Gender {
    Male,
    Female
  }
}
