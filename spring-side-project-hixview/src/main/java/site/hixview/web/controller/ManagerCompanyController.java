package site.hixview.web.controller;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import site.hixview.domain.entity.Country;
import site.hixview.domain.entity.Scale;
import site.hixview.domain.entity.company.Company;
import site.hixview.domain.entity.company.CompanyDto;
import site.hixview.domain.service.CompanyService;
import site.hixview.domain.validation.validator.CompanyAddValidator;
import site.hixview.domain.validation.validator.CompanyModifyValidator;
import site.hixview.util.ControllerUtils;

import java.util.Optional;

import static org.springframework.web.util.UriComponentsBuilder.fromPath;
import static site.hixview.domain.vo.ExceptionMessage.NO_COMPANY_WITH_THAT_CODE_OR_NAME;
import static site.hixview.domain.vo.Regex.NUMBER_PATTERN;
import static site.hixview.domain.vo.RequestUrl.FINISH_URL;
import static site.hixview.domain.vo.RequestUrl.REDIRECT_URL;
import static site.hixview.domain.vo.Word.*;
import static site.hixview.domain.vo.manager.Layout.*;
import static site.hixview.domain.vo.manager.RequestURL.*;
import static site.hixview.domain.vo.manager.ViewName.*;
import static site.hixview.domain.vo.name.EntityName.Company.COMPANY;
import static site.hixview.domain.vo.name.ExceptionName.BEAN_VALIDATION_ERROR;
import static site.hixview.domain.vo.name.ExceptionName.NOT_FOUND_COMPANY_ERROR;
import static site.hixview.domain.vo.name.ViewName.*;

@Controller
@RequiredArgsConstructor
public class ManagerCompanyController {

    private final CompanyService companyService;

    private final CompanyAddValidator addValidator;
    private final CompanyModifyValidator modifyValidator;

    private final Logger log = LoggerFactory.getLogger(ManagerCompanyController.class);

    /**
     * Add - Single
     */
    @GetMapping(ADD_SINGLE_COMPANY_URL)
    @ResponseStatus(HttpStatus.OK)
    public String processAddCompany(Model model) {
        model.addAttribute(LAYOUT_PATH, ADD_PROCESS_LAYOUT);
        model.addAttribute(COMPANY, new CompanyDto());
        model.addAttribute("countries", Country.values());
        model.addAttribute("scales", Scale.values());
        return ADD_COMPANY_VIEW + SINGLE_PROCESS_VIEW;
    }

    @PostMapping(ADD_SINGLE_COMPANY_URL)
    public String submitAddCompany(@ModelAttribute(COMPANY) @Validated CompanyDto companyDto,
                                   BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            finishForRollback(bindingResult.getAllErrors().toString(), ADD_PROCESS_LAYOUT, BEAN_VALIDATION_ERROR, model);
            return ADD_COMPANY_VIEW + SINGLE_PROCESS_VIEW;
        }

        addValidator.validate(companyDto, bindingResult);
        if (bindingResult.hasErrors()) {
            finishForRollback(bindingResult.getAllErrors().toString(), ADD_PROCESS_LAYOUT, null, model);
            return ADD_COMPANY_VIEW + SINGLE_PROCESS_VIEW;
        }

