package com.softuni.commentsrecipes.Repository;

import com.softuni.commentsrecipes.model.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    Comment findTopByOrderByIdDesc();

}
