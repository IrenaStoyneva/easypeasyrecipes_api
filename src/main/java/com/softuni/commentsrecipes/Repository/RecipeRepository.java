package com.softuni.commentsrecipes.Repository;

import com.softuni.commentsrecipes.model.entity.Recipe;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecipeRepository extends JpaRepository<Recipe, Long> {

}
