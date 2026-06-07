import { Injectable } from '@angular/core';
import {environment} from '../../environments/environment';
import {HttpClient, HttpParams} from '@angular/common/http';
import {Observable} from 'rxjs';
import {EmployeeSummary} from '../models/employee-summary';
import {EmployeeRegistration} from '../models/employee-registration';
import {SalaryAdvance} from '../models/salary-advance';
import {EmployeeDetail} from '../models/employee-detail';

@Injectable({
  providedIn: 'root'
})
export class EmployeeService {

  private apiUrl = `${environment.apiUrl}/admin/employees`;

  constructor(private http: HttpClient) {}

  getAllEmployees(): Observable<EmployeeSummary[]> {
    return this.http.get<EmployeeSummary[]>(this.apiUrl);
  }

  getEmployeeDetail(id: number): Observable<EmployeeDetail> {
    return this.http.get<EmployeeDetail>(`${this.apiUrl}/${id}`);
  }


  toggleStatus(id: number | undefined, isActive: boolean): Observable<void> {
    const params = new HttpParams().set('status', isActive.toString());
    return this.http.patch<void>(`${this.apiUrl}/${id}`, null, { params });
  }

  createEmployee(employeeData: EmployeeSummary): Observable<any> {
    return this.http.post(this.apiUrl, employeeData);
  }

  updateEmployee(id: number | undefined, employeeData: EmployeeSummary): Observable<any> {
    return this.http.put(`${this.apiUrl}/${id}`, employeeData);
  }


  getAdvances(employeeId: number): Observable<SalaryAdvance[]> {
    return this.http.get<SalaryAdvance[]>(`${this.apiUrl}/${employeeId}/advances`);
  }

  addAdvance(employeeId: number, data: any): Observable<SalaryAdvance> {
    return this.http.post<SalaryAdvance>(`${this.apiUrl}/${employeeId}/advances`, data);
  }

}
