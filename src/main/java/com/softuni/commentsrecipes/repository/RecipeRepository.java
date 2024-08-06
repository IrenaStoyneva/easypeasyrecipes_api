package com.softuni.commentsrecipes.repository;

import com.softuni.commentsrecipes.model.entity.Recipe;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecipeRepository extends JpaRepository<Recipe, Long> {

}
