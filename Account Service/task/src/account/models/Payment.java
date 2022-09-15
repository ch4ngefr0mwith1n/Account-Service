package account.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.Objects;

@NoArgsConstructor
@AllArgsConstructor
@Data

@Entity
@Table(name = "payments")
public class Payment {

    private static final String emailCheck = "([a-zA-Z0-9]+)([.{1}])?([a-zA-Z0-9]+)@acme([.])com";
    private static final String dateCheck = "(0[1-9]|1[0-2])-\\d\\d\\d\\d";

    public Payment(String employee, String period, Long salary) {
        this.employee = employee;
        this.period = period;
        this.salary = salary;
        this.monthYear = convertToLocalDate(period);
    }

    @JsonIgnore
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotBlank
    @Email
    @Pattern(regexp = emailCheck)
    private String employee;

    @NotBlank
    @Pattern(regexp = dateCheck, message = "Wrong date")
    private String period;

    @NotNull
    private Long salary;

    @JsonIgnore
    @Column(columnDefinition = "DATE")
    private LocalDate monthYear;
    // convertToLocalDate(period)

    private LocalDate convertToLocalDate(String period) {

        Integer month = Integer.parseInt(period.split("-")[0]);
        Integer year = Integer.parseInt(period.split("-")[1]);

        LocalDate startOfTheMonth = LocalDate.of(year, month, 1);

        return startOfTheMonth;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Payment payment = (Payment) o;
        return employee.equals(payment.employee) && period.equals(payment.period);
    }

    @Override
    public int hashCode() {
        return Objects.hash(employee, period);
    }
}
