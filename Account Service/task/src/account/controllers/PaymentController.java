package account.controllers;

import account.models.Payment;
import account.services.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.*;

@RestController
public class PaymentController {

    private PaymentService paymentService;

    @Autowired
    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PutMapping("api/acct/payments")
    public ResponseEntity updateSalary(@Valid @RequestBody Payment payment) {

        Optional<Payment> optPayment = paymentService.findByEmployeeAndPeriod(payment.getEmployee(), payment.getPeriod());
        if (optPayment.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "");
        }

        Payment p = optPayment.get();
        p.setSalary(payment.getSalary());

        paymentService.save(p);

        Map<String,String> response = Map.of("status", "Updated successfully!");
        return new ResponseEntity(response, HttpStatus.OK);
    }

    @PostMapping("api/acct/payments")
    public ResponseEntity uploadSalaries(@Valid @RequestBody List<Payment> paymentList) {

        paymentService.checkListDuplicates(paymentList);

        for (Payment p : paymentList) {
            paymentService.checkIfUserExists(p);
            paymentService.checkPeriodFormat(p);
            paymentService.checkSalaryAmount(p);

            paymentService.save(new Payment(p.getEmployee(), p.getPeriod(), p.getSalary()));
        }

        Map<String,String> response = Map.of("status","Added successfully!");

        return new ResponseEntity(response, HttpStatus.OK);
    }
}
