import React,{createContext,useContext,useState,useEffect,Component} from 'react';
import {Link,NavLink,Navigate,useNavigate,useParams} from 'react-router-dom';
import {Home,ArrowUpRight,ArrowRight,Search,MapPin,BedDouble,Bath,Maximize,SlidersHorizontal,Plus,MessageCircle,X,Send,ShieldCheck,Leaf,Menu,Heart} from 'lucide-react';
import ReactMarkdown from 'react-markdown';
import {api,setToken,errorText,money} from '../api';
import {Auth,Loading,Alert,Field,PropertyCard} from '../ui';
function Account(){
 const {favorites}=useContext(Auth);const [homes,setHomes]=useState([]),[profile,setProfile]=useState({phone:'',preferredCity:''}),[error,setError]=useState(''),[success,setSuccess]=useState(''),[busy,setBusy]=useState(false);
 useEffect(()=>{api.get('/account/favorites').then(r=>setHomes(r.data)).catch(e=>setError(errorText(e)));},[favorites]);
 useEffect(()=>{api.get('/account/profile').then(r=>setProfile(r.data)).catch(e=>setError(errorText(e)));},[]);
 async function save(e){e.preventDefault();setBusy(true);setError('');try{await api.put('/account/profile',profile);setSuccess('Preferences saved.');}catch(e){setError(errorText(e));}finally{setBusy(false);}}
 return <main className="page"><span className="eyebrow">MAKE IT PERSONAL</span><h1>Your saved homes</h1><p>A little shortlist for your next chapter.</p><Alert message={error}/>{homes.length?<div className="property-grid">{homes.map(p=><PropertyCard p={p} key={p.id}/>)}</div>:<p className="empty">Tap the heart on a home to save it here.</p>}<form className="form-panel mt-5" onSubmit={save}><h2>Your preferences</h2>{success&&<div className="alert alert-success">{success}</div>}<div className="form-grid"><Field label="Phone (optional)" required={false} maxLength={30} value={profile.phone} onChange={v=>setProfile({...profile,phone:v})}/><Field label="Preferred city" required={false} maxLength={100} value={profile.preferredCity} onChange={v=>setProfile({...profile,preferredCity:v})}/></div><button className="button" disabled={busy}>{busy?'Saving…':'Save preferences'}</button></form></main>;
}

export default Account;