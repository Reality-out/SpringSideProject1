package springsideproject1.springsideproject1build;

import lombok.experimental.UtilityClass;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import springsideproject1.springsideproject1build.domain.Company;
import springsideproject1.springsideproject1build.domain.CompanyArticle;
import springsideproject1.springsideproject1build.domain.Member;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@UtilityClass
public class Utility {
    /**
     * Constant
     */

    // DB Table Names
    public static final String articleTable = "testarticles";
    public static final String companyTable = "testcompanies";
    public static final String memberTable = "testmembers";

    // Exception Statements

    /*
     * Article
     */
    public static final String ALREADY_EXIST_ARTICLE_NAME = "이미 존재하는 기사 제목입니다.";
    public static final String NO_ARTICLE_WITH_THAT_NAME = "해당 제목과 일치하는 기사가 없습니다.";

    /*
     * Company
     */
    public static final String ALREADY_EXIST_CODE = "이미 존재하는 코드 번호입니다.";
    public static final String NO_COMPANY_WITH_THAT_CODE = "해당 코드 번호와 일치하는 기업이 없습니다.";

    /**
     * Decoder
     */
    public static List<String> decodeUTF8(List<String> source) {
        List<String> returnList = new ArrayList<>();
        for (String s : source) {
            returnList.add(URLDecoder.decode(s, StandardCharsets.UTF_8));
        }
        return returnList;
    }

    /**
     * RowMapper
     */
    public static RowMapper<CompanyArticle> articleRowMapper() {
        return (resultSet, rowNumber) ->
                new CompanyArticle.ArticleBuilder()
                .number(resultSet.getLong("number"))
                .name(resultSet.getString("name"))
                .press(resultSet.getString("press"))
                .subjectCompany(resultSet.getString("subjectcompany"))
                .link(resultSet.getString("link"))
                .date(resultSet.getDate("date").toLocalDate())
                .importance(resultSet.getInt("importance"))
                .build();
    }

    public static RowMapper<Company> companyRowMapper() {
        return (resultSet, rowNumber) ->
                new Company.CompanyBuilder()
                .code(resultSet.getString("code"))
                .country(resultSet.getString("country"))
                .scale(resultSet.getString("scale"))
                .name(resultSet.getString("name"))
                .category1st(resultSet.getString("category1st"))
                .category2nd(resultSet.getString("category2nd"))
                .build();
    }

    public static RowMapper<Member> memberRowMapper() {
        return (resultSet, rowNumber) ->
                new Member.MemberBuilder()
                .identifier(resultSet.getLong("identifier"))
                .id(resultSet.getString("id"))
                .password(resultSet.getString("password"))
                .name(resultSet.getString("name"))
                .build();
    }

    /**
     * Test
     */
    // CompanyArticle
    public static CompanyArticle createTestArticle() {
        return new CompanyArticle.ArticleBuilder()
                .name("'OLED 위기감' 삼성디스플레이, 주64시간제 도입…삼성 비상경영 확산")
                .press("SBS")
                .subjectCompany("삼성디스플레이")
                .link("https://biz.sbs.co.kr/article/20000176881")
                .date(LocalDate.of(2024, 6, 18))
                .importance(0)
                .build();
    }

    public static CompanyArticle createTestEqualDateArticle() {
        return new CompanyArticle.ArticleBuilder()
                .name("삼성전자도 현대차 이어 인도법인 상장 가능성, '코리아 디스카운트' 해소 기회")
                .press("비즈니스포스트")
                .subjectCompany("삼성전자")
                .link("https://www.businesspost.co.kr/BP?command=article_view&num=355822")
                .date(LocalDate.of(2024, 6, 18))
                .importance(0)
                .build();
    }

    public static CompanyArticle createTestNewArticle() {
        return new CompanyArticle.ArticleBuilder()
                .name("[단독] 삼성전자 네트워크사업부 인력 700명 전환 배치")
                .press("헤럴드경제")
                .subjectCompany("삼성전자")
                .link("https://biz.heraldcorp.com/view.php?ud=20240617050050")
                .date(LocalDate.of(2024, 6, 17))
                .importance(0)
                .build();
    }

    // Company
    public static Company createSamsungElectronics() {
        return new Company.CompanyBuilder()
                .code("005930")
                .country("South Korea")
                .scale("big")
                .name("삼성전자")
                .category1st("electronics")
                .category2nd("semiconductor")
                .build();
    }

    public static Company createSKHynix() {
        return new Company.CompanyBuilder()
                .code("000660")
                .country("South Korea")
                .scale("big")
                .name("SK하이닉스")
                .category1st("electronics")
                .category2nd("semiconductor")
                .build();
    }

    // Member
    public static Member createTestMember() {
        return new Member.MemberBuilder()
                .identifier(1L)
                .id("ABcd1234!")
                .password("EFgh1234!")
                .name("박진하")
                .build();
    }

    public static Member createTestNewMember() {
        return new Member.MemberBuilder()
                .identifier(2L)
                .id("abCD4321!")
                .password("OPqr4321!")
                .name("박하진")
                .build();
    }

    // General-Purpose
    public static void resetTable(JdbcTemplate jdbcTemplateTest, String tableName) {
        resetTable(jdbcTemplateTest, tableName, false);
    }

    public static void resetTable(JdbcTemplate jdbcTemplateTest, String tableName, boolean hasAutoIncrement) {
        jdbcTemplateTest.execute("DELETE FROM " + tableName);
        if (hasAutoIncrement) {
            jdbcTemplateTest.execute("ALTER TABLE " + tableName + " AUTO_INCREMENT = 1");
        }
    }

    /**
     * Validation
     */
    public static boolean isNumeric(String string) {
        return Pattern.matches("[0-9]+", string);
    }
}
