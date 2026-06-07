import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class ReportService {

  constructor(private http: HttpClient) {}

  /**
   * Envoie les dates et le texte HTML au backend, et récupère un fichier Word (Blob)
   */
  generateWordReport(projectId: number, payload: any): Observable<Blob> {
    return this.http.post(`${environment.apiUrl}/admin/projects/${projectId}/generate-report`, payload, {
      responseType: 'blob' // TRÈS IMPORTANT : Indique qu'on attend un fichier binaire
    });
  }
}
