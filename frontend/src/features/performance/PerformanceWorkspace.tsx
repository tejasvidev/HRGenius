import { useState } from 'react'
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import axios from 'axios'
import { ArrowLeft } from 'lucide-react'
import { api } from '../../api/client'

type Page<T> = { content: T[]; totalElements: number }
type Employee = { id: number; employeeNumber: string; firstName: string; lastName: string }
type Review = { id: number; cycleName: string; employeeId: number; employeeName: string; reviewerName: string; status: 'SELF_REVIEW' | 'MANAGER_REVIEW' | 'COMPLETED'; selfSummary?: string; selfRating?: number; managerFeedback?: string; managerRating?: number }

export function PerformanceWorkspace({ roles, onBack }: { roles: string[]; onBack: () => void }) {
  const isHr = roles.some((r) => ['SUPER_ADMIN', 'ADMIN', 'HR_MANAGER'].includes(r))
  const isManager = roles.includes('MANAGER')
  const qc = useQueryClient()
  const [error, setError] = useState('')
  const employees = useQuery({ queryKey: ['performance-employees'], enabled: isHr, queryFn: async () => (await api.get<Page<Employee>>('/api/v1/employees', { params: { page: 0, size: 100, status: 'ACTIVE', sort: 'lastName,asc' } })).data.content })
  const mine = useQuery({ queryKey: ['performance-me'], queryFn: async () => (await api.get<Page<Review>>('/api/v1/performance/me', { params: { page: 0, size: 50 } })).data.content })
  const team = useQuery({ queryKey: ['performance-team'], enabled: isManager, queryFn: async () => (await api.get<Page<Review>>('/api/v1/performance/team', { params: { page: 0, size: 100 } })).data.content })
  const all = useQuery({ queryKey: ['performance-all'], enabled: isHr, queryFn: async () => (await api.get<Page<Review>>('/api/v1/performance', { params: { page: 0, size: 100 } })).data.content })
  const fail = (problem: unknown) => setError(axios.isAxiosError(problem) ? problem.response?.data?.detail ?? 'Could not complete review action.' : 'Could not complete review action.')
  const refresh = async () => { setError(''); await Promise.all([qc.invalidateQueries({ queryKey: ['performance-me'] }), qc.invalidateQueries({ queryKey: ['performance-team'] }), qc.invalidateQueries({ queryKey: ['performance-all'] })]) }
  const create = useMutation({ mutationFn: async (body: { cycleName: string; employeeId: number }) => api.post('/api/v1/performance', body), onSuccess: refresh, onError: fail })
  const self = useMutation({ mutationFn: async ({ id, summary, rating }: { id: number; summary: string; rating: number }) => api.patch(`/api/v1/performance/${id}/self`, { summary, rating }), onSuccess: refresh, onError: fail })
  const manager = useMutation({ mutationFn: async ({ id, feedback, rating }: { id: number; feedback: string; rating: number }) => api.patch(`/api/v1/performance/${id}/manager`, { feedback, rating }), onSuccess: refresh, onError: fail })
  const card = (review: Review, mode: 'self' | 'manager' | 'read') => <article className="onboarding-row" key={`${mode}-${review.id}`}><div className="performance-card-head"><div><h3>{review.employeeName} · {review.cycleName}</h3><p>{review.status.replaceAll('_', ' ')}{mode === 'manager' ? ` · reviewer ${review.reviewerName}` : ''}</p></div>{review.status === 'COMPLETED' && <strong>{review.managerRating}/5</strong>}</div>
    {review.selfSummary && <p><b>Self review ({review.selfRating}/5):</b> {review.selfSummary}</p>}{review.managerFeedback && <p><b>Manager feedback ({review.managerRating}/5):</b> {review.managerFeedback}</p>}
    {mode === 'self' && review.status === 'SELF_REVIEW' && <form className="leave-admin-form" onSubmit={(e) => { e.preventDefault(); const f = new FormData(e.currentTarget); self.mutate({ id: review.id, summary: String(f.get('summary')), rating: Number(f.get('rating')) }) }}><label>Summary<textarea name="summary" maxLength={3000} required /></label><label>Self rating<select name="rating" defaultValue="4">{[1,2,3,4,5].map((n) => <option key={n} value={n}>{n} / 5</option>)}</select></label><button className="search-button" disabled={self.isPending}>Submit self-review</button></form>}
    {mode === 'manager' && review.status === 'MANAGER_REVIEW' && <form className="leave-admin-form" onSubmit={(e) => { e.preventDefault(); const f = new FormData(e.currentTarget); manager.mutate({ id: review.id, feedback: String(f.get('feedback')), rating: Number(f.get('rating')) }) }}><label>Feedback<textarea name="feedback" maxLength={3000} required /></label><label>Manager rating<select name="rating" defaultValue="4">{[1,2,3,4,5].map((n) => <option key={n} value={n}>{n} / 5</option>)}</select></label><button className="search-button" disabled={manager.isPending}>Complete review</button></form>}</article>
  return <div className="content"><div className="directory-heading"><div><p className="eyebrow">GROWTH &amp; DEVELOPMENT</p><h1>Performance reviews</h1><p className="subheading">Self-reflection followed by direct-manager feedback.</p></div><button className="secondary-button" onClick={onBack}><ArrowLeft size={15} /> Overview</button></div>
    {error && <p className="login-error" role="alert">{error}</p>}
    {isHr && <section className="directory-card"><div className="directory-table-heading"><div><h2>Start a review</h2><p>Choose an active employee who has a direct manager.</p></div></div><form className="leave-admin-form" onSubmit={(e) => { e.preventDefault(); const f = new FormData(e.currentTarget); create.mutate({ cycleName: String(f.get('cycleName')), employeeId: Number(f.get('employeeId')) }); e.currentTarget.reset() }}><label>Cycle name<input name="cycleName" placeholder="FY 2026" maxLength={120} required /></label><label>Employee<select name="employeeId" defaultValue="" required><option value="" disabled>Select employee</option>{employees.data?.map((employee) => <option key={employee.id} value={employee.id}>{employee.firstName} {employee.lastName} · {employee.employeeNumber}</option>)}</select></label><button className="search-button" disabled={create.isPending || !employees.data?.length}>Create review</button></form></section>}
    <section className="directory-card"><div className="directory-table-heading"><div><h2>My reviews</h2><p>Your self-review tasks and completed feedback.</p></div></div>{mine.data?.length ? mine.data.map((r) => card(r,'self')) : <p className="empty-state">No reviews assigned to you yet.</p>}</section>
    {isManager && <section className="directory-card"><div className="directory-table-heading"><div><h2>Team reviews</h2><p>Reviews assigned to you as a direct manager.</p></div></div>{team.data?.length ? team.data.map((r) => card(r,'manager')) : <p className="empty-state">No team reviews to complete.</p>}</section>}
    {isHr && <section className="directory-card"><div className="directory-table-heading"><div><h2>Review register</h2><p>HR view of the review cycle status.</p></div></div>{all.data?.length ? all.data.map((r) => card(r,'read')) : <p className="empty-state">No reviews have been started.</p>}</section>}
  </div>
}
