package springsideproject1.springsideproject1build.repository;

import springsideproject1.springsideproject1build.domain.Company;

import java.util.List;
import java.util.Optional;

public interface CompanyRepository {
    /**
     * SELECT Company
     */
    List<Company> findAllCompanies();

    Optional<Company> searchCompanyByCode(String code);

    Optional<Company> searchCompanyByName(String name);

    /**
     * INSERT Company
     */
    void saveCompany(Company company);

    /**
     * REMOVE Company
     */
    void removeCompanyByCode(String code);
}
