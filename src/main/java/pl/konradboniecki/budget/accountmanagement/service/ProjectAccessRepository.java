package pl.konradboniecki.budget.accountmanagement.service;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import pl.konradboniecki.budget.accountmanagement.model.ProjectAccess;

import java.util.Optional;

@Repository
public interface ProjectAccessRepository extends CrudRepository<ProjectAccess, Long> {

    Optional<ProjectAccess> findByAccountId(Long aLong);

    ProjectAccess save(ProjectAccess entity);

    @Modifying
    @Transactional
    @Query(value = "UPDATE project_access SET family_id = ?1 WHERE account_id = ?2", nativeQuery = true)
    void setFamilyId(Long familyId, Long accountId);
}
