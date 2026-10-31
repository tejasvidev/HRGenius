import { useState, type FormEvent } from 'react'
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import axios from 'axios'
import { Activity, ArrowLeft, ArrowRight, Award, BarChart3, Bell, BriefcaseBusiness, Building2, CalendarDays, ClipboardCheck, Clock3, CircleDollarSign, HeartPulse, Inbox, LogOut, Search, ShieldCheck, Users } from 'lucide-react'
import { api } from './api/client'
import { useAuth } from './features/auth/AuthContext'
import './features/auth/auth.css'
import './features/employees/employees.css'
import { OnboardingWorkspace } from './features/onboarding/OnboardingWorkspace'
import { LeaveAdminWorkspace } from './features/leave/LeaveAdminWorkspace'
import { RecruitmentWorkflow } from './features/recruitment/RecruitmentWorkflow'
import { EmployeeDetailDialog } from './features/employees/EmployeeDetailDialog'
import { OrganizationWorkspace } from './features/organization/OrganizationWorkspace'
import { AttendanceWorkspace } from './features/attendance/AttendanceWorkspace'
import { PayrollWorkspace } from './features/payroll/PayrollWorkspace'
import { PerformanceWorkspace } from './features/performance/PerformanceWorkspace'
import { AnnouncementsWorkspace } from './features/announcements/AnnouncementsWorkspace'
import { NotificationsWorkspace } from './features/notifications/NotificationsWorkspace'
import { HeadcountDashboard } from './features/reports/HeadcountDashboard'
import { ReportsWorkspace } from './features/reports/ReportsWorkspace'