        companyService.registerCompany(Company.builder().companyDto(companyDto).build());
        return REDIRECT_URL + fromPath(ADD_SINGLE_COMPANY_URL + FINISH_URL).queryParam(NAME, companyDto.getName()).build().toUriString();
    }

    @GetMapping(ADD_SINGLE_COMPANY_URL + FINISH_URL)
    @ResponseStatus(HttpStatus.OK)
    public String finishAddCompany(@RequestParam String name, Model model) {
        model.addAttribute(LAYOUT_PATH, ADD_FINISH_LAYOUT);
        model.addAttribute(VALUE, name);
        return ADD_COMPANY_VIEW + SINGLE_FINISH_VIEW;
    }

    /**
     * See
     */
    @GetMapping(SELECT_COMPANY_URL)
    @ResponseStatus(HttpStatus.OK)
    public String processSeeCompanies(Model model) {
        model.addAttribute(LAYOUT_PATH, SELECT_LAYOUT);
        model.addAttribute("companies", companyService.findCompanies());
        return SELECT_VIEW + "companies-page";
    }

    /**
     * Modify
     */
    @GetMapping(UPDATE_COMPANY_URL)
    @ResponseStatus(HttpStatus.OK)
    public String initiateModifyCompany(Model model) {
        model.addAttribute(LAYOUT_PATH, UPDATE_PROCESS_LAYOUT);
        return UPDATE_COMPANY_VIEW + BEFORE_PROCESS_VIEW;
    }

    @PostMapping(UPDATE_COMPANY_URL)
    @ResponseStatus(HttpStatus.OK)
    public String processModifyCompany(@RequestParam String codeOrName, Model model) {
        Optional<Company> companyOrEmpty = companyService.findCompanyByCodeOrName(codeOrName);
        if (companyOrEmpty.isEmpty()) {
            finishForRollback(NO_COMPANY_WITH_THAT_CODE_OR_NAME, UPDATE_PROCESS_LAYOUT, NOT_FOUND_COMPANY_ERROR, model);
            return UPDATE_COMPANY_VIEW + BEFORE_PROCESS_VIEW;
        }

        model.addAttribute(LAYOUT_PATH, UPDATE_PROCESS_LAYOUT);
        model.addAttribute("updateUrl", UPDATE_COMPANY_URL + FINISH_URL);
        model.addAttribute(COMPANY, companyOrEmpty.orElseThrow().toDto());
        model.addAttribute("countries", Country.values());
        model.addAttribute("scales", Scale.values());
        return UPDATE_COMPANY_VIEW + AFTER_PROCESS_VIEW;
    }

    @PostMapping(UPDATE_COMPANY_URL + FINISH_URL)
    public String submitModifyCompany(@ModelAttribute(COMPANY) @Validated CompanyDto companyDto,
                                      BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            finishForRollback(bindingResult.getAllErrors().toString(), UPDATE_PROCESS_LAYOUT, BEAN_VALIDATION_ERROR, model);
            model.addAttribute("updateUrl", UPDATE_COMPANY_URL + FINISH_URL);
            return UPDATE_COMPANY_VIEW + AFTER_PROCESS_VIEW;
        }

        modifyValidator.validate(companyDto, bindingResult);
        if (bindingResult.hasErrors()) {
            finishForRollback(bindingResult.getAllErrors().toString(), UPDATE_PROCESS_LAYOUT, null, model);
            model.addAttribute("updateUrl", UPDATE_COMPANY_URL + FINISH_URL);
            return UPDATE_COMPANY_VIEW + AFTER_PROCESS_VIEW;
        }
        return REDIRECT_URL + fromPath(UPDATE_COMPANY_URL + FINISH_URL).queryParam(NAME, companyDto.getName()).build().toUriString();
    }

    @GetMapping(UPDATE_COMPANY_URL + FINISH_URL)
    @ResponseStatus(HttpStatus.OK)
    public String finishModifyCompany(@RequestParam String name, Model model) {
        model.addAttribute(LAYOUT_PATH, UPDATE_FINISH_LAYOUT);
        model.addAttribute(VALUE, name);
        return UPDATE_COMPANY_VIEW + FINISH_VIEW;
    }

    /**
     * Get rid of
     */
    @GetMapping(REMOVE_COMPANY_URL)
    @ResponseStatus(HttpStatus.OK)
    public String processRidCompany(Model model) {
        model.addAttribute(LAYOUT_PATH, REMOVE_PROCESS_LAYOUT);
        return REMOVE_COMPANY_URL_VIEW + PROCESS_VIEW;
    }

    @PostMapping(REMOVE_COMPANY_URL)
    public String submitRidCompany(@RequestParam String codeOrName, Model model) {
        Optional<Company> companyOrEmpty = companyService.findCompanyByCodeOrName(codeOrName);
        if (companyOrEmpty.isEmpty()) {
            finishForRollback(NO_COMPANY_WITH_THAT_CODE_OR_NAME, REMOVE_PROCESS_LAYOUT, NOT_FOUND_COMPANY_ERROR, model);
            return REMOVE_COMPANY_URL_VIEW + PROCESS_VIEW;
        }

        if (NUMBER_PATTERN.matcher(codeOrName).matches()) {
            codeOrName = companyService.findCompanyByCode(codeOrName).orElseThrow().getName();
        }
        String redirectURL = REDIRECT_URL + fromPath(REMOVE_COMPANY_URL + FINISH_URL).queryParam(NAME, codeOrName).build().toUriString();
        companyService.removeCompanyByCode(companyService.findCompanyByName(codeOrName).orElseThrow().getCode());
        return redirectURL;
    }

    @GetMapping(REMOVE_COMPANY_URL + FINISH_URL)
    @ResponseStatus(HttpStatus.OK)
    public String finishRidCompany(@RequestParam String name, Model model) {
        model.addAttribute(LAYOUT_PATH, REMOVE_FINISH_LAYOUT);
        model.addAttribute(VALUE, name);
        return REMOVE_COMPANY_URL_VIEW + FINISH_VIEW;
    }

    /**
     * Other private methods
     */
    // Handle Error
    private void finishForRollback(String logMessage, String layoutPath, String error, Model model) {
        ControllerUtils.finishForRollback(logMessage, layoutPath, error, model);
        model.addAttribute("countries", Country.values());
        model.addAttribute("scales", Scale.values());
    }
}