import { Injectable } from '@angular/core';
import {environment} from '../../environments/environment';
import {HttpClient, HttpParams} from '@angular/common/http';
import {Equipment} from '../models/equipment';
import {Observable} from 'rxjs';
import {ProjectEquipment} from '../models/project-equipment';

@Injectable({
  providedIn: 'root',
})
export class EquipmentService {
  private apiUrl = `${environment.apiUrl}`;

  constructor(private http: HttpClient) {}

  // 1. Récupérer le stock DISPONIBLE (pour le dropdown)
  getAvailableEquipment(): Observable<Equipment[]> {
    return this.http.get<Equipment[]>(`${this.apiUrl}/inventory/available`);
  }

  // 2. Liste du matériel sur un projet spécifique
  getProjectEquipment(projectId: number): Observable<ProjectEquipment[]> {
    return this.http.get<ProjectEquipment[]>(`${this.apiUrl}/projects/${projectId}/equipments`);
  }

  // 3. Affecter du matériel (Prendre du stock)
  assignEquipment(projectId: number, data: { equipmentId: number, startDate: Date }): Observable<void> {
    return this.http.post<void>(`${this.apiUrl}/projects/${projectId}/equipments`, data);
  }

  // 4. Libérer du matériel (Rendre au stock)
  releaseEquipment(projectId: number, assignmentId: number, returnDate: string): Observable<void> {
    // On passe la date en query param comme défini dans le backend
    const params = new HttpParams().set('date', returnDate);
    return this.http.patch<void>(
      `${this.apiUrl}/projects/${projectId}/equipments/${assignmentId}/release`,
      {},
      { params }
    );
  }

  //
  // 5. Lister TOUT le stock (y compris celui assigné)
  getAllInventory(): Observable<Equipment[]> {
    return this.http.get<Equipment[]>(`${this.apiUrl}/inventory`);
  }

  // 6. Créer un nouvel équipement
  createEquipment(data: any): Observable<Equipment> {
    console.log(data);
    return this.http.post<Equipment>(`${this.apiUrl}/inventory`, data);
  }

  // 7. Modifier un équipement
  updateEquipment(id: number, data: any): Observable<Equipment> {
    return this.http.put<Equipment>(`${this.apiUrl}/inventory/${id}`, data);
  }

  // 8. Supprimer
  deleteEquipment(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/inventory/${id}`);
  }
}
