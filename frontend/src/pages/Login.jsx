import React,{createContext,useContext,useState,useEffect,Component} from 'react';
import {Link,NavLink,Navigate,useNavigate,useParams} from 'react-router-dom';
import {Home,ArrowUpRight,ArrowRight,Search,MapPin,BedDouble,Bath,Maximize,SlidersHorizontal,Plus,MessageCircle,X,Send,ShieldCheck,Leaf,Menu,Heart} from 'lucide-react';
import ReactMarkdown from 'react-markdown';
import {api,setToken,errorText,money} from '../api';
import {Auth,Loading,Alert,Field,PropertyCard} from '../ui';
function Login({register=false}){
 const {login}=useContext(Auth),navigate=useNavigate();const [form,setForm]=useState({name:'',email:'',password:''}),[error,setError]=useState(''),[busy,setBusy]=useState(false);
 async function submit(e){e.preventDefault();setBusy(true);setError('');try{const {data}=await api.post('/auth/'+(register?'register':'login'),register?form:{email:form.email,password:form.password});login(data);navigate('/dashboard');}catch(e){setError(errorText(e));}finally{setBusy(false);}}
 return <main className="auth-page"><div className="auth-aside"><span className="eyebrow">WELCOME TO HOUSEHUNT</span><h1>A home for your<br/><em>next chapter.</em></h1><p>Find your place, meet your owner, and settle into something good.</p></div><form className="form-panel" onSubmit={submit}><h2>{register?'Make yourself at home.':'Good to see you again.'}</h2><p>{register?'Create an account to book and list homes.':'Log in to manage your HouseHunt journey.'}</p><Alert message={error}/>{register&&<Field label="Full name" value={form.name} onChange={v=>setForm({...form,name:v})} maxLength={100}/>}<Field label="Email address" type="email" value={form.email} onChange={v=>setForm({...form,email:v})}/><Field label="Password" type="password" minLength={register?8:undefined} maxLength={72} value={form.password} onChange={v=>setForm({...form,password:v})}/><button className="button w-100" disabled={busy}>{busy?'Please wait…':register?'Create account':'Log in'}<ArrowRight size={17}/></button><p className="mt-3">{register?'Already have an account?':'New around here?'} <Link to={register?'/login':'/register'}>{register?'Log in':'Create an account'}</Link></p></form></main>;
}

export default Login;