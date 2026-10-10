import React,{createContext,useContext,useState,useEffect,Component} from 'react';
import {Link,NavLink,Navigate,useNavigate,useParams} from 'react-router-dom';
import {Home,ArrowUpRight,ArrowRight,Search,MapPin,BedDouble,Bath,Maximize,SlidersHorizontal,Plus,MessageCircle,X,Send,ShieldCheck,Leaf,Menu,Heart} from 'lucide-react';
import ReactMarkdown from 'react-markdown';
import {api,setToken,errorText,money} from '../api';
import {Auth,Loading,Alert,Field,PropertyCard} from '../ui';
function Dashboard(){
 const {user}=useContext(Auth);const [properties,setProperties]=useState([]),[bookings,setBookings]=useState([]),[queue,setQueue]=useState(null),[users,setUsers]=useState(null),[page,setPage]=useState(0),[userPage,setUserPage]=useState(0),[error,setError]=useState(''),[loading,setLoading]=useState(true),[busy,setBusy]=useState(false),[deleting,setDeleting]=useState(null);
 async function load(){
  setError('');
  const jobs=[api.get('/dashboard/properties'),api.get('/bookings')];
  const labels=['Your properties','Rental requests'];
  if(user.role==='ADMIN'){
   jobs.push(api.get('/admin/properties',{params:{page}}),api.get('/admin/users',{params:{page:userPage}}));
   labels.push('Listing review','Registered users');
  }
  const results=await Promise.allSettled(jobs);
  const setters=[setProperties,setBookings,setQueue,setUsers];
  results.forEach((result,index)=>{if(result.status==='fulfilled')setters[index](result.value.data);});
  setError(results.flatMap((result,index)=>result.status==='rejected'?[labels[index]+': '+errorText(result.reason)]:[]).join(' · '));
  setLoading(false);
 }
 useEffect(()=>{load();},[page,userPage]);
 async function act(job){setBusy(true);setError('');try{await job();setDeleting(null);await load();}catch(e){setError(errorText(e));}finally{setBusy(false);}}
 return <main className="page"><div className="section-heading"><div><span className="eyebrow">YOUR HOUSEHUNT</span><h1>Welcome home, {user.name.split(' ')[0]}.</h1><p>Your listings, requests, and next steps — all in one place.</p></div><Link className="button" to="/list"><Plus size={17}/> New listing</Link></div><Alert message={error}/>{loading?<Loading/>:<><div className="stats"><div><strong>{properties.length}</strong><span>Your properties</span></div><div><strong>{bookings.filter(b=>b.status==='REQUESTED').length}</strong><span>Pending requests</span></div><div><strong>{bookings.filter(b=>b.status==='ACCEPTED').length}</strong><span>Confirmed rentals</span></div></div>
 <h2>Your properties</h2><div className="table-responsive"><table className="table"><thead><tr><th>Property</th><th>Rent / month</th><th>Status</th><th>Actions</th></tr></thead><tbody>{properties.map(p=><tr key={p.id}><td><Link to={'/property/'+p.id}>{p.title}</Link><small>{p.city}</small></td><td>{money(p.price)}</td><td><span className={'status '+p.status}>{p.status}</span></td><td><Link className="text-button" to={'/edit/'+p.id}>Edit</Link><button className="text-button danger" onClick={()=>setDeleting(p)}>Delete</button></td></tr>)}</tbody></table>{!properties.length&&<p className="empty">Your first listing could be someone’s next home.</p>}</div>
 <h2 className="mt-5">Rental requests</h2><div className="booking-grid">{bookings.map(b=><div className="booking-card" key={b.id}><span className={'status '+b.status}>{b.status}</span><h3>{b.propertyTitle}</h3><p>{b.startDate} → {b.endDate}</p><p>Requested by {b.tenantName}</p><p>{b.message}</p><div className="action-row">{b.ownerId===user.id&&b.status==='REQUESTED'&&<><button disabled={busy} className="button small" onClick={()=>act(()=>api.patch('/bookings/'+b.id,{status:'ACCEPTED'}))}>Accept</button><button disabled={busy} className="outline-button" onClick={()=>act(()=>api.patch('/bookings/'+b.id,{status:'DECLINED'}))}>Decline</button></>}{b.tenantId===user.id&&['REQUESTED','ACCEPTED'].includes(b.status)&&<button disabled={busy} className="outline-button" onClick={()=>act(()=>api.patch('/bookings/'+b.id,{status:'CANCELLED'}))}>Cancel request</button>}</div></div>)}</div>{!bookings.length&&<p className="empty">No rental requests yet. Find a home to get started.</p>}
 {user.role==='ADMIN'&&<><h2 className="mt-5">Admin · Review listings</h2>{queue?.content.map(p=><div className="review-row" key={p.id}><div><Link to={'/property/'+p.id}><strong>{p.title}</strong></Link><p>{p.ownerName} · {p.city} · {money(p.price)}</p></div><div className="action-row"><button disabled={busy} className="button small" onClick={()=>act(()=>api.patch('/admin/properties/'+p.id,{status:'APPROVED'}))}>Approve</button><button disabled={busy} className="outline-button" onClick={()=>act(()=>api.patch('/admin/properties/'+p.id,{status:'REJECTED'}))}>Reject</button></div></div>)}{!queue?.content.length&&<p className="empty">All caught up. No pending listings.</p>}<Pager page={page} data={queue} setPage={setPage}/><h2 className="mt-5">Registered users</h2><div className="table-responsive"><table className="table"><thead><tr><th>Name</th><th>Email</th><th>Role</th></tr></thead><tbody>{users?.content.map(u=><tr key={u.id}><td>{u.name}</td><td>{u.email}</td><td>{u.role}</td></tr>)}</tbody></table></div><Pager page={userPage} data={users} setPage={setUserPage}/></>}</>}
 {deleting&&<div className="modal-backdrop-custom"><section className="form-panel" role="dialog" aria-modal="true" aria-labelledby="delete-title"><h3 id="delete-title">Delete {deleting.title}?</h3><p>This removes the listing. Properties with booking history are kept for records.</p><Alert message={error}/><div className="action-row"><button disabled={busy} className="button" onClick={()=>act(()=>api.delete('/properties/'+deleting.id))}>Delete listing</button><button className="outline-button" onClick={()=>setDeleting(null)}>Keep listing</button></div></section></div>}
 </main>;
}
function Pager({page,data,setPage}){return data?.totalPages>1?<div className="pagination-controls"><button disabled={!page} onClick={()=>setPage(page-1)}>Previous</button><span>{page+1} / {data.totalPages}</span><button disabled={page+1>=data.totalPages} onClick={()=>setPage(page+1)}>Next</button></div>:null;}

export default Dashboard;