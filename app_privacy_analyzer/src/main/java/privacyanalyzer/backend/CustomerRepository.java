package privacyanalyzer.backend;

import org.springframework.data.jpa.repository.JpaRepository;

import privacyanalyzer.backend.data.entity.Customer;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
}
