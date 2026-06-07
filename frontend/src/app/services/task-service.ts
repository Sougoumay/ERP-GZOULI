import { Injectable } from '@angular/core';

import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { Task, TaskCreation } from '../models/task';

@Injectable({
  providedIn: 'root',
})
export class TaskService {
  private apiUrl = `${environment.apiUrl}/projects`;

  constructor(private http: HttpClient) {}

  // 1. Lister les tâches d'un projet
  getTasks(projectId: number): Observable<Task[]> {
    return this.http.get<Task[]>(`${this.apiUrl}/${projectId}/tasks`);
  }

  // 2. Créer
  createTask(projectId: number, data: TaskCreation): Observable<Task> {
    return this.http.post<Task>(`${this.apiUrl}/${projectId}/tasks`, data);
  }

  // 3. Modifier
  updateTask(projectId: number, taskId: number, data: TaskCreation): Observable<Task> {
    return this.http.put<Task>(`${this.apiUrl}/${projectId}/tasks/${taskId}`, data);
  }

  // 4. Toggle (Cocher / Décocher)
  toggleTask(projectId: number, taskId: number): Observable<Task> {
    return this.http.patch<Task>(`${this.apiUrl}/${projectId}/tasks/${taskId}/toggle`, {});
  }

  // 5. Supprimer
  deleteTask(projectId: number, taskId: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${projectId}/tasks/${taskId}`);
  }

  // 6. Récupérer le % d'avancement (KPI)
  getProgress(projectId: number): Observable<{ progress: number }> {
    return this.http.get<{ progress: number }>(`${this.apiUrl}/${projectId}/tasks/progress`);
  }
}
