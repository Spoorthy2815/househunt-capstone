import React from 'react';
import {createRoot} from 'react-dom/client';
import {BrowserRouter,Routes,Route,Link} from 'react-router-dom';
import 'bootstrap/dist/css/bootstrap.min.css';
import './styles.css';
import {AuthProvider,ErrorBoundary,Protected,Header,Chat} from './ui';
import Explore from './pages/Explore';
import Login from './pages/Login';
import ListingForm from './pages/ListingForm';
import Detail from './pages/Detail';
import Dashboard from './pages/Dashboard';
import Account from './pages/Account';
function App(){return <ErrorBoundary><BrowserRouter><AuthProvider><Header/><Routes><Route path="/" element={<Explore/>}/><Route path="/login" element={<Login/>}/><Route path="/register" element={<Login register/>}/><Route path="/property/:id" element={<Detail/>}/><Route path="/account" element={<Protected><Account/></Protected>}/><Route path="/dashboard" element={<Protected><Dashboard/></Protected>}/><Route path="/list" element={<Protected><ListingForm/></Protected>}/><Route path="/edit/:id" element={<Protected><ListingForm/></Protected>}/><Route path="*" element={<div className="empty"><h1>That address doesn’t exist.</h1><Link to="/">Find your way home</Link></div>}/></Routes><footer><Link className="brand" to="/">househunt.</Link><span>A better place to begin.</span><span>© {new Date().getFullYear()} HouseHunt</span></footer><Chat/></AuthProvider></BrowserRouter></ErrorBoundary>;}
createRoot(document.getElementById('root')).render(<App/>);