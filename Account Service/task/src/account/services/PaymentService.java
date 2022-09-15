package account.services;

import account.models.Payment;
import account.repositories.PaymentRepository;
import account.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

@Service
public class PaymentService {

    private PaymentRepository paymentRepository;
    private UserRepository userRepository;

    @Autowired
    public PaymentService(PaymentRepository paymentRepository, UserRepository userRepository) {
        this.paymentRepository = paymentRepository;
        this.userRepository = userRepository;
    }

    public Optional<Payment> findByEmployeeAndPeriod(String employee, String period) {
        return paymentRepository.findByEmployeeAndPeriod(employee, period);
    }

    public void save(Payment payment) {
        paymentRepository.save(payment);
    }

    public void checkIfUserExists(Payment payment) {
        String userEmail = payment.getEmployee();
        if (userRepository.findByEmail(userEmail).isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "");
        }
    }

    public void checkSalaryAmount(Payment payment) {
        if (payment.getSalary() < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "");
        }
    }

    public void checkPeriodFormat(Payment payment) {
        String dateCheck = "(0[1-9]|1[0-2])-\\d\\d\\d\\d";
        if (!payment.getPeriod().matches(dateCheck)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "");
        }
    }

    public void checkListDuplicates(List<Payment> paymentList) {
        Set<Payment> paymentSet = new HashSet<>(paymentList);
        if (paymentSet.size() != paymentList.size() || !paymentSet.containsAll(paymentList)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "");
        }
    }
}
