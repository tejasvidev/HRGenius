package com.hrgenius.onboarding.controller;

import com.hrgenius.onboarding.dto.*;
import com.hrgenius.onboarding.service.OnboardingService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import java.util.Set;
import java.util.stream.Collectors;

@RestController @RequestMapping("/api/v1/onboarding")
public class OnboardingController {
    private final OnboardingService service;
    public OnboardingController(OnboardingService service){this.service=service;}
    private Set<String> roles(Authentication a){return a.getAuthorities().stream().map(x->x.getAuthority()).collect(Collectors.toSet());}
    @GetMapping("/templates") @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN','HR_MANAGER','HR_EXECUTIVE')")
    public Object templates(){return service.templates();}
    @PostMapping("/templates") @ResponseStatus(HttpStatus.CREATED) @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN','HR_MANAGER','HR_EXECUTIVE')")
    public OnboardingTemplateResponse createTemplate(@Valid @RequestBody OnboardingTemplateRequest r){return service.createTemplate(r);}
    @PatchMapping("/templates/{id}/active") @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN','HR_MANAGER','HR_EXECUTIVE')")
    public OnboardingTemplateResponse active(@PathVariable long id,@RequestParam boolean active){return service.setTemplateActive(id,active);}
    @GetMapping("/plans/me") public Object mine(@AuthenticationPrincipal Jwt jwt){return service.myPlans(jwt.getClaimAsString("email"));}
    @GetMapping("/plans") @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN','HR_MANAGER','HR_EXECUTIVE')") public Object plans(){return service.allPlans();}
    @PostMapping("/plans") @ResponseStatus(HttpStatus.CREATED) @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN','HR_MANAGER','HR_EXECUTIVE')")
    public OnboardingPlanResponse createPlan(@Valid @RequestBody OnboardingPlanRequest r){return service.startPlan(r);}
    @GetMapping("/plans/{id}") public OnboardingPlanResponse plan(@PathVariable long id,@AuthenticationPrincipal Jwt jwt,Authentication a){return service.plan(id,jwt.getClaimAsString("email"),roles(a));}
    @GetMapping("/plans/{id}/tasks") public Object tasks(@PathVariable long id,@AuthenticationPrincipal Jwt jwt,Authentication a){return service.planTasks(id,jwt.getClaimAsString("email"),roles(a));}
    @GetMapping("/plans/{id}/documents") public Object documents(@PathVariable long id,@AuthenticationPrincipal Jwt jwt,Authentication a){return service.planDocuments(id,jwt.getClaimAsString("email"),roles(a));}
    @PostMapping("/plans/{id}/documents") @ResponseStatus(HttpStatus.CREATED)
    public OnboardingDocumentResponse submit(@PathVariable long id,@AuthenticationPrincipal Jwt jwt,@Valid @RequestBody OnboardingDocumentRequest r){return service.submitDocument(id,jwt.getClaimAsString("email"),r);}
    @PatchMapping("/tasks/{id}/status") public OnboardingTaskResponse updateTask(@PathVariable long id,@AuthenticationPrincipal Jwt jwt,Authentication a,@Valid @RequestBody OnboardingTaskStatusRequest r){return service.updateTask(id,jwt.getClaimAsString("email"),roles(a),r);}
    @PatchMapping("/documents/{id}/review") public OnboardingDocumentResponse review(@PathVariable long id,@AuthenticationPrincipal Jwt jwt,Authentication a,@Valid @RequestBody OnboardingDocumentReviewRequest r){return service.reviewDocument(id,jwt.getClaimAsString("email"),roles(a),r);}
}
