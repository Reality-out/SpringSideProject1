package site.hixview.domain.validator.article;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import site.hixview.domain.entity.article.IndustryArticle;
import site.hixview.domain.entity.article.IndustryArticleBufferSimple;
import site.hixview.domain.entity.article.IndustryArticleDto;
import site.hixview.domain.service.IndustryArticleService;
import site.hixview.util.test.IndustryArticleTestUtils;

import javax.sql.DataSource;
import java.util.HashMap;

import static java.util.Objects.requireNonNull;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static site.hixview.domain.vo.RequestUrl.FINISH_URL;
import static site.hixview.domain.vo.RequestUrl.REDIRECT_URL;
import static site.hixview.domain.vo.Word.*;
import static site.hixview.domain.vo.manager.Layout.ADD_PROCESS_LAYOUT;
import static site.hixview.domain.vo.manager.Layout.UPDATE_PROCESS_LAYOUT;
import static site.hixview.domain.vo.manager.RequestURL.ADD_INDUSTRY_ARTICLE_WITH_STRING_URL;
import static site.hixview.domain.vo.manager.RequestURL.ADD_SINGLE_INDUSTRY_ARTICLE_URL;
import static site.hixview.domain.vo.name.EntityName.Article.*;
import static site.hixview.domain.vo.name.ExceptionName.IS_BEAN_VALIDATION_ERROR;
import static site.hixview.domain.vo.name.SchemaName.TEST_INDUSTRY_ARTICLES_SCHEMA;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class IndustryArticleValidatorErrorTest implements IndustryArticleTestUtils {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    IndustryArticleService articleService;

    private final JdbcTemplate jdbcTemplateTest;

    @Autowired
    public IndustryArticleValidatorErrorTest(DataSource dataSource) {
        jdbcTemplateTest = new JdbcTemplate(dataSource);
    }

    @BeforeEach
    public void beforeEach() {
        resetTable(jdbcTemplateTest, TEST_INDUSTRY_ARTICLES_SCHEMA, true);
    }

    @DisplayName("미래의 기사 입력일을 사용하는 산업 기사 추가 유효성 검증")
    @Test
    public void futureDateIndustryArticleAdd() throws Exception {
        // given & when
        IndustryArticleDto articleDtoFuture = createTestIndustryArticleDto();
        articleDtoFuture.setYear(2099);
        articleDtoFuture.setMonth(12);
        articleDtoFuture.setDays(31);

        // then
        assertThat(requireNonNull(mockMvc.perform(postWithIndustryArticleDto(ADD_SINGLE_INDUSTRY_ARTICLE_URL, articleDtoFuture))
                .andExpectAll(view().name(addSingleIndustryArticleProcessPage),
                        model().attribute(LAYOUT_PATH, ADD_PROCESS_LAYOUT),
                        model().attribute(ERROR, (String) null))
                .andReturn().getModelAndView()).getModelMap().get(ARTICLE))
                .usingRecursiveComparison()
                .isEqualTo(articleDtoFuture);
    }

    @DisplayName("기사 입력일이 유효하지 않은 산업 기사 추가 유효성 검증")
    @Test
    public void invalidDateIndustryArticleAdd() throws Exception {
        // given & when
        IndustryArticleDto articleDto = createTestIndustryArticleDto();
        articleDto.setYear(2000);
        articleDto.setMonth(2);
        articleDto.setDays(31);

        // then
        assertThat(requireNonNull(mockMvc.perform(postWithIndustryArticleDto(ADD_SINGLE_INDUSTRY_ARTICLE_URL, articleDto))
                .andExpectAll(view().name(addSingleIndustryArticleProcessPage),
                        model().attribute(LAYOUT_PATH, ADD_PROCESS_LAYOUT),
                        model().attribute(ERROR, (String) null))
                .andReturn().getModelAndView()).getModelMap().get(ARTICLE))
                .usingRecursiveComparison()
                .isEqualTo(articleDto);
    }

    @DisplayName("중복 기사명을 사용하는 산업 기사 추가")
    @Test
    public void duplicatedNameIndustryArticleAdd() throws Exception {
        // given
        IndustryArticle article1 = testIndustryArticle;
        String commonName = article1.getName();
        IndustryArticleDto articleDto2 = createTestNewIndustryArticleDto();
        articleDto2.setName(commonName);

        // when
        articleService.registerArticle(article1);

        // then
        assertThat(requireNonNull(mockMvc.perform(postWithIndustryArticleDto(ADD_SINGLE_INDUSTRY_ARTICLE_URL, articleDto2))
                .andExpectAll(view().name(addSingleIndustryArticleProcessPage),
                        model().attribute(LAYOUT_PATH, ADD_PROCESS_LAYOUT),
                        model().attribute(ERROR, (String) null))
                .andReturn().getModelAndView()).getModelMap().get(ARTICLE))
                .usingRecursiveComparison()
                .isEqualTo(articleDto2);
    }

    @DisplayName("중복 기사 링크를 사용하는 산업 기사 추가")
    @Test
    public void duplicatedLinkIndustryArticleAdd() throws Exception {
        // given
        IndustryArticle article1 = testIndustryArticle;
        String commonLink = article1.getLink();
        IndustryArticleDto articleDto2 = createTestNewIndustryArticleDto();
        articleDto2.setLink(commonLink);

        // when
        articleService.registerArticle(article1);

        // then
        assertThat(requireNonNull(mockMvc.perform(postWithIndustryArticleDto(ADD_SINGLE_INDUSTRY_ARTICLE_URL, articleDto2))
                .andExpectAll(view().name(addSingleIndustryArticleProcessPage),
                        model().attribute(LAYOUT_PATH, ADD_PROCESS_LAYOUT),
                        model().attribute(ERROR, (String) null))
                .andReturn().getModelAndView()).getModelMap().get(ARTICLE))
                .usingRecursiveComparison()
                .isEqualTo(articleDto2);
    }

    @DisplayName("기사 입력일이 유효하지 않은, 문자열을 사용하는 산업 기사들 추가")
    @Test
    public void invalidDateIndustryArticleAddWithString() throws Exception {
        // given & when
        IndustryArticleDto articleDto = createTestIndustryArticleDto();
        articleDto.setYear(2000);
        articleDto.setMonth(2);
        articleDto.setDays(31);
        IndustryArticleBufferSimple articleBuffer = IndustryArticleBufferSimple.builder().articleDto(articleDto).build();

        // then
        requireNonNull(mockMvc.perform(postWithMultipleParams(ADD_INDUSTRY_ARTICLE_WITH_STRING_URL, new HashMap<>() {{
                    put(nameDatePressString, articleBuffer.getNameDatePressString());
                    put(linkString, articleBuffer.getLinkString());
                    put(SUBJECT_FIRST_CATEGORY, articleBuffer.getSubjectFirstCategory());
                    put(SUBJECT_SECOND_CATEGORY, articleBuffer.getSubjectSecondCategory());
                }}))
                .andExpectAll(view().name(
                                REDIRECT_URL + ADD_INDUSTRY_ARTICLE_WITH_STRING_URL + FINISH_URL),
                        model().attribute(IS_BEAN_VALIDATION_ERROR, String.valueOf(false)),
                        model().attribute(ERROR_SINGLE, (String) null)));
    }

    @DisplayName("중복 기사명을 사용하는, 문자열을 사용하는 산업 기사들 추가")
    @Test
    public void duplicatedNameIndustryArticleAddWithString() throws Exception {
        // given & when
        articleService.registerArticle(IndustryArticle.builder().article(testIndustryArticle).name(testEqualDateIndustryArticle.getName()).build());

        // then
        requireNonNull(mockMvc.perform(postWithMultipleParams(ADD_INDUSTRY_ARTICLE_WITH_STRING_URL, new HashMap<>() {{
                    put(nameDatePressString, testEqualDateIndustryArticleBuffer.getNameDatePressString());
                    put(linkString, testEqualDateIndustryArticleBuffer.getLinkString());
                    put(SUBJECT_FIRST_CATEGORY, testEqualDateIndustryArticleBuffer.getSubjectFirstCategory());
                    put(SUBJECT_SECOND_CATEGORY, testEqualDateIndustryArticleBuffer.getSubjectSecondCategory());
                }}))
                .andExpectAll(view().name(
                                REDIRECT_URL + ADD_INDUSTRY_ARTICLE_WITH_STRING_URL + FINISH_URL),
                        model().attribute(IS_BEAN_VALIDATION_ERROR, String.valueOf(false)),
                        model().attribute(ERROR_SINGLE, (String) null)));
    }

    @DisplayName("중복 기사 링크를 사용하는, 문자열을 사용하는 산업 기사들 추가")
    @Test
    public void duplicatedLinkIndustryArticleAddWithString() throws Exception {
        // given & when
        articleService.registerArticle(IndustryArticle.builder().article(testIndustryArticle).link(testEqualDateIndustryArticle.getLink()).build());

        // then
        requireNonNull(mockMvc.perform(postWithMultipleParams(ADD_INDUSTRY_ARTICLE_WITH_STRING_URL, new HashMap<>() {{
                    put(nameDatePressString, testEqualDateIndustryArticleBuffer.getNameDatePressString());
                    put(linkString, testEqualDateIndustryArticleBuffer.getLinkString());
                    put(SUBJECT_FIRST_CATEGORY, testEqualDateIndustryArticleBuffer.getSubjectFirstCategory());
                    put(SUBJECT_SECOND_CATEGORY, testEqualDateIndustryArticleBuffer.getSubjectSecondCategory());
                }}))
                .andExpectAll(view().name(
                                REDIRECT_URL + ADD_INDUSTRY_ARTICLE_WITH_STRING_URL + FINISH_URL),
                        model().attribute(IS_BEAN_VALIDATION_ERROR, String.valueOf(false)),
                        model().attribute(ERROR_SINGLE, (String) null)));
    }

    @DisplayName("미래의 기사 입력일을 사용하는 산업 기사 변경 유효성 검증")
    @Test
    public void futureDateIndustryArticleModify() throws Exception {
        IndustryArticleDto articleDtoFuture = createTestIndustryArticleDto();
        articleDtoFuture.setYear(2099);
        articleDtoFuture.setMonth(12);
        articleDtoFuture.setDays(31);

        assertThat(requireNonNull(mockMvc.perform(postWithIndustryArticleDto(modifyIndustryArticleFinishUrl, articleDtoFuture))
                .andExpectAll(view().name(modifyIndustryArticleProcessPage),
                        model().attribute(LAYOUT_PATH, UPDATE_PROCESS_LAYOUT),
                        model().attribute(ERROR, (String) null))
                .andReturn().getModelAndView()).getModelMap().get(ARTICLE))
                .usingRecursiveComparison()
                .isEqualTo(articleDtoFuture);
    }

    @DisplayName("기사 입력일이 유효하지 않은 산업 기사 변경")
    @Test
    public void invalidDateIndustryArticleModify() throws Exception {
        // given & when
        IndustryArticleDto articleDto = createTestIndustryArticleDto();
        articleDto.setYear(2000);
        articleDto.setMonth(2);
        articleDto.setDays(31);

        // then
        assertThat(requireNonNull(mockMvc.perform(postWithIndustryArticleDto(modifyIndustryArticleFinishUrl, articleDto))
                .andExpectAll(view().name(modifyIndustryArticleProcessPage),
                        model().attribute(LAYOUT_PATH, UPDATE_PROCESS_LAYOUT),
                        model().attribute(ERROR, (String) null))
                .andReturn().getModelAndView()).getModelMap().get(ARTICLE))
                .usingRecursiveComparison()
                .isEqualTo(articleDto);
    }

    @DisplayName("기사명 또는 기사 링크까지 변경을 시도하는, 산업 기사 변경")
    @Test
    public void changeNameOrLinkIndustryArticleModify() throws Exception {
        // given & when
        IndustryArticle article = articleService.registerArticle(testIndustryArticle);

        // then
        requireNonNull(mockMvc.perform(postWithIndustryArticle(modifyIndustryArticleFinishUrl,
                        IndustryArticle.builder().article(article).name(testNewIndustryArticle.getName()).build()))
                .andExpectAll(view().name(modifyIndustryArticleProcessPage),
                        model().attribute(LAYOUT_PATH, UPDATE_PROCESS_LAYOUT),
                        model().attribute(ERROR, (String) null)));

        requireNonNull(mockMvc.perform(postWithIndustryArticle(modifyIndustryArticleFinishUrl,
                        IndustryArticle.builder().article(article).link(testNewIndustryArticle.getLink()).build()))
                .andExpectAll(view().name(modifyIndustryArticleProcessPage),
                        model().attribute(LAYOUT_PATH, UPDATE_PROCESS_LAYOUT),
                        model().attribute(ERROR, (String) null)));
    }

    @DisplayName("대상 산업이 추가되지 않은 산업 기사 변경")
    @Test
    public void notRegisteredSubjectIndustryArticleModify() throws Exception {
        // given & when
        IndustryArticleDto articleDto = createTestIndustryArticleDto();

        // then
        assertThat(requireNonNull(mockMvc.perform(postWithIndustryArticleDto(modifyIndustryArticleFinishUrl, articleDto))
                .andExpectAll(view().name(modifyIndustryArticleProcessPage),
                        model().attribute(LAYOUT_PATH, UPDATE_PROCESS_LAYOUT),
                        model().attribute(ERROR, (String) null))
                .andReturn().getModelAndView()).getModelMap().get(ARTICLE))
                .usingRecursiveComparison()
                .isEqualTo(articleDto);
    }
}
