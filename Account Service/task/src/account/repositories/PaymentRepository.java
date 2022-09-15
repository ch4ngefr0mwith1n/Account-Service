package account.repositories;

import account.models.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findByEmployeeAndPeriod(String employee, String period);
    List<Payment> findAllByEmployeeOrderByMonthYearDesc(String employee);
}
