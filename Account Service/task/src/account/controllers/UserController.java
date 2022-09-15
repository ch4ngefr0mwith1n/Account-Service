package account.controllers;

import account.handlers.NewPasswordHandler;
import account.handlers.PaymentResponseObject;
import account.models.Payment;
import account.models.User;
import account.repositories.PaymentRepository;
import account.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
public class UserController {
    private UserService userService;
    private PaymentRepository paymentRepository;

    @Autowired
    public UserController(UserService userService, PaymentRepository paymentRepository) {
        this.userService = userService;
        this.paymentRepository = paymentRepository;
    }

    @PostMapping("api/auth/changepass")
    public ResponseEntity changePassword(@Valid @RequestBody NewPasswordHandler newPasswordHandler,
                                         @AuthenticationPrincipal UserDetails userDetails) {

        User currentUser = userService.findUserByEmail(userDetails.getUsername());

        // provjera za trenutnu lozinku:
        userService.passwordLengthCheck(currentUser.getPassword());
        userService.breachedPasswordCheck(currentUser.getPassword(), userService.getBreachedPasswords());

        // vađenje lozinke iz "handler" objekta:
        String newPassword = newPasswordHandler.getNew_password();

        userService.samePasswordCheck(newPassword, currentUser);
        userService.passwordLengthCheck(newPassword);
        userService.breachedPasswordCheck(newPassword, userService.getBreachedPasswords());

        // postavljanje nove lozinke:
        userService.setNewPassword(newPassword, currentUser);

        // BITNO - čuvanje izmjena u bazi:
        userService.save(currentUser);

        // response format:
        Map<String,String> changedPassword = new LinkedHashMap<>();
        changedPassword.put("email", currentUser.getEmail());
        changedPassword.put("status", "The password has been updated successfully");

        return new ResponseEntity(changedPassword, HttpStatus.OK);
    }

    @PostMapping("api/auth/signup")
    public ResponseEntity createUser(@Valid @RequestBody User user) {

        // provjera za lozinku prilikom sign-upa:
        userService.passwordLengthCheck(user.getPassword());
        userService.breachedPasswordCheck(user.getPassword(), userService.getBreachedPasswords());

        User signedUp = userService.register(user);
        return new ResponseEntity(signedUp, HttpStatus.OK);
    }


    @GetMapping("api/empl/payment")
    public ResponseEntity getCurrentUserInfo(@RequestParam(name = "period") Optional<String> period,
                                             @AuthenticationPrincipal UserDetails userDetails) {

        User currentUser = userService.findUserByEmail(userDetails.getUsername());

        // provjera za trenutnu lozinku:
        userService.passwordLengthCheck(currentUser.getPassword());
        userService.breachedPasswordCheck(currentUser.getPassword(), userService.getBreachedPasswords());

        if (period.isPresent()) {
            Optional<Payment> optionalPayment = paymentRepository.findByEmployeeAndPeriod(currentUser.getEmail(), period.get());

            if (optionalPayment.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "");
            }

            PaymentResponseObject pro = new PaymentResponseObject(currentUser, optionalPayment.get());
            return new ResponseEntity(pro, HttpStatus.OK);
        }

        List<Payment> allPaymentsForUser = paymentRepository.findAllByEmployeeOrderByMonthYearDesc(currentUser.getEmail());
        List<PaymentResponseObject> allPaymentsFormatted = allPaymentsForUser.stream()
                                                                .map(p -> new PaymentResponseObject(currentUser, p))
                                                                .collect(Collectors.toList());

        return new ResponseEntity(allPaymentsFormatted, HttpStatus.OK);
    }
}