function App() {
  const { user, initializing, login, logout } = useAuth()
  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')
  const [authError, setAuthError] = useState('')
  const [submitting, setSubmitting] = useState(false)
  const [workspacePage, setWorkspacePage] = useState<'overview' | 'people' | 'attendance' | 'leave' | 'payroll' | 'performance' | 'announcements' | 'notifications' | 'reports' | 'recruitment' | 'onboarding' | 'organization'>('overview')

  const health = useQuery({
    queryKey: ['system-health'],
    queryFn: async () => (await api.get<{ status: string }>('/actuator/health')).data,
    refetchInterval: 30_000,
  })

  async function submitLogin(event: FormEvent<HTMLFormElement>) {
    event.preventDefault()
    setAuthError('')
    setSubmitting(true)
    try {
      await login(email, password)
      setPassword('')
    } catch (error) {
      setAuthError(axios.isAxiosError(error)
        ? error.response?.data?.detail ?? 'Unable to connect. Check that the HRGenius API is running.'
        : 'Sign-in failed. Please try again.')
    } finally {
      setSubmitting(false)
    }
  }

  async function signOut() {
    try { await logout() } catch { setAuthError('Your session was cleared locally, but the server could not be reached.') }
  }

  if (initializing) return <div className="auth-loading"><span className="brand-mark">H</span><p>Restoring your secure session…</p></div>

  if (!user) return <main className="login-shell">
    <section className="login-panel">
      <div className="brand login-brand"><span className="brand-mark">H</span><span>HR<span className="brand-accent">Genius</span></span></div>
      <p className="eyebrow">SECURE HR WORKSPACE</p>
      <h1>Welcome back.</h1>
      <p className="login-copy">Sign in to manage your organization's people and work.</p>
      <form className="login-form" onSubmit={submitLogin}>
        <label htmlFor="email">Work email</label>
        <input id="email" name="email" autoComplete="username" type="email" required value={email} onChange={(event) => setEmail(event.target.value)} placeholder="you@company.com" />
        <div className="password-label"><label htmlFor="password">Password</label></div>
        <input id="password" name="password" autoComplete="current-password" type="password" required value={password} onChange={(event) => setPassword(event.target.value)} placeholder="Enter your password" />
        {authError && <p className="login-error" role="alert">{authError}</p>}
        <button className="login-button" type="submit" disabled={submitting}>{submitting ? 'Signing in…' : <>Sign in <ArrowRight size={16} /></>}</button>
      </form>
      <div className="login-security"><ShieldCheck size={15} /><span>Protected with secure, short-lived sessions</span></div>
      <p className="login-help">Need an account? Contact your HRGenius administrator.</p>
    </section>
    <aside className="login-art">
      <div className="art-glow glow-one" /><div className="art-glow glow-two" />
      <div className="art-copy"><span className="art-kicker">PEOPLE FIRST. BUILT FOR HR.</span><h2>Better work starts<br />with better people<br />operations.</h2><p>One secure space for the moments that shape every employee's journey.</p>
        <div className="art-stat"><div className="stat-icon"><Users size={17} /></div><div><strong>One connected workspace</strong><small>From first day to every milestone</small></div></div>
      </div>
      <span className="art-footer">HRGenius · People operations</span>
    </aside>
  </main>

  return <main className="shell">
    <aside className="sidebar">
      <div className="brand"><span className="brand-mark">H</span><span>HR<span className="brand-accent">Genius</span></span></div>
      <p className="nav-label">WORKSPACE</p>
      <button className={`nav-link nav-button ${workspacePage === 'overview' ? 'active' : ''}`} onClick={() => setWorkspacePage('overview')}><Activity size={18} /> Overview</button>
      {user.roles.some((role) => ['SUPER_ADMIN', 'ADMIN', 'HR_MANAGER', 'HR_EXECUTIVE'].includes(role))
        ? <button className={`nav-link nav-button ${workspacePage === 'people' ? 'active' : ''}`} onClick={() => setWorkspacePage('people')}><Users size={18} /> People</button>
        : <span className="nav-link disabled"><Users size={18} /> My profile</span>}
      {user.roles.some((role) => ['EMPLOYEE', 'MANAGER', 'SUPER_ADMIN', 'ADMIN', 'HR_MANAGER', 'HR_EXECUTIVE'].includes(role)) &&
        <button className={`nav-link nav-button ${workspacePage === 'leave' ? 'active' : ''}`} onClick={() => setWorkspacePage('leave')}><CalendarDays size={18} /> Time off</button>}
      {user.roles.some((role) => ['EMPLOYEE', 'MANAGER', 'SUPER_ADMIN', 'ADMIN', 'HR_MANAGER', 'HR_EXECUTIVE'].includes(role)) &&
        <button className={`nav-link nav-button ${workspacePage === 'attendance' ? 'active' : ''}`} onClick={() => setWorkspacePage('attendance')}><Clock3 size={18} /> Attendance</button>}
      {user.roles.some((role) => ['EMPLOYEE', 'SUPER_ADMIN', 'ADMIN', 'HR_MANAGER', 'HR_EXECUTIVE'].includes(role)) &&
        <button className={`nav-link nav-button ${workspacePage === 'payroll' ? 'active' : ''}`} onClick={() => setWorkspacePage('payroll')}><CircleDollarSign size={18} /> Payroll</button>}
      {user.roles.some((role) => ['EMPLOYEE', 'MANAGER', 'SUPER_ADMIN', 'ADMIN', 'HR_MANAGER'].includes(role)) &&
        <button className={`nav-link nav-button ${workspacePage === 'performance' ? 'active' : ''}`} onClick={() => setWorkspacePage('performance')}><Award size={18} /> Performance</button>}
      <button className={`nav-link nav-button ${workspacePage === 'announcements' ? 'active' : ''}`} onClick={() => setWorkspacePage('announcements')}><Bell size={18} /> Announcements</button>
      <button className={`nav-link nav-button ${workspacePage === 'notifications' ? 'active' : ''}`} onClick={() => setWorkspacePage('notifications')}><Inbox size={18} /> Notifications</button>
      {user.roles.some((role) => ['SUPER_ADMIN', 'ADMIN', 'HR_MANAGER', 'HR_EXECUTIVE', 'RECRUITER'].includes(role)) &&
        <button className={`nav-link nav-button ${workspacePage === 'recruitment' ? 'active' : ''}`} onClick={() => setWorkspacePage('recruitment')}><BriefcaseBusiness size={18} /> Recruiting</button>}
      {user.roles.some((role) => ['SUPER_ADMIN', 'ADMIN', 'HR_MANAGER', 'HR_EXECUTIVE', 'EMPLOYEE', 'MANAGER'].includes(role)) &&
        <button className={`nav-link nav-button ${workspacePage === 'onboarding' ? 'active' : ''}`} onClick={() => setWorkspacePage('onboarding')}><ClipboardCheck size={18} /> Onboarding</button>}
      {user.roles.some((role) => ['SUPER_ADMIN', 'ADMIN', 'HR_MANAGER'].includes(role))
        ? <button className={`nav-link nav-button ${workspacePage === 'organization' ? 'active' : ''}`} onClick={() => setWorkspacePage('organization')}><Building2 size={18} /> Organization</button>
        : <span className="nav-link disabled"><Building2 size={18} /> Organization</span>}
      {user.roles.some((role) => ['SUPER_ADMIN', 'ADMIN', 'HR_MANAGER', 'HR_EXECUTIVE', 'RECRUITER'].includes(role)) &&
        <button className={`nav-link nav-button ${workspacePage === 'reports' ? 'active' : ''}`} onClick={() => setWorkspacePage('reports')}><BarChart3 size={18} /> Reports</button>}
      <div className="sidebar-bottom"><div className="avatar">{user.firstName.slice(0, 1)}{user.lastName.slice(0, 1)}</div><div><strong>{user.firstName} {user.lastName}</strong><small>{user.roles[0]?.replaceAll('_', ' ')}</small></div><button className="icon-button" onClick={signOut} title="Sign out" aria-label="Sign out"><LogOut size={16} /></button></div>
    </aside>
    <section className="main-area" id="overview">
      <header className="topbar"><div><span className="crumb">Workspace</span><span className="crumb-sep">/</span><strong>{workspacePage === 'people' ? 'People' : workspacePage === 'attendance' ? 'Attendance' : workspacePage === 'payroll' ? 'Payroll' : workspacePage === 'performance' ? 'Performance' : workspacePage === 'announcements' ? 'Announcements' : workspacePage === 'notifications' ? 'Notifications' : workspacePage === 'reports' ? 'Reports' : workspacePage === 'leave' ? 'Time off' : workspacePage === 'recruitment' ? 'Recruiting' : workspacePage === 'onboarding' ? 'Onboarding' : workspacePage === 'organization' ? 'Organization' : 'Overview'}</strong></div><span className="env-pill">DEVELOPMENT</span></header>
      {workspacePage === 'reports' ? <ReportsWorkspace roles={user.roles} /> : workspacePage === 'notifications' ? <NotificationsWorkspace onBack={() => setWorkspacePage('overview')} /> : workspacePage === 'announcements' ? <AnnouncementsWorkspace roles={user.roles} onBack={() => setWorkspacePage('overview')} /> : workspacePage === 'performance' ? <PerformanceWorkspace roles={user.roles} onBack={() => setWorkspacePage('overview')} /> : workspacePage === 'payroll' ? <PayrollWorkspace roles={user.roles} onBack={() => setWorkspacePage('overview')} /> : workspacePage === 'attendance' ? <AttendanceWorkspace roles={user.roles} onBack={() => setWorkspacePage('overview')} /> : workspacePage === 'organization' ? <OrganizationWorkspace roles={user.roles} onBack={() => setWorkspacePage('overview')} /> : workspacePage === 'onboarding' ? <OnboardingWorkspace roles={user.roles} onBack={() => setWorkspacePage('overview')} /> : workspacePage === 'recruitment' ? <RecruitmentWorkspace onBack={() => setWorkspacePage('overview')} /> : workspacePage === 'leave' ? (user.roles.some((role) => ['SUPER_ADMIN', 'ADMIN', 'HR_MANAGER', 'HR_EXECUTIVE'].includes(role)) ? <LeaveAdminWorkspace roles={user.roles} onBack={() => setWorkspacePage('overview')} /> : <MyLeave onBack={() => setWorkspacePage('overview')} />) : workspacePage === 'people' ? <EmployeeDirectory onBack={() => setWorkspacePage('overview')}
        canManage={user.roles.some((role) => ['SUPER_ADMIN', 'ADMIN', 'HR_MANAGER'].includes(role))} /> : <div className="content">
        <div className="welcome"><div><p className="eyebrow">HR OPERATIONS</p><h1>Welcome, {user.firstName}.</h1><p className="subheading">Your HRGenius workspace is ready for the next setup steps.</p></div><div className="welcome-icon"><Users size={26} /></div></div>
        <HeadcountDashboard roles={user.roles} />
        <div className="status-card"><div className="status-heading"><div className="status-icon"><HeartPulse size={19} /></div><div><h2>System status</h2><p>Live connection to the HRGenius API</p></div></div><div className="health-value"><span className={`health-dot ${health.data?.status === 'UP' ? 'up' : health.isError ? 'down' : ''}`} />{health.isLoading ? 'Checking…' : health.data?.status === 'UP' ? 'API online' : health.isError ? 'API unavailable' : 'Connecting'}</div>{health.isError && <p className="health-help">The backend health endpoint did not respond.</p>}</div>
        <div className="section-heading"><div><h2>Workspace setup</h2><p>Core modules will appear here as they are implemented.</p></div><span className="phase-count">PHASE 2</span></div>
        <div className="module-grid">
          <article className="module-card"><div className="module-top"><span className="module-symbol blue"><Activity size={19} /></span><span className="module-status">FOUNDATION</span></div><h3>Platform foundation</h3><p>API service, configuration, health monitoring, and API documentation.</p></article>
          <article className="module-card"><div className="module-top"><span className="module-symbol violet"><Users size={19} /></span><span className="module-status">UP NEXT</span></div><h3>People &amp; organization</h3><p>Teams, locations, and a secure employee directory.</p></article>
          <article className="module-card"><div className="module-top"><span className="module-symbol green"><Building2 size={19} /></span><span className="module-status">PLANNED</span></div><h3>Employee lifecycle</h3><p>Recruitment, onboarding, attendance, leave, payroll, and growth.</p></article>
        </div>
        <footer>HRGenius <span>·</span> Secure people operations</footer>
      </div>}
    </section>
  </main>
}

