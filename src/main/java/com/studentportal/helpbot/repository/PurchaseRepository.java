package com.studentportal.helpbot.repository;

import com.studentportal.helpbot.model.Purchase;
import org.springframework.data.repository.CrudRepository;

public interface PurchaseRepository extends CrudRepository<Purchase, String> {
}
