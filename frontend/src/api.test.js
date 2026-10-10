import {describe,it,expect} from 'vitest';
import {money,errorText,setToken,api} from './api';
describe('API client',()=>{
 it('formats rental prices in INR',()=>expect(money(28000)).toContain('28,000'));
 it('shows server validation messages',()=>expect(errorText({response:{data:{message:'End date must be after start date'}}})).toBe('End date must be after start date'));
 it('keeps bearer token in session storage and clears it on logout',()=>{setToken('example');expect(sessionStorage.getItem('househunt-token')).toBe('example');setToken(null);expect(sessionStorage.getItem('househunt-token')).toBeNull();});
 it('provides useful network errors',()=>expect(errorText({})).toContain('backend'));
});