import { Injectable } from '@angular/core';
import {Observable} from 'rxjs';
import {HttpClient, HttpParams} from '@angular/common/http';
import {ProjectRegistration} from '../models/project-registration';
import {ProjectSummary} from '../models/project-summary';
import {environment} from '../../environments/environment';
import {ProjectDetail} from '../models/project-detail';
import {TeamMember} from '../models/team-member';

@Injectable({
  providedIn: 'root',
})
export class ProjectService {
  private apiUrl = `${environment.apiUrl}/projects`; // ex: http://localhost:8080/api/projects

  constructor(private http: HttpClient) {}

  getAllProjects(): Observable<ProjectSummary[]> {
    return this.http.get<ProjectSummary[]>(this.apiUrl);
  }

  getProjectById(id: number): Observable<ProjectDetail> {
    return this.http.get<ProjectDetail>(`${this.apiUrl}/${id}`);
  }

  createProject(project: ProjectRegistration): Observable<any> {
    return this.http.post(this.apiUrl, project);
  }

  updateProject(id: number, project: ProjectSummary): Observable<any> {
    return this.http.put(`${this.apiUrl}/${id}`, project);
  }

  // Soft Delete / Réactivation (Comme pour EmployeeService)
  toggleStatus(id: number, isActive: boolean): Observable<void> {
    const params = new HttpParams().set('active', isActive.toString());
    // Utilisation de PATCH pour éviter le conflit avec le PUT update
    return this.http.patch<void>(`${this.apiUrl}/${id}/status`, null, { params });
  }

  getProjectTeam(projectId: number): Observable<TeamMember[]> {
    console.log("L'id du projet est : " + projectId);
    return this.http.get<TeamMember[]>(`${this.apiUrl}/${projectId}/team`);
  }

  assignTeamMembers(projectId: number, employeeIds: number[]): Observable<void> {
    return this.http.post<void>(`${this.apiUrl}/${projectId}/team`, employeeIds);
  }

  removeTeamMembers(projectId: number, employeeIds: number[]): Observable<void> {
    // Plus besoin de l'option { body: ... }, on passe les données directement
    return this.http.post<void>(`${this.apiUrl}/${projectId}/team/remove`, employeeIds);
  }
}
