package springsideproject1.springsideproject1production.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import springsideproject1.springsideproject1production.domain.Company;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Repository
@Transactional(readOnly = true)
public class CompanyRepositoryJdbc implements CompanyRepository {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public CompanyRepositoryJdbc(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    /**
     * SELECT Company
     */
    @Override
    public Optional<Company> searchCompanyByCode(Long code) {
        return Optional.ofNullable(jdbcTemplate.query("select * from testcompanies where code = ?", companyRowMapper(), code).getFirst());
    }

    @Override
    public Optional<Company> searchCompanyByName(String name) {
        return Optional.ofNullable(jdbcTemplate.query("select * from testcompanies where name = ?", companyRowMapper(), name).getFirst());
    }

    /**
     * INSERT Company
     */
    @Override
    @Transactional
    public void saveCompany(Company company) {
        SimpleJdbcInsert jdbcInsert = new SimpleJdbcInsert(jdbcTemplate);
        jdbcInsert.withTableName("testcompanies");

        Map<String, Object> insertParam = new HashMap<>() {{
            put("code", company.getCode());
            put("country", company.getCountry());
            put("scale", company.getScale());
            put("name", company.getName());
            put("category1st", company.getCategory1st());
            put("category2nd", company.getCategory2nd());
        }};

        jdbcInsert.execute(new MapSqlParameterSource(insertParam));
    }

    /**
     * REMOVE Company
     */
    @Override
    public void removeCompanyByCode(Long code) {
        jdbcTemplate.execute("delete from testcompanies where code = '" + code + "'");
    }

    private RowMapper<Company> companyRowMapper() {
        return (resultSet, rowNumber) -> {
            Company company = new Company();
            company.setCode(resultSet.getLong("code"));
            company.setCountry(resultSet.getString("country"));
            company.setScale(resultSet.getString("scale"));
            company.setName(resultSet.getString("name"));
            company.setCategory1st(resultSet.getString("category1st"));
            company.setCategory2nd(resultSet.getString("category2nd"));
            return company;
        };
    }
}
