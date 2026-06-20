package com.exam.repository;

import com.exam.entity.FavoriteQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FavoriteQuestionRepository extends JpaRepository<FavoriteQuestion, Long> {
    Optional<FavoriteQuestion> findByQuestionId(Long questionId);
    void deleteByQuestionId(Long questionId);
    boolean existsByQuestionId(Long questionId);
    List<FavoriteQuestion> findByQuestionIdIn(List<Long> questionIds);
}
