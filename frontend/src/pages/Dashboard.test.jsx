import React from 'react';
import {describe,it,expect,vi,afterEach} from 'vitest';
import {render,screen,cleanup} from '@testing-library/react';
import {MemoryRouter} from 'react-router-dom';
import {Auth} from '../ui';
import {api} from '../api';
import Dashboard from './Dashboard';
vi.mock('../api',()=>({
 api:{get:vi.fn()},
 errorText:e=>e.message,
 money:n=>'INR '+n
}));
afterEach(()=>{cleanup();vi.clearAllMocks();});
describe('dashboard loading',()=>{
 it('keeps successful sections visible and identifies the section that failed',async()=>{
  api.get.mockImplementation(url=>{
   if(url==='/dashboard/properties')return Promise.resolve({data:[{id:7,title:'My rental',city:'Pune',price:15000,status:'APPROVED'}]});
   if(url==='/bookings')return Promise.resolve({data:[]});
   if(url==='/admin/properties')return Promise.reject(new Error('Review unavailable'));
   return Promise.resolve({data:{content:[{id:1,name:'Admin',email:'admin@test.local',role:'ADMIN'}],totalPages:1}});
  });
  render(<MemoryRouter><Auth.Provider value={{user:{id:1,name:'Admin',role:'ADMIN'}}}><Dashboard/></Auth.Provider></MemoryRouter>);
  expect(await screen.findByText('My rental')).toBeInTheDocument();
  expect(await screen.findByRole('alert')).toHaveTextContent('Listing review: Review unavailable');
  expect(screen.getByText('admin@test.local')).toBeInTheDocument();
 });
});