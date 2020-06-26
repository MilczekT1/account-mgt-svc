package pl.konradboniecki.budget.accountmanagement.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.konradboniecki.budget.accountmanagement.model.Account;
import pl.konradboniecki.budget.accountmanagement.model.ProjectAccess;

import java.util.Optional;

@Service
public class ProjectAccessService {

    private ProjectAccessRepository projectAccessRepository;

    @Autowired
    public ProjectAccessService(ProjectAccessRepository projectAccessRepository) {
        this.projectAccessRepository = projectAccessRepository;
    }

    public Optional<ProjectAccess> findByAccountId(Long id) {
        return projectAccessRepository.findByAccountId(id);
    }

    public Account addProjectAccessDataToAccount(Account acc) {
        ProjectAccess pj = findByAccountId(acc.getId()).get();
        acc.setFamilyId(pj.getFamilyId());
        acc.setBudgetGranted(pj.isBudgetGranted());
        acc.setHorseeGranted(pj.isHorseeGranted());
        return acc;
    }

    public void setFamilyId(Long familyId, Long accountId) {
        projectAccessRepository.setFamilyId(familyId, accountId);
    }

    public ProjectAccess saveProjectAccess(ProjectAccess projectAccess) {
        return projectAccessRepository.save(projectAccess);
    }
}
