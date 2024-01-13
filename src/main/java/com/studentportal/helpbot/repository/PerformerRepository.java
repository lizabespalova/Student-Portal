package com.studentportal.helpbot.repository;
import com.studentportal.helpbot.model.Performer;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PerformerRepository  extends CrudRepository<Performer, Long> {
}

