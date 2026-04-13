package com.goosage.api.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.goosage.app.KnowledgeService;
import com.goosage.app.KnowledgeTemplateService;
import com.goosage.app.TemplateService;
import com.goosage.auth.SessionConst;
import com.goosage.dto.KnowledgeDto;
import com.goosage.dto.knowledge.KnowledgeCreateRequest;
import com.goosage.dto.knowledge.KnowledgeMapper;
import com.goosage.dto.knowledge.KnowledgeResponse;
import com.goosage.dto.template.TemplateDto;
import com.goosage.dto.template.TemplateResponse;
import com.goosage.support.web.ApiResponse;
import com.goosage.support.web.UnauthorizedException;

import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/knowledge")
public class KnowledgeController {

    private final KnowledgeService knowledgeService;
    private final KnowledgeTemplateService knowledgeTemplateService;
    private final TemplateService templateService;

    public KnowledgeController(
            KnowledgeService knowledgeService,
            KnowledgeTemplateService knowledgeTemplateService,
            TemplateService templateService
    ) {
        this.knowledgeService = knowledgeService;
        this.knowledgeTemplateService = knowledgeTemplateService;
        this.templateService = templateService;
    }

    private long requireUserId(HttpSession session) {
        Object uidObj = session.getAttribute(SessionConst.LOGIN_USER_ID);
        if (uidObj == null) {
            throw new UnauthorizedException("UNAUTHORIZED");
        }

        if (uidObj instanceof Long) {
            return (Long) uidObj;
        }

        return Long.parseLong(String.valueOf(uidObj));
    }

    @PostMapping
    public ApiResponse<KnowledgeResponse> create(
            @RequestBody KnowledgeCreateRequest req,
            HttpSession session
    ) {
        requireUserId(session);

        KnowledgeDto saved = knowledgeService.save(KnowledgeMapper.toDto(req));
        return ApiResponse.ok(KnowledgeMapper.toResponse(saved));
    }

    @GetMapping
    public ApiResponse<List<KnowledgeResponse>> list(HttpSession session) {
        requireUserId(session);

        List<KnowledgeDto> list = knowledgeService.findAll();
        return ApiResponse.ok(KnowledgeMapper.toResponseList(list));
    }

    @GetMapping("/{id}")
    public ApiResponse<KnowledgeResponse> getOne(
            @PathVariable Long id,
            HttpSession session
    ) {
        requireUserId(session);

        KnowledgeDto k = knowledgeService.mustFindById(id);
        return ApiResponse.ok(KnowledgeMapper.toResponse(k));
    }

    @GetMapping("/{id}/template/summary-v1")
    public ApiResponse<TemplateResponse> summaryV1(
            @PathVariable Long id,
            HttpSession session
    ) {
        requireUserId(session);

        KnowledgeDto knowledge = knowledgeService.mustFindById(id);
        String resultText = knowledgeTemplateService.toSummaryV1(knowledge);

        TemplateResponse response = new TemplateResponse(
                knowledge.getId(),
                "SUMMARY_V1",
                resultText
        );

        return ApiResponse.ok(response);
    }

    @GetMapping("/{id}/template/summary-v2")
    public ApiResponse<TemplateResponse> summaryV2(
            @PathVariable Long id,
            HttpSession session
    ) {
        requireUserId(session);

        KnowledgeDto knowledge = knowledgeService.mustFindById(id);
        TemplateDto saved = templateService.getOrCreateSummaryV2(knowledge);

        TemplateResponse response = new TemplateResponse(
                knowledge.getId(),
                saved.getTemplateType(),
                saved.getResultText()
        );

        return ApiResponse.ok(response);
    }
}