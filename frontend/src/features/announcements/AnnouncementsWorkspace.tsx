import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import axios from 'axios'
import { useState } from 'react'
import { ArrowLeft, Megaphone } from 'lucide-react'
import { api } from '../../api/client'

type Page<T> = { content: T[]; totalElements: number }
type Announcement = { id: number; title: string; body: string; departmentId?: number; departmentName: string; priority: 'LOW'|'NORMAL'|'HIGH'; status: 'DRAFT'|'PUBLISHED'; publishedAt?: string; expiresAt?: string }
type Department = { id: number; name: string }
export function AnnouncementsWorkspace({ roles, onBack }: { roles: string[]; onBack: () => void }) {
  const canManage=roles.some((r)=>['SUPER_ADMIN','ADMIN','HR_MANAGER'].includes(r)), qc=useQueryClient()
  const [error,setError]=useState('')
  const list=useQuery({queryKey:['announcements',canManage],queryFn:async()=> (await api.get<Page<Announcement>>(canManage?'/api/v1/announcements':'/api/v1/announcements/me',{params:{page:0,size:100}})).data.content})
  const departments=useQuery({queryKey:['departments-announcements'],enabled:canManage,queryFn:async()=> (await api.get<Page<Department>>('/api/v1/departments',{params:{page:0,size:100,active:true}})).data.content})
  const fail=(e:unknown)=>setError(axios.isAxiosError(e)?e.response?.data?.detail??'Could not save announcement.':'Could not save announcement.')
  const refresh=async()=>{setError('');await Promise.all([qc.invalidateQueries({queryKey:['announcements']}),qc.invalidateQueries({queryKey:['announcements',canManage]})])}
  const create=useMutation({mutationFn:async(body:unknown)=>api.post('/api/v1/announcements',body),onSuccess:refresh,onError:fail})
  const publish=useMutation({mutationFn:async(id:number)=>api.post(`/api/v1/announcements/${id}/publish`),onSuccess:refresh,onError:fail})
  return <div className="content"><div className="directory-heading"><div><p className="eyebrow">COMPANY UPDATES</p><h1>Announcements</h1><p className="subheading">Important updates for employees and teams.</p></div><button className="secondary-button" onClick={onBack}><ArrowLeft size={15}/> Overview</button></div>
    {error&&<p className="login-error" role="alert">{error}</p>}
    {canManage&&<section className="directory-card"><div className="directory-table-heading"><div><h2>Create announcement</h2><p>Drafts stay private until published. Optionally target one department.</p></div></div><form className="leave-admin-form" onSubmit={(e)=>{e.preventDefault();const f=new FormData(e.currentTarget);const expiry=String(f.get('expiresAt'));create.mutate({title:f.get('title'),body:f.get('body'),priority:f.get('priority'),departmentId:f.get('departmentId')?Number(f.get('departmentId')):null,expiresAt:expiry?new Date(expiry).toISOString():null});e.currentTarget.reset()}}>
      <label>Title<input name="title" maxLength={180} required/></label><label>Message<textarea name="body" maxLength={5000} required/></label><div className="form-row"><label>Audience<select name="departmentId" defaultValue=""><option value="">All employees</option>{departments.data?.map((d)=><option value={d.id} key={d.id}>{d.name}</option>)}</select></label><label>Priority<select name="priority" defaultValue="NORMAL"><option>LOW</option><option>NORMAL</option><option>HIGH</option></select></label><label>Expires at<input name="expiresAt" type="datetime-local"/></label></div><button className="search-button" disabled={create.isPending}>{create.isPending?'Saving…':'Save draft'}</button></form></section>}
    <section className="directory-card"><div className="directory-table-heading"><div><h2>{canManage?'Announcement register':'For you'}</h2><p>{canManage?'Publish drafts or review published notices.':'Published notices for your organization and department.'}</p></div></div>
      {list.data?.length?list.data.map((item)=><article className="onboarding-row announcement-row" key={item.id}><div className="announcement-icon"><Megaphone size={18}/></div><div className="announcement-copy"><div className="announcement-title"><h3>{item.title}</h3><span className={`announcement-priority ${item.priority.toLowerCase()}`}>{item.priority}</span></div><p>{item.body}</p><small>{item.departmentName} · {item.status}{item.expiresAt?` · expires ${new Date(item.expiresAt).toLocaleDateString()}`:''}</small></div>{canManage&&item.status==='DRAFT'&&<button className="search-button" onClick={()=>publish.mutate(item.id)} disabled={publish.isPending}>Publish</button>}</article>):<p className="empty-state">No announcements to show.</p>}
    </section>
  </div>
}