interface Employee {
  id: number
  employeeNumber: string
  firstName: string
  lastName: string
  workEmail: string
  status: 'ACTIVE' | 'ON_LEAVE' | 'NOTICE_PERIOD' | 'SEPARATED'
  departmentName: string | null
  designationTitle: string | null
  locationName: string | null
  dateOfJoining: string
}

interface EmployeePage {
  content: Employee[]
  page: number
  pageSize: number
  totalElements: number
  totalPages: number
}

function EmployeeDirectory({ onBack, canManage }: { onBack: () => void; canManage: boolean }) {
  const [searchInput, setSearchInput] = useState('')
  const [query, setQuery] = useState('')
  const [page, setPage] = useState(0)
  const [createOpen, setCreateOpen] = useState(false)
  const [selectedEmployeeId, setSelectedEmployeeId] = useState<number | null>(null)
  const [createError, setCreateError] = useState('')
  const queryClient = useQueryClient()
  const employees = useQuery({
    queryKey: ['employees', query, page],
    queryFn: async () => (await api.get<EmployeePage>('/api/v1/employees', {
      params: { q: query || undefined, page, size: 20, sort: 'lastName,asc' },
    })).data,
  })
  const createEmployee = useMutation({
    mutationFn: async (payload: { employeeNumber: string; firstName: string; lastName: string; workEmail: string; employmentType: string; dateOfJoining: string }) =>
      api.post('/api/v1/employees', payload),
    onSuccess: async () => {
      setCreateOpen(false)
      setCreateError('')
      await queryClient.invalidateQueries({ queryKey: ['employees'] })
    },
    onError: (error) => setCreateError(axios.isAxiosError(error)
      ? error.response?.data?.detail ?? 'Unable to create this employee.'
      : 'Unable to create this employee.'),
  })

  function search(event: FormEvent<HTMLFormElement>) {
    event.preventDefault()
    setPage(0)
    setQuery(searchInput.trim())
  }

  return <div className="content">
    <div className="directory-heading">
      <div><p className="eyebrow">PEOPLE OPERATIONS</p><h1>Employee directory</h1><p className="subheading">Search your organization's employee records.</p></div>
      <div className="directory-actions">{canManage && <button className="search-button" onClick={() => { setCreateError(''); setCreateOpen(true) }}>Add employee</button>}
        <button className="secondary-button" onClick={onBack}><ArrowLeft size={15} /> Overview</button></div>
    </div>
    <form className="directory-search" onSubmit={search}>
      <Search size={17} aria-hidden="true" />
      <input aria-label="Search employees" placeholder="Search name, email, or employee ID" value={searchInput} onChange={(event) => setSearchInput(event.target.value)} />
      <button type="submit" className="search-button">Search</button>
    </form>
    <section className="directory-card" aria-live="polite">
      <div className="directory-table-heading"><div><h2>Employees</h2><p>{employees.data ? `${employees.data.totalElements} records` : 'Current employee records'}</p></div></div>
      {employees.isLoading && <p className="directory-message">Loading employee records…</p>}
      {employees.isError && <div className="directory-message error" role="alert">Could not load employees. Check your access or try again.<button className="text-button" onClick={() => void employees.refetch()}>Retry</button></div>}
      {employees.data && employees.data.content.length === 0 && <p className="directory-message">No employees found. Try a different search.</p>}
      {employees.data && employees.data.content.length > 0 && <div className="table-scroll"><table><thead><tr><th>Employee</th><th>Department</th><th>Location</th><th>Joined</th><th>Status</th><th></th></tr></thead><tbody>
        {employees.data.content.map((employee) => <tr key={employee.id}>
          <td><div className="employee-name">{employee.firstName} {employee.lastName}</div><div className="employee-meta">{employee.employeeNumber} · {employee.workEmail}</div></td>
          <td>{employee.designationTitle ? <><span className="cell-primary">{employee.designationTitle}</span><span className="cell-secondary">{employee.departmentName ?? '—'}</span></> : employee.departmentName ?? '—'}</td>
          <td>{employee.locationName ?? '—'}</td><td>{new Date(`${employee.dateOfJoining}T00:00:00`).toLocaleDateString()}</td>
          <td><span className={`status-badge ${employee.status.toLowerCase().replace('_', '-')}`}>{employee.status.replaceAll('_', ' ')}</span></td><td><button className="text-button" onClick={() => setSelectedEmployeeId(employee.id)}>Details</button></td>
        </tr>)}
      </tbody></table></div>}
      {employees.data && employees.data.totalPages > 1 && <div className="pagination"><span>Page {employees.data.page + 1} of {employees.data.totalPages}</span><div><button aria-label="Previous page" disabled={page === 0} onClick={() => setPage((value) => value - 1)}><ArrowLeft size={15} /></button><button aria-label="Next page" disabled={page + 1 >= employees.data!.totalPages} onClick={() => setPage((value) => value + 1)}><ArrowRight size={15} /></button></div></div>}
    </section>
    {createOpen && <div className="modal-backdrop" role="presentation" onMouseDown={(event) => { if (event.target === event.currentTarget) setCreateOpen(false) }}>
      <section className="employee-modal" role="dialog" aria-modal="true" aria-labelledby="create-employee-title">
        <div className="modal-heading"><div><p className="eyebrow">NEW RECORD</p><h2 id="create-employee-title">Add employee</h2></div><button className="modal-close" onClick={() => setCreateOpen(false)} aria-label="Close">×</button></div>
        <form className="employee-form" onSubmit={(event) => {
          event.preventDefault()
          const values = new FormData(event.currentTarget)
          createEmployee.mutate({
            employeeNumber: String(values.get('employeeNumber')).trim(),
            firstName: String(values.get('firstName')).trim(),
            lastName: String(values.get('lastName')).trim(),
            workEmail: String(values.get('workEmail')).trim(),
            employmentType: String(values.get('employmentType')),
            dateOfJoining: String(values.get('dateOfJoining')),
          })
        }}>
          <label>Employee ID<input name="employeeNumber" required maxLength={32} pattern="[A-Za-z0-9_-]{2,32}" placeholder="e.g. E-1042" /></label>
          <div className="form-row"><label>First name<input name="firstName" required maxLength={100} /></label><label>Last name<input name="lastName" required maxLength={100} /></label></div>
          <label>Work email<input name="workEmail" type="email" required maxLength={254} placeholder="name@company.com" /></label>
          <div className="form-row"><label>Employment type<select name="employmentType" defaultValue="FULL_TIME"><option value="FULL_TIME">Full time</option><option value="PART_TIME">Part time</option><option value="CONTRACT">Contract</option><option value="INTERN">Intern</option><option value="TEMPORARY">Temporary</option></select></label><label>Joining date<input name="dateOfJoining" type="date" required /></label></div>
          {createError && <p className="login-error" role="alert">{createError}</p>}
          <div className="modal-actions"><button type="button" className="secondary-button" onClick={() => setCreateOpen(false)}>Cancel</button><button type="submit" className="search-button" disabled={createEmployee.isPending}>{createEmployee.isPending ? 'Saving…' : 'Create employee'}</button></div>
        </form>
      </section>
    </div>}
    {selectedEmployeeId != null && <EmployeeDetailDialog id={selectedEmployeeId} canManage={canManage} onClose={() => setSelectedEmployeeId(null)} />}
  </div>
}

