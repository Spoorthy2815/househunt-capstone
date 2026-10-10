import React,{createContext,useContext,useState,useEffect,Component} from 'react';
import {Link,NavLink,Navigate,useNavigate,useParams} from 'react-router-dom';
import {Home,ArrowUpRight,ArrowRight,Search,MapPin,BedDouble,Bath,Maximize,SlidersHorizontal,Plus,MessageCircle,X,Send,ShieldCheck,Leaf,Menu,Heart} from 'lucide-react';
import ReactMarkdown from 'react-markdown';
import {api,setToken,errorText,money} from './api';

export const Auth=createContext(null);
export function AuthProvider({children}){
 const [user,setUser]=useState(null),[loading,setLoading]=useState(true),[favorites,setFavorites]=useState([]);
 useEffect(()=>{api.get('/auth/me').then(r=>setUser(r.data)).catch(()=>setToken(null)).finally(()=>setLoading(false));const logout=()=>setUser(null);window.addEventListener('househunt-logout',logout);return()=>window.removeEventListener('househunt-logout',logout);},[]);
 useEffect(()=>{setFavorites([]);if(user)api.get('/account/favorites').then(r=>setFavorites(r.data.map(p=>p.id))).catch(()=>{});},[user]);
 async function toggleFavorite(id){if(!user)throw new Error('Please log in to save homes.');if(favorites.includes(id)){await api.delete('/account/favorites/'+id);setFavorites(ids=>ids.filter(x=>x!==id));}else{await api.put('/account/favorites/'+id);setFavorites(ids=>[...ids,id]);}}
 const login=data=>{setToken(data.token);setUser(data.user);};
 const logout=()=>{setToken(null);setUser(null);};
 return <Auth.Provider value={{user,loading,login,logout,favorites,toggleFavorite}}>{children}</Auth.Provider>;
}
export class ErrorBoundary extends Component{
 state={error:false};static getDerivedStateFromError(){return{error:true};}
 render(){return this.state.error?<div className="empty"><h2>Something went wrong</h2><button onClick={()=>location.reload()}>Reload HouseHunt</button></div>:this.props.children;}
}
export function Protected({children}){const {user,loading}=useContext(Auth);return loading?<Loading/>:user?children:<Navigate to="/login" replace/>;}
export function Loading(){return <div className="empty" role="status"><span className="spinner-border spinner-border-sm"/> Loading your next chapter…</div>;}
export function Alert({message}){return message?<div className="alert alert-danger" role="alert">{message}</div>:null;}
export function Header(){
 const {user,logout}=useContext(Auth);const [open,setOpen]=useState(false);
 return <header><Link className="brand" to="/"><span className="brand-icon"><Home size={23}/></span>househunt<span className="brand-dot">.</span></Link><button className="mobile-toggle" aria-label="Toggle menu" onClick={()=>setOpen(!open)}><Menu/></button><nav className={open?'open':''}><NavLink to="/" end>Find a home</NavLink><NavLink to="/dashboard">My dashboard</NavLink><NavLink to="/account">Saved homes</NavLink><Link to="/list">List your property <ArrowUpRight size={15}/></Link></nav><div className="header-actions">{user?<><span className="user-name">Hi, {user.name.split(' ')[0]}</span><button className="text-button" onClick={logout}>Log out</button></>:<><Link to="/login" className="text-button">Log in</Link><Link className="button small" to="/register">Get started <ArrowUpRight size={16}/></Link></>}</div></header>;
}
export function PropertyCard({p}){
 const {favorites,toggleFavorite,user}=useContext(Auth);
 const saved=favorites.includes(p.id);const [saveError,setSaveError]=useState(''),[saving,setSaving]=useState(false);
 async function toggle(){setSaveError('');if(!user){setSaveError('Log in to save this home.');return;}setSaving(true);try{await toggleFavorite(p.id);}catch(e){setSaveError(errorText(e));}finally{setSaving(false);}}
 return <article className="property-card"><div className="photo"><Link to={'/property/'+p.id}><img loading="lazy" src={p.imageUrl||'/placeholder.svg'} alt={p.title} onError={e=>{e.currentTarget.onerror=null;e.currentTarget.src='/placeholder.svg';}}/></Link><span className="photo-tag">{p.type}</span><button className={'favorite '+(saved?'saved':'')} onClick={toggle} disabled={saving} aria-label={saved?'Remove from saved homes':'Save home'}><Heart size={18} fill={saved?'currentColor':'none'}/></button></div><div className="card-body">{saveError&&<small role="alert" className="danger">{saveError}</small>}<div className="price">{money(p.price)}<span> / month</span><ArrowUpRight size={20}/></div><Link className="property-title" to={'/property/'+p.id}>{p.title}</Link><p className="location"><MapPin size={14}/>{p.city} · {p.address}</p><div className="specs"><span><BedDouble size={16}/>{p.bedrooms} beds</span><span><Bath size={16}/>{p.bathrooms} baths</span><span><Maximize size={14}/>{p.area} sq ft</span></div></div></article>;
}
export function Field({label,value,onChange,type='text',required=true,...props}){return <label className="field">{label}<input className="form-control" type={type} value={value} onChange={e=>onChange(e.target.value)} required={required} {...props}/></label>;}
export function Chat(){
 const {user}=useContext(Auth);const [open,setOpen]=useState(false),[messages,setMessages]=useState([]),[input,setInput]=useState(''),[busy,setBusy]=useState(false),[error,setError]=useState('');
 useEffect(()=>{setMessages([]);if(user)api.get('/chat').then(r=>setMessages(r.data.reverse())).catch(()=>{});},[user]);
 async function send(e){e.preventDefault();if(!input.trim()||busy)return;setBusy(true);setError('');try{const {data}=await api.post('/chat',{message:input});setMessages(m=>[...m,data]);setInput('');}catch(e){setError(errorText(e));}finally{setBusy(false);}}
 return <><button className="chat-launch" onClick={()=>setOpen(!open)} aria-label="Open home assistant"><MessageCircle size={20}/> Ask HouseHunt</button>{open&&<aside className="chat-panel"><div className="chat-header"><div><strong>Your home companion</strong><small>Grounded in approved listings</small></div><button onClick={()=>setOpen(false)} aria-label="Close assistant"><X size={20}/></button></div><div className="chat-messages"><div className="chat-answer">Hi! Tell me what your next home looks like. Try “A two-bedroom apartment in Bengaluru.”</div>{messages.map(m=><React.Fragment key={m.id}><div className="chat-question">{m.question}</div><div className="chat-answer"><ReactMarkdown>{m.answer}</ReactMarkdown></div></React.Fragment>)}{busy&&<div className="chat-answer" role="status">Finding a little inspiration…</div>}<Alert message={error}/></div>{user?<form className="chat-input" onSubmit={send}><input aria-label="Message the assistant" maxLength={1000} placeholder="Tell us about your ideal home…" value={input} onChange={e=>setInput(e.target.value)}/><button disabled={busy||!input.trim()} aria-label="Send message"><Send size={19}/></button></form>:<Link className="button m-3" to="/login" onClick={()=>setOpen(false)}>Log in to chat</Link>}</aside>}</>;
}
