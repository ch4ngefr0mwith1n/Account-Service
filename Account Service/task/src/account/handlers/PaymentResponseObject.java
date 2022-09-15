package account.handlers;

import account.models.Payment;
import account.models.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@Data
public class PaymentResponseObject {

    private String name;
    private String lastname;
    private String period;
    private String salary;

    @JsonIgnore
    List<String> months = List.of("January", "February", "March", "April", "May", "June", "July", "August",
            "September", "October", "November", "December");

    public PaymentResponseObject(User user, Payment payment) {
        this.name = user.getName();
        this.lastname = user.getLastname();
        this.period = monthYearFormat(payment.getPeriod());
        this.salary = salaryFormat(String.valueOf(payment.getSalary()));
    }

    String monthYearFormat(String monthYear) {
        String year = monthYear.split("-")[1];

        Integer monthAsInt = Integer.parseInt(monthYear.split("-")[0]);
        if (monthAsInt < 1 || monthAsInt > 12) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "");
        }

        String month = months.get(monthAsInt - 1);

        return String.format("%s-%s", month, year);
    }

    String salaryFormat(String salary) {
        if (salary.length() <= 2) {
            return String.format("0 dollar(s) %s cent(s)", salary);
        }

        String dollars = salary.substring(0, salary.length() - 2);
        String cents = salary.substring(salary.length() - 2);

        return String.format("%s dollar(s) %s cent(s)", dollars, cents);
    }

}