interface LeaveType { id: number; name: string; code: string }
interface LeaveBalance { id: number; leaveTypeName: string; entitledDays: number; usedDays: number; remainingDays: number }
interface LeaveRequestRecord { id: number; leaveTypeName: string; startDate: string; endDate: string; requestedDays: number; reason: string; status: string }
interface LeaveRequestPage { content: LeaveRequestRecord[]; totalElements: number }

function MyLeave({ onBack }: { onBack: () => void }) {
  const queryClient = useQueryClient()
  const [submitError, setSubmitError] = useState('')
  const types = useQuery({ queryKey: ['leave-types'], queryFn: async () => (await api.get<LeaveType[]>('/api/v1/leave/types')).data })
  const balances = useQuery({ queryKey: ['leave-balances-me'], queryFn: async () => (await api.get<LeaveBalance[]>('/api/v1/leave/balances/me')).data })
  const requests = useQuery({ queryKey: ['leave-requests-me'], queryFn: async () => (await api.get<LeaveRequestPage>('/api/v1/leave/requests', { params: { page: 0, size: 50, sort: 'createdAt,desc' } })).data })
  const submitRequest = useMutation({
    mutationFn: async (body: { leaveTypeId: number; startDate: string; endDate: string; reason: string }) => api.post('/api/v1/leave/requests', body),
    onSuccess: async () => { setSubmitError(''); await Promise.all([queryClient.invalidateQueries({ queryKey: ['leave-requests-me'] }), queryClient.invalidateQueries({ queryKey: ['leave-balances-me'] })]) },
    onError: (error) => setSubmitError(axios.isAxiosError(error) ? error.response?.data?.detail ?? 'Unable to submit request.' : 'Unable to submit request.'),
  })
  const cancelRequest = useMutation({
    mutationFn: async (id: number) => api.post(`/api/v1/leave/requests/${id}/cancel`),
    onSuccess: async () => { await queryClient.invalidateQueries({ queryKey: ['leave-requests-me'] }) },
  })

  return <div className="content">
    <div className="directory-heading"><div><p className="eyebrow">EMPLOYEE SELF-SERVICE</p><h1>Time off</h1><p className="subheading">Check your available leave and manage requests.</p></div><button className="secondary-button" onClick={onBack}><ArrowLeft size={15} /> Overview</button></div>
    <section className="leave-balance-grid" aria-label="Leave balances">
      {balances.isLoading && <p className="directory-message">Loading balances…</p>}
      {balances.data?.length === 0 && <p className="directory-message">No leave balances have been configured for this year.</p>}
      {balances.data?.map((balance) => <article className="leave-balance-card" key={balance.id}><span>{balance.leaveTypeName}</span><strong>{balance.remainingDays}<small> days available</small></strong><div>{balance.usedDays} used of {balance.entitledDays}</div></article>)}
    </section>
    <div className="leave-columns">
      <section className="directory-card leave-request-card"><div className="directory-table-heading"><div><h2>My requests</h2><p>{requests.data ? `${requests.data.totalElements} requests` : 'Your recent requests'}</p></div></div>
        {requests.isLoading && <p className="directory-message">Loading requests…</p>}
        {(requests.isError || balances.isError) && <p className="directory-message error" role="alert">Could not load your leave information. Your account may not be linked to an employee record.</p>}
        {requests.data?.content.length === 0 && <p className="directory-message">You have not submitted a leave request.</p>}
        {requests.data?.content.map((request) => <div className="leave-request-row" key={request.id}><div><strong>{request.leaveTypeName}</strong><span>{new Date(`${request.startDate}T00:00:00`).toLocaleDateString()} – {new Date(`${request.endDate}T00:00:00`).toLocaleDateString()} · {request.requestedDays} days</span><small>{request.reason}</small></div><div className="leave-row-actions"><span className={`status-badge ${request.status.toLowerCase()}`}>{request.status}</span>{request.status === 'PENDING' && <button className="text-button" disabled={cancelRequest.isPending} onClick={() => cancelRequest.mutate(request.id)}>Cancel</button>}</div></div>)}
      </section>
      <section className="directory-card leave-form-card"><div className="directory-table-heading"><div><h2>Request time off</h2><p>Requests are sent to your manager or HR.</p></div></div>
        <form className="employee-form leave-form" onSubmit={(event) => { event.preventDefault(); const form = new FormData(event.currentTarget); submitRequest.mutate({ leaveTypeId: Number(form.get('leaveTypeId')), startDate: String(form.get('startDate')), endDate: String(form.get('endDate')), reason: String(form.get('reason')).trim() }) }}>
          <label>Leave type<select name="leaveTypeId" required defaultValue=""><option value="" disabled>Select leave type</option>{types.data?.map((type) => <option value={type.id} key={type.id}>{type.name}</option>)}</select></label>
          <div className="form-row"><label>From<input name="startDate" type="date" min={new Date().toISOString().slice(0, 10)} required /></label><label>To<input name="endDate" type="date" min={new Date().toISOString().slice(0, 10)} required /></label></div>
          <label>Reason<textarea name="reason" required maxLength={1000} rows={3} /></label>
          {submitError && <p className="login-error" role="alert">{submitError}</p>}
          <button className="search-button" type="submit" disabled={submitRequest.isPending || !types.data?.length}>{submitRequest.isPending ? 'Submitting…' : 'Submit request'}</button>
        </form>
      </section>
    </div>
  </div>
}

