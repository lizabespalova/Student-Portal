package com.studentportal.helpbot.repository;

import com.studentportal.helpbot.model.Post;
import org.springframework.data.repository.CrudRepository;

public interface PostRepository extends CrudRepository<Post, Integer> {
}
