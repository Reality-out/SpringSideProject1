package springsideproject1.springsideproject1build.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;
import springsideproject1.springsideproject1build.domain.Company;
import springsideproject1.springsideproject1build.utility.test.CompanyTest;

import javax.sql.DataSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static springsideproject1.springsideproject1build.config.constant.EXCEPTION_MESSAGE_CONFIG.ALREADY_EXIST_COMPANY_CODE;
import static springsideproject1.springsideproject1build.config.constant.EXCEPTION_MESSAGE_CONFIG.NO_COMPANY_WITH_THAT_CODE;

@SpringBootTest
@Transactional
class CompanyServiceJdbcTest implements CompanyTest {

    @Autowired
    CompanyService companyService;

    private final JdbcTemplate jdbcTemplateTest;

    @Autowired
    public CompanyServiceJdbcTest(DataSource dataSource) {
        jdbcTemplateTest = new JdbcTemplate(dataSource);
    }

    @BeforeEach
    public void beforeEach() {
        resetTable(jdbcTemplateTest, companyTable);
    }

    @DisplayName("기업 등록")
    @Test
    public void registerCompany() {
        // given
        Company company = createSamsungElectronics();

        // when
        companyService.joinCompany(company);

        // then
        assertThat(companyService.findCompanies().getFirst()).usingRecursiveComparison().isEqualTo(company);
    }

    @DisplayName("중복 코드 번호를 사용하는 기업 등록")
    @Test
    public void registerCompanyWithSameCode() {
        // given
        Company company1 = createSamsungElectronics();
        Company company2 = createSamsungElectronics();

        // when
        companyService.joinCompany(company1);

        // then
        IllegalStateException e = assertThrows(IllegalStateException.class,
                () -> companyService.joinCompany(company2));
        assertThat(e.getMessage()).isEqualTo(ALREADY_EXIST_COMPANY_CODE);
    }

    @DisplayName("존재하지 않는 코드 번호를 통한 기업 삭제")
    @Test
    public void removeCompanyByFaultCode() {
        IllegalStateException e = assertThrows(IllegalStateException.class,
                () -> companyService.removeCompany("123456"));
        assertThat(e.getMessage()).isEqualTo(NO_COMPANY_WITH_THAT_CODE);
    }
}