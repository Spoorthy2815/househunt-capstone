import axios from 'axios';
export const api=axios.create({baseURL:import.meta.env.VITE_API_URL || '/api',timeout:45000});
let authToken=sessionStorage.getItem('househunt-token');
export function setToken(value){authToken=value;if(value)sessionStorage.setItem('househunt-token',value);else sessionStorage.removeItem('househunt-token');}
api.interceptors.request.use(config=>{if(authToken)config.headers.Authorization='Bearer '+authToken;return config;});
api.interceptors.response.use(r=>r,async error=>{
 const config=error.config;
 if(config?.method==='get' && !config._retried && (!error.response || error.response.status>=500)){config._retried=true;await new Promise(r=>setTimeout(r,500));return api(config);}
 if(error.response?.status===401 && !config?.url?.includes('/auth/login')){setToken(null);window.dispatchEvent(new Event('househunt-logout'));}
 return Promise.reject(error);
});
export const errorText=e=>e.response?.data?.message || (e.code==='ECONNABORTED'?'Request timed out. Please try again.':'Cannot reach the server. Check that the backend is running.');
export const money=n=>new Intl.NumberFormat('en-IN',{style:'currency',currency:'INR',maximumFractionDigits:0}).format(n);