package com.hrgenius.recruitment.service;

import com.hrgenius.common.ConflictException;
import com.hrgenius.common.PageRequests;
import com.hrgenius.common.PageResponse;
import com.hrgenius.common.ResourceNotFoundException;
import com.hrgenius.employee.dto.EmployeeRequest;
import com.hrgenius.employee.dto.EmployeeResponse;
import com.hrgenius.employee.entity.Employee;
import com.hrgenius.employee.entity.EmploymentType;
import com.hrgenius.employee.service.EmployeeService;
import com.hrgenius.organization.entity.*;
import com.hrgenius.organization.repository.*;
import com.hrgenius.employee.repository.EmployeeRepository;
import com.hrgenius.onboarding.service.OnboardingService;
import com.hrgenius.notification.OutboundEmail;
import com.hrgenius.recruitment.dto.*;
import com.hrgenius.recruitment.entity.*;
import com.hrgenius.recruitment.repository.*;
import org.springframework.data.domain.Pageable;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Service
public class RecruitmentService {
    private static final Set<String> RECRUITMENT_ROLES = Set.of("ROLE_SUPER_ADMIN", "ROLE_ADMIN", "ROLE_HR_MANAGER", "ROLE_HR_EXECUTIVE", "ROLE_RECRUITER");
    private static final Set<ApplicationStatus> ACTIVE_APPLICATIONS = Set.of(ApplicationStatus.APPLIED, ApplicationStatus.SCREENING,
            ApplicationStatus.INTERVIEW, ApplicationStatus.OFFER, ApplicationStatus.OFFER_ACCEPTED, ApplicationStatus.HIRED);
    private final JobOpeningRepository openings; private final CandidateRepository candidates;
    private final JobApplicationRepository applications; private final ApplicationHistoryRepository history;
    private final InterviewRepository interviews; private final OfferRepository offers;
    private final DepartmentRepository departments; private final DesignationRepository designations;
    private final LocationRepository locations; private final EmployeeRepository employees;
    private final EmployeeService employeeService;
    private final OnboardingService onboardingService;
    private final ApplicationEventPublisher events;

    public RecruitmentService(JobOpeningRepository openings, CandidateRepository candidates, JobApplicationRepository applications,
            ApplicationHistoryRepository history, InterviewRepository interviews, OfferRepository offers,
            DepartmentRepository departments, DesignationRepository designations, LocationRepository locations,
            EmployeeRepository employees, EmployeeService employeeService, OnboardingService onboardingService, ApplicationEventPublisher events) {
        this.openings = openings; this.candidates = candidates; this.applications = applications; this.history = history;
        this.interviews = interviews; this.offers = offers; this.departments = departments; this.designations = designations;
        this.locations = locations; this.employees = employees; this.employeeService = employeeService; this.onboardingService = onboardingService; this.events = events;
    }

    @Transactional(readOnly = true)
    public PageResponse<JobOpeningResponse> listOpenings(JobOpeningStatus status, Pageable pageable) {
        var page = status == null ? openings.findAll(PageRequests.capped(pageable)) : openings.findAllByStatus(status, PageRequests.capped(pageable));
        return PageResponse.from(page, JobOpeningResponse::from);
    }
    @Transactional
    public JobOpeningResponse createOpening(JobOpeningRequest request) {
        JobOpening opening = buildOpening(request); validateOpening(opening);
        return JobOpeningResponse.from(openings.save(opening));
    }
    @Transactional
    public JobOpeningResponse updateOpening(Long id, JobOpeningRequest request) {
        JobOpening current = opening(id);
        if (current.getStatus() == JobOpeningStatus.CLOSED) throw new ConflictException("Closed openings cannot be edited");
        JobOpening update = buildOpening(request); validateOpening(update); current.update(update);
        return JobOpeningResponse.from(openings.save(current));
    }
    @Transactional
    public JobOpeningResponse setOpeningStatus(Long id, JobOpeningStatus status) {
        JobOpening opening = opening(id);
        if (status == JobOpeningStatus.OPEN && opening.getClosingDate() != null && opening.getClosingDate().isBefore(LocalDate.now()))
            throw new ConflictException("An opening with a past closing date cannot be opened");
        if (opening.getStatus() == JobOpeningStatus.CLOSED && status != JobOpeningStatus.CLOSED)
            throw new ConflictException("A closed opening cannot be reopened");
        opening.setStatus(status); return JobOpeningResponse.from(openings.save(opening));
    }

