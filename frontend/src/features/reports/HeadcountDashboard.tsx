import { useState } from 'react'
import { useQuery } from '@tanstack/react-query'
import { api } from '../../api/client'
type Headcount={totalEmployees:number;activeEmployees:number;inactiveEmployees:number;newJoiners:number;from:string;to:string;departments:{department:string;employees:number}[]}
export function HeadcountDashboard({roles}:{roles:string[]}){
 const enabled=roles.some((r)=>['SUPER_ADMIN','ADMIN','HR_MANAGER','HR_EXECUTIVE'].includes(r)),today=new Date(),todayText=`${today.getFullYear()}-${String(today.getMonth()+1).padStart(2,'0')}-${String(today.getDate()).padStart(2,'0')}`
 const [from,setFrom]=useState(`${today.getFullYear()}-01-01`),[to,setTo]=useState(todayText)
 const report=useQuery({queryKey:['reports-headcount',from,to],enabled,queryFn:async()=> (await api.get<Headcount>('/api/v1/reports/headcount',{params:{from,to}})).data})
 if(!enabled)return null
 const max=Math.max(1,...(report.data?.departments.map((d)=>d.employees)??[]))
 return <section className="headcount-dashboard"><div className="section-heading"><div><h2>People analytics</h2><p>Live employee headcount and joiners for the selected period.</p></div><div className="date-filter"><label>From<input type="date" value={from} max={to} onChange={(e)=>setFrom(e.target.value)}/></label><label>To<input type="date" value={to} min={from} max={todayText} onChange={(e)=>setTo(e.target.value)}/></label></div></div>
 {report.isError?<p className="login-error" role="alert">Could not load headcount report.</p>:<><div className="headcount-stats">{[['Total employees',report.data?.totalEmployees],['Active',report.data?.activeEmployees],['Separated',report.data?.inactiveEmployees],['New joiners',report.data?.newJoiners]].map(([label,value])=><article className="headcount-stat" key={String(label)}><small>{label}</small><strong>{report.isLoading?'—':value}</strong></article>)}</div>
 <div className="headcount-departments"><h3>Employees by department</h3>{report.data?.departments.length?report.data.departments.map((d)=><div className="department-bar-row" key={d.department}><span>{d.department}</span><div className="department-bar-track"><i style={{width:`${Math.max(4,Math.round((d.employees/max)*100))}%`}}/></div><b>{d.employees}</b></div>):<p className="empty-state">No employee records yet.</p>}</div></>}
 </section>
}
