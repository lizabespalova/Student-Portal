package com.studentportal.helpbot.model;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PerformerRepository  extends CrudRepository<Performer, Long> {
}