    @Transactional(readOnly = true)
    public PageResponse<CandidateResponse> listCandidates(String query, Pageable pageable) {
        var bounded = PageRequests.capped(pageable);
        var page = query == null || query.isBlank() ? candidates.findAll(bounded)
                : candidates.findAllByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseOrEmailContainingIgnoreCase(
                        query.trim(), query.trim(), query.trim(), bounded);
        return PageResponse.from(page, CandidateResponse::from);
    }
    @Transactional
    public CandidateResponse createCandidate(CandidateRequest request) {
        if (candidates.existsByEmailIgnoreCase(request.email())) throw new ConflictException("A candidate with this email already exists");
        return CandidateResponse.from(candidates.save(new Candidate(request.firstName(), request.lastName(), request.email(),
                request.phone(), request.resumeUrl(), request.source())));
    }
    @Transactional(readOnly = true)
    public CandidateResponse candidate(Long id) { return CandidateResponse.from(candidates.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Candidate", id))); }

    @Transactional(readOnly = true)
    public PageResponse<ApplicationResponse> listApplications(Long openingId, ApplicationStatus status, Pageable pageable) {
        var bounded = PageRequests.capped(pageable);
        var page = openingId != null ? applications.findAllByOpening_Id(openingId, bounded)
                : status != null ? applications.findAllByStatus(status, bounded) : applications.findAll(bounded);
        return PageResponse.from(page, ApplicationResponse::from);
    }
    @Transactional
    public ApplicationResponse apply(ApplicationCreateRequest request, String actorEmail) {
        JobOpening opening = opening(request.openingId()); Candidate candidate = candidates.findById(request.candidateId())
                .orElseThrow(() -> new ResourceNotFoundException("Candidate", request.candidateId()));
        if (opening.getStatus() != JobOpeningStatus.OPEN) throw new ConflictException("Applications can only be attached to open positions");
        if (opening.getClosingDate() != null && opening.getClosingDate().isBefore(LocalDate.now())) throw new ConflictException("This opening has closed");
        if (applications.existsByOpening_IdAndCandidate_Id(opening.getId(), candidate.getId())) throw new ConflictException("Candidate already applied to this opening");
        if (applications.countByOpening_IdAndStatusIn(opening.getId(), ACTIVE_APPLICATIONS) >= opening.getVacancies())
            throw new ConflictException("This opening has no remaining vacancies");
        JobApplication application = applications.save(new JobApplication(opening, candidate));
        addHistory(application, null, ApplicationStatus.APPLIED, "Application received", actorEmail);
        return ApplicationResponse.from(application);
    }
    @Transactional
    public ApplicationResponse changeStage(Long id, ApplicationStageRequest input, String actorEmail) {
        JobApplication application = application(id); ApplicationStatus from = application.getStatus();
        if (!Set.of(ApplicationStatus.SCREENING, ApplicationStatus.REJECTED, ApplicationStatus.WITHDRAWN).contains(input.status()))
            throw new ConflictException("This stage is managed by interview, offer, or conversion actions");
        boolean allowed = (from == ApplicationStatus.APPLIED && Set.of(ApplicationStatus.SCREENING, ApplicationStatus.REJECTED, ApplicationStatus.WITHDRAWN).contains(input.status()))
                || (from == ApplicationStatus.SCREENING && Set.of(ApplicationStatus.REJECTED, ApplicationStatus.WITHDRAWN).contains(input.status()))
                || (from == ApplicationStatus.INTERVIEW && Set.of(ApplicationStatus.REJECTED, ApplicationStatus.WITHDRAWN).contains(input.status()));
        if (!allowed) throw new ConflictException("Invalid application stage transition from " + from + " to " + input.status());
        application.setStatus(input.status()); applications.save(application);
        addHistory(application, from, input.status(), input.note(), actorEmail);
        return ApplicationResponse.from(application);
    }

    @Transactional(readOnly = true)
    public PageResponse<InterviewResponse> listInterviews(String email, Set<String> roles, Pageable pageable) {
        var bounded = PageRequests.capped(pageable);
        var page = roles.stream().anyMatch(RECRUITMENT_ROLES::contains) ? interviews.findAll(bounded)
                : roles.contains("ROLE_MANAGER") ? interviews.findAllByInterviewer_Id(
                        employees.findByWorkEmailIgnoreCase(email).orElseThrow(() -> new ResourceNotFoundException("Employee profile", null)).getId(), bounded)
                : throwAccessDenied();
        return PageResponse.from(page, InterviewResponse::from);
    }
    @Transactional
    public InterviewResponse scheduleInterview(InterviewRequest input, String actorEmail) {
        JobApplication application = application(input.applicationId()); Employee interviewer = employee(input.interviewerId());
        if (!Set.of(ApplicationStatus.APPLIED, ApplicationStatus.SCREENING, ApplicationStatus.INTERVIEW).contains(application.getStatus()))
            throw new ConflictException("This application is not at an interviewable stage");
        Interview interview = interviews.save(new Interview(application, interviewer, input.scheduledAt().toInstant(),
                input.durationMinutes(), input.interviewType()));
        if (application.getStatus() != ApplicationStatus.INTERVIEW) {
            ApplicationStatus from = application.getStatus(); application.setStatus(ApplicationStatus.INTERVIEW); applications.save(application);
            addHistory(application, from, ApplicationStatus.INTERVIEW, "Interview scheduled", actorEmail);
        }
        return InterviewResponse.from(interview);
    }
    @Transactional
    public InterviewResponse recordFeedback(Long id, String email, Set<String> roles, InterviewFeedbackRequest input) {
        Interview interview = interviews.findById(id).orElseThrow(() -> new ResourceNotFoundException("Interview", id));
        boolean privileged = roles.stream().anyMatch(RECRUITMENT_ROLES::contains);
        if (!privileged && !interview.getInterviewer().getWorkEmail().equalsIgnoreCase(email))
            throw new AccessDeniedException("Only the assigned interviewer or recruitment team can submit feedback");
        if (interview.getStatus() != InterviewStatus.SCHEDULED) throw new ConflictException("Only scheduled interviews can be completed");
        interview.complete(input.feedback(), input.rating()); return InterviewResponse.from(interviews.save(interview));
    }

    @Transactional
    public OfferResponse createOffer(OfferRequest input) {
        JobApplication application = application(input.applicationId());
        if (application.getStatus() != ApplicationStatus.INTERVIEW || !interviews.existsByApplication_IdAndStatus(application.getId(), InterviewStatus.COMPLETED))
            throw new ConflictException("An offer requires a completed interview");
        if (offers.findByApplication_Id(application.getId()).isPresent()) throw new ConflictException("An offer already exists for this application");
        if (input.expiresOn().isBefore(LocalDate.now()) || input.expiresOn().isAfter(input.startDate()))
            throw new ConflictException("Offer expiry must be today or later and cannot follow the start date");
        return OfferResponse.from(offers.save(new Offer(application, input.salary(), input.currency(), input.startDate(), input.expiresOn())));
    }
    @Transactional(readOnly = true)
    public List<OfferResponse> listOffers(Long applicationId) {
        application(applicationId);
        return offers.findAllByApplication_IdOrderByCreatedAtDesc(applicationId).stream().map(OfferResponse::from).toList();
    }
    @Transactional
    public OfferResponse sendOffer(Long id, String actorEmail) {
        Offer offer = offer(id); ApplicationStatus from = offer.getApplication().getStatus();
        if (from != ApplicationStatus.INTERVIEW) throw new ConflictException("This application is no longer eligible for an offer");
        if (offer.getExpiresOn().isBefore(LocalDate.now())) throw new ConflictException("This offer has expired");
        try { offer.send(); } catch (IllegalStateException ex) { throw new ConflictException(ex.getMessage()); }
        offer.getApplication().setStatus(ApplicationStatus.OFFER); applications.save(offer.getApplication());
        addHistory(offer.getApplication(), from, ApplicationStatus.OFFER, "Offer sent", actorEmail);
        var candidate=offer.getApplication().getCandidate();
        events.publishEvent(new OutboundEmail(candidate.getEmail(),"Offer from HRGenius",
                "Hello "+candidate.getFirstName()+",\n\nWe are pleased to extend an offer for "+offer.getApplication().getOpening().getTitle()+". The proposed salary is "+offer.getCurrency()+" "+offer.getSalary()+", with a start date of "+offer.getStartDate()+". Please respond by "+offer.getExpiresOn()+" by contacting your recruiting representative."));
        return OfferResponse.from(offers.save(offer));
    }
    @Transactional
    public OfferResponse decideOffer(Long id, boolean accepted, String actorEmail) {
        Offer offer = offer(id);
        if (offer.getApplication().getStatus() != ApplicationStatus.OFFER) throw new ConflictException("This application is not awaiting an offer decision");
        if (offer.getExpiresOn().isBefore(LocalDate.now())) throw new ConflictException("This offer has expired");
        ApplicationStatus from = offer.getApplication().getStatus();
        try { offer.decide(accepted); } catch (IllegalStateException ex) { throw new ConflictException(ex.getMessage()); }
        ApplicationStatus to = accepted ? ApplicationStatus.OFFER_ACCEPTED : ApplicationStatus.REJECTED;
        offer.getApplication().setStatus(to); applications.save(offer.getApplication());
        addHistory(offer.getApplication(), from, to, accepted ? "Offer accepted" : "Offer declined", actorEmail);
        return OfferResponse.from(offers.save(offer));
    }
    @Transactional
    public EmployeeResponse convertCandidate(Long applicationId, CandidateConversionRequest input, String actorEmail) {
        JobApplication application = application(applicationId);
        if (application.getStatus() != ApplicationStatus.OFFER_ACCEPTED) throw new ConflictException("Only an accepted offer can be converted");
        if (application.getEmployee() != null) throw new ConflictException("This application has already been converted");
        JobOpening opening = application.getOpening(); Candidate candidate = application.getCandidate();
        EmployeeRequest employeeInput = new EmployeeRequest(input.employeeNumber(), candidate.getFirstName(), candidate.getLastName(),
                input.workEmail(), candidate.getEmail(), candidate.getPhone(), null, null, null, null, null, null, null, null,
                input.employmentType(), input.dateOfJoining(), null, null, opening.getRequiredSkills(),
                opening.getDepartment() == null ? null : opening.getDepartment().getId(),
                opening.getDesignation() == null ? null : opening.getDesignation().getId(),
                opening.getHiringManager() == null ? null : opening.getHiringManager().getId(),
                opening.getLocation() == null ? null : opening.getLocation().getId());
        Employee employee = employeeService.create(employeeInput);
        onboardingService.startPlan(input.onboardingTemplateId(), employee.getId(), input.dateOfJoining());
        ApplicationStatus from = application.getStatus(); application.setEmployee(employee); applications.save(application);
        addHistory(application, from, ApplicationStatus.HIRED, "Accepted offer converted to employee", actorEmail);
        events.publishEvent(new OutboundEmail(employee.getWorkEmail(),"Welcome to HRGenius",
                "Hello "+employee.getFirstName()+",\n\nYour employee profile and onboarding plan have been created. Your start date is "+employee.getDateOfJoining()+". Please contact HR for access instructions."));
        return EmployeeResponse.from(employee);
    }

    private JobOpening buildOpening(JobOpeningRequest r) {
        return new JobOpening(r.title(), activeDepartment(r.departmentId()), activeDesignation(r.designationId(), r.departmentId()),
                activeLocation(r.locationId()), r.vacancies(), r.description(), r.requiredSkills(), r.minExperienceYears(),
                r.maxExperienceYears(), r.employmentType(), r.salaryMin(), r.salaryMax(), activeEmployee(r.hiringManagerId()),
                activeEmployee(r.recruiterId()), r.openingDate(), r.closingDate());
    }
    private void validateOpening(JobOpening j) {
        if (j.getClosingDate() != null && j.getClosingDate().isBefore(j.getOpeningDate())) throw new ConflictException("Closing date cannot precede opening date");
        if (j.getMinExperienceYears() != null && j.getMaxExperienceYears() != null && j.getMaxExperienceYears().compareTo(j.getMinExperienceYears()) < 0)
            throw new ConflictException("Maximum experience cannot be below minimum experience");
        if (j.getSalaryMin() != null && j.getSalaryMax() != null && j.getSalaryMax().compareTo(j.getSalaryMin()) < 0)
            throw new ConflictException("Maximum salary cannot be below minimum salary");
        if (j.getDesignation() != null && !Objects.equals(j.getDesignation().getDepartment() == null ? null : j.getDesignation().getDepartment().getId(),
                j.getDepartment() == null ? null : j.getDepartment().getId()))
            throw new ConflictException("Designation must belong to the selected department");
    }
    private Department activeDepartment(Long id) { if (id == null) return null; Department d = departments.findById(id).orElseThrow(() -> new ResourceNotFoundException("Department", id)); if (!d.isActive()) throw new ConflictException("Department is inactive"); return d; }
    private Designation activeDesignation(Long id, Long departmentId) { if (id == null) return null; Designation d = designations.findById(id).orElseThrow(() -> new ResourceNotFoundException("Designation", id)); if (!d.isActive()) throw new ConflictException("Designation is inactive"); return d; }
    private Location activeLocation(Long id) { if (id == null) return null; Location l = locations.findById(id).orElseThrow(() -> new ResourceNotFoundException("Location", id)); if (!l.isActive()) throw new ConflictException("Location is inactive"); return l; }
    private Employee activeEmployee(Long id) { if (id == null) return null; Employee e = employee(id); if (e.getStatus() != com.hrgenius.employee.entity.EmployeeStatus.ACTIVE) throw new ConflictException("Assigned employee must be active"); return e; }
    private Employee employee(Long id) { return employees.findById(id).orElseThrow(() -> new ResourceNotFoundException("Employee", id)); }
    private JobOpening opening(Long id) { return openings.findById(id).orElseThrow(() -> new ResourceNotFoundException("Job opening", id)); }
    private JobApplication application(Long id) { return applications.findById(id).orElseThrow(() -> new ResourceNotFoundException("Application", id)); }
    private Offer offer(Long id) { return offers.findById(id).orElseThrow(() -> new ResourceNotFoundException("Offer", id)); }
    private void addHistory(JobApplication app, ApplicationStatus from, ApplicationStatus to, String note, String email) {
        Employee actor = email == null ? null : employees.findByWorkEmailIgnoreCase(email).orElse(null);
        history.save(new ApplicationHistory(app, from == null ? null : from.name(), to.name(), note, actor));
    }
    private org.springframework.data.domain.Page<Interview> throwAccessDenied() {
        throw new AccessDeniedException("Only recruitment staff and assigned managers can view interviews");
    }
}