interface JobOpening {
  id: number; title: string; vacancies: number; description: string; requiredSkills: string | null
  employmentType: string; openingDate: string; closingDate: string | null; status: 'DRAFT' | 'OPEN' | 'CLOSED'
  departmentName: string | null; designationTitle: string | null; locationName: string | null
}
interface OpeningPage { content: JobOpening[]; totalElements: number }
interface Candidate { id: number; firstName: string; lastName: string; email: string; phone?: string; resumeUrl?: string; source?: string }
interface Application { id: number; openingId: number; openingTitle: string; candidateId: number; candidateName: string; candidateEmail: string; status: string; appliedAt: string }
interface CandidatePage { content: Candidate[]; totalElements: number }
interface ApplicationPage { content: Application[]; totalElements: number }

function RecruitmentWorkspace({ onBack }: { onBack: () => void }) {
  const queryClient = useQueryClient()
  const [formOpen, setFormOpen] = useState(false)
  const [view, setView] = useState<'openings' | 'candidates' | 'pipeline'>('openings')
  const [workflowApplication, setWorkflowApplication] = useState<Application | null>(null)
  const [error, setError] = useState('')
  const openings = useQuery({ queryKey: ['recruitment-openings'], queryFn: async () => (await api.get<OpeningPage>('/api/v1/recruitment/openings', { params: { page: 0, size: 50, sort: 'createdAt,desc' } })).data })
  const candidateList = useQuery({ queryKey: ['recruitment-candidates'], queryFn: async () => (await api.get<CandidatePage>('/api/v1/recruitment/candidates', { params: { page: 0, size: 100, sort: 'lastName,asc' } })).data })
  const applicationList = useQuery({ queryKey: ['recruitment-applications'], queryFn: async () => (await api.get<ApplicationPage>('/api/v1/recruitment/applications', { params: { page: 0, size: 100, sort: 'appliedAt,desc' } })).data })
  const createCandidate = useMutation({
    mutationFn: async (body: { firstName: string; lastName: string; email: string; phone?: string; resumeUrl?: string; source?: string }) => api.post('/api/v1/recruitment/candidates', body),
    onSuccess: async () => { setFormOpen(false); setError(''); await queryClient.invalidateQueries({ queryKey: ['recruitment-candidates'] }) },
    onError: (problem) => setError(axios.isAxiosError(problem) ? problem.response?.data?.detail ?? 'Could not save this candidate.' : 'Could not save this candidate.'),
  })
  const createApplication = useMutation({
    mutationFn: async (body: { openingId: number; candidateId: number }) => api.post('/api/v1/recruitment/applications', body),
    onSuccess: async () => { setFormOpen(false); setError(''); setView('pipeline'); await queryClient.invalidateQueries({ queryKey: ['recruitment-applications'] }) },
    onError: (problem) => setError(axios.isAxiosError(problem) ? problem.response?.data?.detail ?? 'Could not create the application.' : 'Could not create the application.'),
  })
  const updateStage = useMutation({
    mutationFn: async ({ id, status }: { id: number; status: string }) => api.patch(`/api/v1/recruitment/applications/${id}/stage`, { status, note: 'Updated from recruitment workspace' }),
    onSuccess: async () => { setError(''); await queryClient.invalidateQueries({ queryKey: ['recruitment-applications'] }) },
    onError: (problem) => setError(axios.isAxiosError(problem) ? problem.response?.data?.detail ?? 'Could not update this application.' : 'Could not update this application.'),
  })
  const createOpening = useMutation({
    mutationFn: async (body: { title: string; vacancies: number; description: string; requiredSkills: string; employmentType: string; openingDate: string }) => api.post('/api/v1/recruitment/openings', body),
    onSuccess: async () => { setFormOpen(false); setError(''); await queryClient.invalidateQueries({ queryKey: ['recruitment-openings'] }) },
    onError: (problem) => setError(axios.isAxiosError(problem) ? problem.response?.data?.detail ?? 'Could not save this opening.' : 'Could not save this opening.'),
  })
  const updateStatus = useMutation({
    mutationFn: async ({ id, status }: { id: number; status: 'OPEN' | 'CLOSED' }) => api.patch(`/api/v1/recruitment/openings/${id}/status`, { status }),
    onSuccess: async () => { await queryClient.invalidateQueries({ queryKey: ['recruitment-openings'] }) },
    onError: (problem) => setError(axios.isAxiosError(problem) ? problem.response?.data?.detail ?? 'Could not update opening.' : 'Could not update opening.'),
  })
  const today = new Date(Date.now() - new Date().getTimezoneOffset() * 60_000).toISOString().slice(0, 10)

  return <div className="content">
    <div className="directory-heading"><div><p className="eyebrow">TALENT ACQUISITION</p><h1>Recruitment</h1><p className="subheading">Manage openings, candidates, and application stages.</p></div><div className="directory-actions">{view === 'openings' && <button className="search-button" onClick={() => { setError(''); setFormOpen(true) }}>New opening</button>}{view === 'candidates' && <button className="search-button" onClick={() => { setError(''); setFormOpen(true) }}>New candidate</button>}{view === 'pipeline' && <button className="search-button" onClick={() => { setError(''); setFormOpen(true) }}>Create application</button>}<button className="secondary-button" onClick={onBack}><ArrowLeft size={15} /> Overview</button></div></div>
    <nav className="workspace-tabs" aria-label="Recruitment sections">{(['openings', 'candidates', 'pipeline'] as const).map((tab) => <button key={tab} className={view === tab ? 'active' : ''} onClick={() => { setView(tab); setError('') }}>{tab === 'pipeline' ? 'Applications' : tab[0].toUpperCase() + tab.slice(1)}</button>)}</nav>
    {error && !formOpen && <p className="login-error" role="alert">{error}</p>}
    {view === 'openings' && <section className="directory-card"><div className="directory-table-heading"><div><h2>Job openings</h2><p>{openings.data ? `${openings.data.totalElements} positions` : 'Current hiring needs'}</p></div></div>
      {openings.isLoading && <p className="directory-message">Loading openings…</p>}
      {openings.isError && <p className="directory-message error" role="alert">Could not load job openings. Check your access or try again.</p>}
      {openings.data?.content.length === 0 && <p className="directory-message">No openings yet. Create a position to start recruiting.</p>}
      {openings.data?.content.map((opening) => <article className="opening-row" key={opening.id}>
        <div className="opening-main"><div className="opening-title-row"><h3>{opening.title}</h3><span className={`status-badge ${opening.status.toLowerCase()}`}>{opening.status}</span></div>
          <p>{[opening.designationTitle, opening.departmentName, opening.locationName].filter(Boolean).join(' · ') || 'Organization details not assigned'}</p>
          <small>{opening.employmentType.replaceAll('_', ' ')} · {opening.vacancies} {opening.vacancies === 1 ? 'vacancy' : 'vacancies'} · Opened {new Date(`${opening.openingDate}T00:00:00`).toLocaleDateString()}</small>
          {opening.requiredSkills && <small className="opening-skills">Skills: {opening.requiredSkills}</small>}
        </div>
        {opening.status !== 'CLOSED' && <button className={opening.status === 'DRAFT' ? 'search-button' : 'secondary-button'} disabled={updateStatus.isPending}
          onClick={() => updateStatus.mutate({ id: opening.id, status: opening.status === 'DRAFT' ? 'OPEN' : 'CLOSED' })}>{opening.status === 'DRAFT' ? 'Open position' : 'Close position'}</button>}
      </article>)}
    </section>}
    {view === 'candidates' && <section className="directory-card"><div className="directory-table-heading"><div><h2>Candidate records</h2><p>{candidateList.data ? `${candidateList.data.totalElements} candidates` : 'Candidate directory'}</p></div></div>{candidateList.isLoading && <p className="directory-message">Loading candidates…</p>}{candidateList.isError && <p className="directory-message error">Could not load candidates. Check your access or try again.</p>}{candidateList.data?.content.length === 0 && <p className="directory-message">No candidates yet. Add a candidate to begin.</p>}{candidateList.data?.content.map((candidate) => <article className="opening-row" key={candidate.id}><div className="opening-main"><div className="opening-title-row"><h3>{candidate.firstName} {candidate.lastName}</h3></div><p>{candidate.email}{candidate.phone ? ` · ${candidate.phone}` : ''}</p><small>{candidate.source ? `Source: ${candidate.source}` : 'Source not recorded'}{candidate.resumeUrl ? ` · Resume reference available` : ''}</small></div>{candidate.resumeUrl && <a className="secondary-button" href={candidate.resumeUrl} target="_blank" rel="noreferrer">Open resume</a>}</article>)}</section>}
    {view === 'pipeline' && <section className="directory-card"><div className="directory-table-heading"><div><h2>Applications</h2><p>{applicationList.data ? `${applicationList.data.totalElements} applications` : 'Hiring pipeline'}</p></div></div>{applicationList.isLoading && <p className="directory-message">Loading applications…</p>}{applicationList.isError && <p className="directory-message error">Could not load applications. Check your access or try again.</p>}{applicationList.data?.content.length === 0 && <p className="directory-message">No applications yet. Create one from a candidate and open position.</p>}{applicationList.data?.content.map((application) => { const stages = application.status === 'APPLIED' ? ['SCREENING', 'REJECTED', 'WITHDRAWN'] : ['REJECTED', 'WITHDRAWN']; return <article className="opening-row" key={application.id}><div className="opening-main"><div className="opening-title-row"><h3>{application.candidateName}</h3><span className={`status-badge ${application.status.toLowerCase()}`}>{application.status.replaceAll('_', ' ')}</span></div><p>{application.candidateEmail} · {application.openingTitle}</p><small>Applied {new Date(application.appliedAt).toLocaleDateString()}</small></div><div className="review-actions"><button className="secondary-button" onClick={() => setWorkflowApplication(application)}>Interviews & offers</button>{['APPLIED', 'SCREENING', 'INTERVIEW'].includes(application.status) && <select aria-label={`Update stage for ${application.candidateName}`} disabled={updateStage.isPending} value="" onChange={(e) => { if (e.target.value) updateStage.mutate({ id: application.id, status: e.target.value }) }}><option value="">Update stage…</option>{stages.map((stage) => <option key={stage} value={stage}>{stage === 'REJECTED' ? 'Reject' : stage === 'WITHDRAWN' ? 'Withdraw' : stage}</option>)}</select>}</div></article> })}</section>}
    {formOpen && view === 'candidates' && <div className="modal-backdrop" role="presentation" onMouseDown={(event) => { if (event.target === event.currentTarget) setFormOpen(false) }}><section className="employee-modal" role="dialog" aria-modal="true" aria-labelledby="candidate-form-title"><div className="modal-heading"><div><p className="eyebrow">TALENT POOL</p><h2 id="candidate-form-title">Add candidate</h2></div><button className="modal-close" onClick={() => setFormOpen(false)} aria-label="Close">×</button></div><form className="employee-form" onSubmit={(event) => { event.preventDefault(); const f = new FormData(event.currentTarget); const phone = String(f.get('phone')).trim(); const resumeUrl = String(f.get('resumeUrl')).trim(); const source = String(f.get('source')).trim(); createCandidate.mutate({ firstName: String(f.get('firstName')).trim(), lastName: String(f.get('lastName')).trim(), email: String(f.get('email')).trim(), ...(phone ? { phone } : {}), ...(resumeUrl ? { resumeUrl } : {}), ...(source ? { source } : {}) }) }}><div className="form-row"><label>First name<input name="firstName" required maxLength={100} /></label><label>Last name<input name="lastName" required maxLength={100} /></label></div><label>Email<input name="email" type="email" required maxLength={254} /></label><div className="form-row"><label>Phone<input name="phone" maxLength={40} /></label><label>Source<input name="source" maxLength={80} placeholder="Referral, careers page…" /></label></div><label>HTTPS resume reference<input name="resumeUrl" type="url" pattern="https://.*" maxLength={512} /></label>{error && <p className="login-error" role="alert">{error}</p>}<div className="modal-actions"><button type="button" className="secondary-button" onClick={() => setFormOpen(false)}>Cancel</button><button className="search-button" disabled={createCandidate.isPending}>{createCandidate.isPending ? 'Saving…' : 'Add candidate'}</button></div></form></section></div>}
    {formOpen && view === 'pipeline' && <div className="modal-backdrop" role="presentation" onMouseDown={(event) => { if (event.target === event.currentTarget) setFormOpen(false) }}><section className="employee-modal" role="dialog" aria-modal="true" aria-labelledby="application-form-title"><div className="modal-heading"><div><p className="eyebrow">HIRING PIPELINE</p><h2 id="application-form-title">Create application</h2></div><button className="modal-close" onClick={() => setFormOpen(false)} aria-label="Close">×</button></div><form className="employee-form" onSubmit={(event) => { event.preventDefault(); const f = new FormData(event.currentTarget); createApplication.mutate({ openingId: Number(f.get('openingId')), candidateId: Number(f.get('candidateId')) }) }}><label>Open position<select name="openingId" required defaultValue=""><option value="" disabled>Select opening</option>{openings.data?.content.filter((opening) => opening.status === 'OPEN').map((opening) => <option key={opening.id} value={opening.id}>{opening.title}</option>)}</select></label><label>Candidate<select name="candidateId" required defaultValue=""><option value="" disabled>Select candidate</option>{candidateList.data?.content.map((candidate) => <option key={candidate.id} value={candidate.id}>{candidate.firstName} {candidate.lastName} · {candidate.email}</option>)}</select></label>{error && <p className="login-error" role="alert">{error}</p>}<div className="modal-actions"><button type="button" className="secondary-button" onClick={() => setFormOpen(false)}>Cancel</button><button className="search-button" disabled={createApplication.isPending || !openings.data?.content.some((opening) => opening.status === 'OPEN') || !candidateList.data?.content.length}>{createApplication.isPending ? 'Creating…' : 'Create application'}</button></div></form></section></div>}
    {formOpen && view === 'openings' && <div className="modal-backdrop" role="presentation" onMouseDown={(event) => { if (event.target === event.currentTarget) setFormOpen(false) }}><section className="employee-modal" role="dialog" aria-modal="true" aria-labelledby="opening-form-title">
      <div className="modal-heading"><div><p className="eyebrow">HIRING NEED</p><h2 id="opening-form-title">Create opening</h2></div><button className="modal-close" onClick={() => setFormOpen(false)} aria-label="Close">×</button></div>
      <form className="employee-form" onSubmit={(event) => { event.preventDefault(); const values = new FormData(event.currentTarget); createOpening.mutate({ title: String(values.get('title')).trim(), vacancies: Number(values.get('vacancies')), description: String(values.get('description')).trim(), requiredSkills: String(values.get('requiredSkills')).trim(), employmentType: String(values.get('employmentType')), openingDate: String(values.get('openingDate')) }) }}>
        <label>Job title<input name="title" required maxLength={160} placeholder="e.g. Product Designer" /></label>
        <div className="form-row"><label>Vacancies<input name="vacancies" type="number" min="1" step="1" defaultValue="1" required /></label><label>Employment type<select name="employmentType" defaultValue="FULL_TIME"><option value="FULL_TIME">Full time</option><option value="PART_TIME">Part time</option><option value="CONTRACT">Contract</option><option value="INTERN">Intern</option><option value="TEMPORARY">Temporary</option></select></label></div>
        <label>Job description<textarea name="description" required rows={4} /></label><label>Required skills<input name="requiredSkills" maxLength={1000} placeholder="Optional, comma separated" /></label>
        <label>Opening date<input name="openingDate" type="date" defaultValue={today} required /></label>
        {error && <p className="login-error" role="alert">{error}</p>}<div className="modal-actions"><button type="button" className="secondary-button" onClick={() => setFormOpen(false)}>Cancel</button><button type="submit" className="search-button" disabled={createOpening.isPending}>{createOpening.isPending ? 'Saving…' : 'Create draft'}</button></div>
      </form>
    </section></div>}
    {workflowApplication && <RecruitmentWorkflow application={workflowApplication} onClose={() => setWorkflowApplication(null)} />}
  </div>
}

export default App
