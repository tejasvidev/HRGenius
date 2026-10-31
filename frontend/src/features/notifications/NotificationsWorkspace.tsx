import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import axios from 'axios'
import { useState } from 'react'
import { ArrowLeft, CheckCheck, Inbox } from 'lucide-react'
import { api } from '../../api/client'
type Notification={id:number;title:string;body:string;type:string;createdAt:string;readAt?:string}
type Page<T>={content:T[];totalElements:number}
export function NotificationsWorkspace({onBack}:{onBack:()=>void}){
 const qc=useQueryClient(),[error,setError]=useState('')
 const list=useQuery({queryKey:['notifications-me'],queryFn:async()=> (await api.get<Page<Notification>>('/api/v1/notifications/me',{params:{page:0,size:100}})).data.content})
 const count=useQuery({queryKey:['notifications-unread'],queryFn:async()=> (await api.get<{count:number}>('/api/v1/notifications/me/unread-count')).data.count})
 const fail=(e:unknown)=>setError(axios.isAxiosError(e)?e.response?.data?.detail??'Could not update notifications.':'Could not update notifications.')
 const refresh=async()=>{setError('');await Promise.all([qc.invalidateQueries({queryKey:['notifications-me']}),qc.invalidateQueries({queryKey:['notifications-unread']})])}
 const mark=useMutation({mutationFn:async(id:number)=>api.patch(`/api/v1/notifications/me/${id}/read`),onSuccess:refresh,onError:fail})
 const markAll=useMutation({mutationFn:async()=>api.post('/api/v1/notifications/me/read-all'),onSuccess:refresh,onError:fail})
 return <div className="content"><div className="directory-heading"><div><p className="eyebrow">YOUR INBOX</p><h1>Notifications</h1><p className="subheading">{count.data??0} unread notification{count.data===1?'':'s'}.</p></div><div className="notification-actions"><button className="secondary-button" onClick={()=>markAll.mutate()} disabled={!count.data||markAll.isPending}><CheckCheck size={15}/> Mark all read</button><button className="secondary-button" onClick={onBack}><ArrowLeft size={15}/> Overview</button></div></div>
 {error&&<p className="login-error" role="alert">{error}</p>}
 <section className="directory-card"><div className="directory-table-heading"><div><h2>Recent notifications</h2><p>New notices appear here when an announcement is published for you.</p></div></div>
 {list.data?.length?list.data.map((n)=><article className={`onboarding-row notification-row ${n.readAt?'read':''}`} key={n.id}><div className="announcement-icon"><Inbox size={18}/></div><div className="announcement-copy"><div className="announcement-title"><h3>{n.title}</h3>{!n.readAt&&<span className="notification-unread">NEW</span>}</div><p>{n.body}</p><small>{new Date(n.createdAt).toLocaleString()}</small></div>{!n.readAt&&<button className="secondary-button" onClick={()=>mark.mutate(n.id)} disabled={mark.isPending}>Mark read</button>}</article>):<p className="empty-state">You’re all caught up.</p>}
 </section></div>
}
