package it.unicas.spring.springai.repository;

import it.unicas.spring.springai.model.AssessmentResult;
import it.unicas.spring.springai.model.Macchinario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

@Repository
public interface AssessmentResultRepository extends JpaRepository<AssessmentResult, Long> {

    List<AssessmentResult> findByMacchinarioOrderByDataAssessmentDesc(Macchinario macchinario);

    Optional<AssessmentResult> findFirstByMacchinarioOrderByDataAssessmentDesc(Macchinario macchinario);

    Optional<AssessmentResult> findFirstByMacchinarioIdOrderByDataAssessmentDesc(Long macchinarioId);

    List<AssessmentResult> findByMacchinarioIdOrderByDataAssessmentDesc(Long macchinarioId);

    void deleteAllByMacchinarioId(Long macchinarioId);

    List<AssessmentResult> findByLivelloRischio(String livelloRischio);

    List<AssessmentResult> findAllByOrderByDataAssessmentDesc();
}
