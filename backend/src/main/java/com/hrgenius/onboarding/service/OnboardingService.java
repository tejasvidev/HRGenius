package com.hrgenius.onboarding.service;

import com.hrgenius.common.ConflictException;
import com.hrgenius.common.ResourceNotFoundException;
import com.hrgenius.employee.entity.Employee;
import com.hrgenius.employee.repository.EmployeeRepository;
import com.hrgenius.onboarding.dto.*;
import com.hrgenius.onboarding.entity.*;
import com.hrgenius.onboarding.repository.*;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Service
public class OnboardingService {
    private static final Set<String> HR=Set.of("ROLE_SUPER_ADMIN","ROLE_ADMIN","ROLE_HR_MANAGER","ROLE_HR_EXECUTIVE");
    private final OnboardingTemplateRepository templates; private final OnboardingPlanRepository plans;
    private final OnboardingTaskRepository tasks; private final OnboardingDocumentRepository documents; private final EmployeeRepository employees;
    public OnboardingService(OnboardingTemplateRepository templates, OnboardingPlanRepository plans, OnboardingTaskRepository tasks,
            OnboardingDocumentRepository documents, EmployeeRepository employees) {
        this.templates=templates;this.plans=plans;this.tasks=tasks;this.documents=documents;this.employees=employees;
    }
    @Transactional(readOnly=true) public List<OnboardingTemplateResponse> templates(){return templates.findAllByOrderByNameAsc().stream().map(OnboardingTemplateResponse::from).toList();}
    @Transactional public OnboardingTemplateResponse createTemplate(OnboardingTemplateRequest r){
        OnboardingTemplate t=new OnboardingTemplate(r.name(),r.description()); short i=0;
        for(TemplateTaskInput x:r.tasks()) t.addTask(new OnboardingTemplateTask(x.title(),x.description(),x.assigneeRole(),x.daysFromStart(),x.required(),i++));
        return OnboardingTemplateResponse.from(templates.save(t));
    }
    @Transactional public OnboardingTemplateResponse setTemplateActive(long id,boolean active){OnboardingTemplate t=template(id);t.setActive(active);return OnboardingTemplateResponse.from(t);}
    @Transactional(readOnly=true) public List<OnboardingPlanResponse> allPlans(){return plans.findAllByOrderByCreatedAtDesc().stream().map(OnboardingPlanResponse::from).toList();}
    @Transactional(readOnly=true) public List<OnboardingPlanResponse> myPlans(String email){Employee e=employeeByEmail(email);return plans.findAllByEmployee_IdOrderByCreatedAtDesc(e.getId()).stream().map(OnboardingPlanResponse::from).toList();}
    @Transactional public OnboardingPlanResponse startPlan(OnboardingPlanRequest r){Employee e=employee(r.employeeId());OnboardingTemplate t=template(r.templateId());return createPlan(e,t,r.startDate());}
    @Transactional public OnboardingPlanResponse startPlan(long templateId,long employeeId,LocalDate date){return createPlan(employee(employeeId),template(templateId),date);}
    private OnboardingPlanResponse createPlan(Employee e,OnboardingTemplate t,LocalDate date){
        if(!t.isActive())throw new ConflictException("Onboarding template is inactive");
        if(plans.existsByEmployee_IdAndStatusIn(e.getId(),Set.of(OnboardingPlanStatus.NOT_STARTED,OnboardingPlanStatus.IN_PROGRESS)))throw new ConflictException("Employee already has an active onboarding plan");
        OnboardingPlan p=plans.save(new OnboardingPlan(e,t,date));
        for(OnboardingTemplateTask source:t.getTasks()){Employee assignee=source.getAssigneeRole()==OnboardingAssigneeRole.EMPLOYEE?e:source.getAssigneeRole()==OnboardingAssigneeRole.MANAGER?e.getManager():null;tasks.save(new OnboardingTask(p,source,assignee,date.plusDays(source.getDaysFromStart())));}
        return OnboardingPlanResponse.from(p);
    }
    @Transactional(readOnly=true) public OnboardingPlanResponse plan(long id,String email,Set<String> roles){OnboardingPlan p=authorizedPlan(id,email,roles);return OnboardingPlanResponse.from(p);}
    @Transactional(readOnly=true) public List<OnboardingTaskResponse> planTasks(long id,String email,Set<String> roles){authorizedPlan(id,email,roles);return tasks.findAllByPlan_IdOrderByDueDateAscIdAsc(id).stream().map(OnboardingTaskResponse::from).toList();}
    @Transactional public OnboardingTaskResponse updateTask(long id,String email,Set<String> roles,OnboardingTaskStatusRequest r){
        OnboardingTask t=tasks.findById(id).orElseThrow(()->new ResourceNotFoundException("Onboarding task",id));
        if(!allowed(t.getPlan(),t.getAssigneeRole(),t.getAssignee(),email,roles))throw new AccessDeniedException("You are not assigned to this onboarding task");
        OnboardingTaskStatus old=t.getStatus(),next=r.status();
        if(old==OnboardingTaskStatus.COMPLETED||!(next==OnboardingTaskStatus.COMPLETED||next==OnboardingTaskStatus.BLOCKED||old==OnboardingTaskStatus.PENDING&&next==OnboardingTaskStatus.IN_PROGRESS||old==OnboardingTaskStatus.BLOCKED&&next==OnboardingTaskStatus.IN_PROGRESS))throw new ConflictException("Invalid onboarding task status transition");
        t.updateStatus(next,r.notes());recalculate(t.getPlan());return OnboardingTaskResponse.from(t);
    }
    @Transactional(readOnly=true) public List<OnboardingDocumentResponse> planDocuments(long id,String email,Set<String> roles){authorizedPlan(id,email,roles);return documents.findAllByPlan_IdOrderBySubmittedAtDesc(id).stream().map(OnboardingDocumentResponse::from).toList();}
    @Transactional public OnboardingDocumentResponse submitDocument(long id,String email,OnboardingDocumentRequest r){OnboardingPlan p=authorizedPlan(id,email,Set.of());if(!p.getEmployee().getWorkEmail().equalsIgnoreCase(email))throw new AccessDeniedException("Only the employee can submit onboarding documents");OnboardingDocument d=documents.save(new OnboardingDocument(p,r.documentType(),r.documentName(),r.referenceUrl()));recalculate(p);return OnboardingDocumentResponse.from(d);}
    @Transactional public OnboardingDocumentResponse reviewDocument(long id,String email,Set<String> roles,OnboardingDocumentReviewRequest r){if(!isHr(roles))throw new AccessDeniedException("HR role required");OnboardingDocument d=documents.findById(id).orElseThrow(()->new ResourceNotFoundException("Onboarding document",id));Employee reviewer=employees.findByWorkEmailIgnoreCase(email).orElse(null);d.review(reviewer,r.verified(),r.comment());recalculate(d.getPlan());return OnboardingDocumentResponse.from(d);}
    private void recalculate(OnboardingPlan p){List<OnboardingTask> list=tasks.findAllByPlan_IdOrderByDueDateAscIdAsc(p.getId());boolean started=list.stream().anyMatch(t->t.getStatus()!=OnboardingTaskStatus.PENDING);boolean done=list.stream().filter(OnboardingTask::isRequired).allMatch(t->t.getStatus()==OnboardingTaskStatus.COMPLETED)&&!documents.existsByPlan_IdAndStatus(p.getId(),OnboardingDocumentStatus.PENDING);p.setStatus(done?OnboardingPlanStatus.COMPLETED:started?OnboardingPlanStatus.IN_PROGRESS:OnboardingPlanStatus.NOT_STARTED);}
    private OnboardingPlan authorizedPlan(long id,String email,Set<String> roles){OnboardingPlan p=plans.findById(id).orElseThrow(()->new ResourceNotFoundException("Onboarding plan",id));if(!isHr(roles)&&!p.getEmployee().getWorkEmail().equalsIgnoreCase(email))throw new AccessDeniedException("Onboarding plan access denied");return p;}
    private boolean allowed(OnboardingPlan p,OnboardingAssigneeRole role,Employee assignee,String email,Set<String> roles){if(isHr(roles))return true;Employee actor=employeeByEmail(email);return switch(role){case EMPLOYEE->p.getEmployee().getWorkEmail().equalsIgnoreCase(email);case MANAGER->assignee!=null&&assignee.getId().equals(actor.getId());case HR->false;};}
    private boolean isHr(Set<String> roles){return roles.stream().anyMatch(HR::contains);}
    private OnboardingTemplate template(long id){return templates.findById(id).orElseThrow(()->new ResourceNotFoundException("Onboarding template",id));}
    private Employee employee(long id){return employees.findById(id).orElseThrow(()->new ResourceNotFoundException("Employee",id));}
    private Employee employeeByEmail(String email){return employees.findByWorkEmailIgnoreCase(email).orElseThrow(()->new ResourceNotFoundException("Employee profile",null));}
}
