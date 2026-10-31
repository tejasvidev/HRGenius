import { useMemo, useState } from 'react'
import axios from 'axios'
import { Download, FileSpreadsheet, Printer } from 'lucide-react'
import { api } from '../../api/client'
import { useQuery } from '@tanstack/react-query'
type Page<T>={content:T[]}
type Department={id:number;name:string}
type Employee={id:number;employeeNumber:string;firstName:string;lastName:string}
type Dataset={report:string;columns:string[];rows:unknown[][];page:number;pageSize:number;totalElements:number;totalPages:number}
type ReportOption={id:string;label:string;sort:Record<string,string>}
const reports:ReportOption[]=[
 {id:'headcount',label:'Headcount',sort:{Metric:'id'}},
 {id:'employee-directory',label:'Employee directory',sort:{'Last name':'lastName','Employee number':'employeeNumber',Status:'status','Date joined':'dateOfJoining'}},
 {id:'department-wise',label:'Department-wise',sort:{Department:'department',Employees:'count'}},{id:'designation-wise',label:'Designation-wise',sort:{Designation:'designation',Employees:'count'}},
 {id:'attendance',label:'Attendance',sort:{'Work date':'workDate',Status:'status'}},{id:'late-arrivals',label:'Late arrivals',sort:{'Work date':'workDate','Late minutes':'lateMinutes'}},
 {id:'absences',label:'Absences',sort:{'Work date':'workDate'}},{id:'overtime',label:'Overtime',sort:{'Work date':'workDate','Overtime minutes':'overtimeMinutes'}},
 {id:'leave',label:'Leave requests',sort:{'Start date':'startDate','End date':'endDate',Status:'status'}},{id:'leave-balances',label:'Leave balances',sort:{Year:'calendarYear','Entitled days':'entitledDays','Used days':'usedDays'}},
 {id:'recruitment',label:'Recruitment',sort:{'Applied at':'appliedAt',Stage:'status'}},{id:'hiring-funnel',label:'Hiring funnel',sort:{Stage:'stage',Applications:'count'}},
 {id:'onboarding',label:'Onboarding',sort:{'Start date':'startDate',Status:'status'}},{id:'payroll',label:'Payroll',sort:{Net:'netPay',Gross:'grossPay'}},{id:'performance',label:'Performance',sort:{Status:'status','Self rating':'selfRating','Manager rating':'managerRating'}}
]
export function ReportsWorkspace({roles}:{roles:string[]}){
 const isRecruiter=roles.includes('RECRUITER'),isHr=roles.some((r)=>['SUPER_ADMIN','ADMIN','HR_MANAGER','HR_EXECUTIVE'].includes(r)),canPayroll=roles.some((r)=>['SUPER_ADMIN','ADMIN','HR_MANAGER'].includes(r)),canPerformance=canPayroll,canFilter=canPayroll
 const available=useMemo(()=>reports.filter(r=>(isRecruiter&&!isHr?['recruitment','hiring-funnel'].includes(r.id):(!['payroll'].includes(r.id)||canPayroll)&&(!['performance'].includes(r.id)||canPerformance))),[isRecruiter,isHr,canPayroll,canPerformance])
 const [selected,setSelected]=useState(available[0]?.id??'recruitment'),now=new Date(),today=`${now.getFullYear()}-${String(now.getMonth()+1).padStart(2,'0')}-${String(now.getDate()).padStart(2,'0')}`
 const [from,setFrom]=useState(`${now.getFullYear()}-01-01`),[to,setTo]=useState(today),[departmentId,setDepartmentId]=useState(''),[employeeId,setEmployeeId]=useState(''),[page,setPage]=useState(0),[sortField,setSortField]=useState(''),[direction,setDirection]=useState<'asc'|'desc'>('asc'),[downloadError,setDownloadError]=useState('')
 const meta=available.find(r=>r.id===selected)??available[0]
 const departments=useQuery({queryKey:['reports-departments'],enabled:canFilter,queryFn:async()=> (await api.get<Page<Department>>('/api/v1/departments',{params:{page:0,size:100,active:true}})).data.content})
 const employees=useQuery({queryKey:['reports-employees'],enabled:canFilter,queryFn:async()=> (await api.get<Page<Employee>>('/api/v1/employees',{params:{page:0,size:100,sort:'lastName,asc'}})).data.content})
 const selectedSort=meta?.sort[sortField]??Object.values(meta?.sort??{})[0]??''
 const params={from,to,page,size:25,...(departmentId?{departmentId}:{}),...(employeeId?{employeeId}:{}),...(selectedSort?{sort:`${selectedSort},${direction}`}:{})}
 const data=useQuery({queryKey:['reports-data',selected,params],enabled:!!meta,queryFn:async()=>{const path=selected==='headcount'?'/api/v1/reports/headcount/data':`/api/v1/reports/${selected}`;return(await api.get<Dataset>(path,{params})).data}})
 const download=async(format:'csv'|'xlsx')=>{try{setDownloadError('');const path=`/api/v1/reports/${selected}/export`;const result=await api.get<Blob>(path,{params:{from,to,...(departmentId?{departmentId}:{}),...(employeeId?{employeeId}:{}),format},responseType:'blob'});const blob=new Blob([result.data],{type:format==='csv'?'text/csv;charset=utf-8':'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet'});const url=URL.createObjectURL(blob);const a=document.createElement('a');a.href=url;a.download=`hrgenius-${selected}.${format}`;a.click();URL.revokeObjectURL(url);if(result.headers['x-export-truncated']==='true')setDownloadError('The export was capped at 20,000 rows. Narrow the filters for a complete export.')}catch(e){setDownloadError(axios.isAxiosError(e)?e.response?.data?.detail??'Export failed.':'Export failed.')}}
 const choose=(id:string)=>{setSelected(id);setPage(0);setSortField('');setDownloadError('')}
 if(!meta)return <div className="content"><p className="empty-state">No reports are available for your role.</p></div>
 return <div className="content reports-workspace"><div className="directory-heading"><div><p className="eyebrow">PEOPLE ANALYTICS</p><h1>Reports</h1><p className="subheading">Filter, sort, page, and export HR reports.</p></div></div>
  <section className="directory-card report-controls"><div className="report-filters"><label>Report<select value={selected} onChange={e=>choose(e.target.value)}>{available.map(r=><option key={r.id} value={r.id}>{r.label}</option>)}</select></label><label>From<input type="date" value={from} max={to} onChange={e=>{setFrom(e.target.value);setPage(0)}}/></label><label>To<input type="date" value={to} min={from} max={today} onChange={e=>{setTo(e.target.value);setPage(0)}}/></label>
   {canFilter&&<label>Department<select value={departmentId} onChange={e=>{setDepartmentId(e.target.value);setPage(0)}}><option value="">All departments</option>{departments.data?.map(d=><option key={d.id} value={d.id}>{d.name}</option>)}</select></label>}{canFilter&&<label>Employee<select value={employeeId} onChange={e=>{setEmployeeId(e.target.value);setPage(0)}}><option value="">All employees</option>{employees.data?.map(p=><option key={p.id} value={p.id}>{p.firstName} {p.lastName} · {p.employeeNumber}</option>)}</select></label>}
   {Object.keys(meta.sort).length>0&&<label>Sort by<select value={sortField} onChange={e=>{setSortField(e.target.value);setPage(0)}}><option value="">Default</option>{Object.keys(meta.sort).map(k=><option key={k}>{k}</option>)}</select></label>}{Object.keys(meta.sort).length>0&&<label>Order<select value={direction} onChange={e=>{setDirection(e.target.value as 'asc'|'desc');setPage(0)}}><option value="asc">Ascending</option><option value="desc">Descending</option></select></label>}
   <div className="report-export-buttons"><button className="secondary-button" onClick={()=>download('csv')}><Download size={15}/> CSV</button><button className="secondary-button" onClick={()=>download('xlsx')}><FileSpreadsheet size={15}/> Excel</button><button className="secondary-button" onClick={()=>window.print()}><Printer size={15}/> PDF / Print</button></div>
  </div></section>
  {downloadError&&<p className="login-error" role="alert">{downloadError}</p>}{data.isError&&<p className="login-error" role="alert">Could not load this report. Check the date range and your report permissions.</p>}
  <section className="directory-card report-result"><div className="directory-table-heading"><div><h2>{meta.label}</h2><p>{data.data?`${data.data.totalElements.toLocaleString()} records`:'Loading report…'}</p></div></div>
   <div className="report-table-wrap"><table className="data-table report-table"><thead><tr>{data.data?.columns.map(c=><th key={c}>{c}</th>)}</tr></thead><tbody>{data.data?.rows.map((row,i)=><tr key={i}>{row.map((cell,j)=><td key={j}>{cell===null||cell===undefined?'—':String(cell)}</td>)}</tr>)}</tbody></table>{!data.isLoading&&!data.isError&&!data.data?.rows.length&&<p className="empty-state">No data matches these filters.</p>}</div>
   <div className="report-pagination"><span>Page {data.data?data.data.page+1:page+1} of {Math.max(data.data?.totalPages??1,1)}</span><div><button className="secondary-button" onClick={()=>setPage(v=>Math.max(v-1,0))} disabled={page===0}>Previous</button><button className="secondary-button" onClick={()=>setPage(v=>v+1)} disabled={!data.data||page+1>=data.data.totalPages}>Next</button></div></div>
  </section>
 </div>
}
