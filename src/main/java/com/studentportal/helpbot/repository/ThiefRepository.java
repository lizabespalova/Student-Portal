package com.studentportal.helpbot.repository;

import com.studentportal.helpbot.model.Thief;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface ThiefRepository extends CrudRepository<Thief, Long> {

}
