import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, switchMap } from 'rxjs';
import { environment } from '../../environments/environment';
import { ProjectMissionOrder } from '../models/project-mission-order';
import { S3Service } from './s3-service';

@Injectable({
  providedIn: 'root',
})
export class ProjectMissionOrderService {
  constructor(private http: HttpClient, private s3UploadService: S3Service) { }

  // 1. LECTURE : Récupérer l'historique des ordres d'un projet
  getOrdersByProject(projectId: number): Observable<ProjectMissionOrder[]> {
    return this.http.get<ProjectMissionOrder[]>(`${environment.apiUrl}/projects/${projectId}/orders`);
  }

  // 2. CRÉATION (Avec Upload S3)
  createOrder(projectId: number, orderData: any, file: File): Observable<any> {
    // Étape 1 : Obtenir l'URL S3
    return this.s3UploadService.uploadFile('invoices', file).pipe(

      switchMap((fileInfo) => {

        const payload = {
          ...orderData,
          fileKey: fileInfo.fileKey,
          fileName: fileInfo.fileName
        };

        return this.http.post(
          `${environment.apiUrl}/projects/${projectId}/orders`,
          payload
        );
      })
    );
  }

  // 3. MISE À JOUR (Le fichier est optionnel)
  updateOrder(projectId: number, orderId: number, orderData: any, file?: File): Observable<any> {
    if (file) {
      return this.s3UploadService.uploadFile('invoices', file).pipe(

        switchMap((fileInfo) => {

          const payload = {
            ...orderData,
            fileKey: fileInfo.fileKey,
            fileName: fileInfo.fileName
          };

          return this.http.put(
            `${environment.apiUrl}/projects/${projectId}/orders/${orderId}`,
            payload
          );
        })
      );
    } else {
      // S'il n'y a pas de fichier, on met juste à jour les données (ex: la date d'effet)
      return this.http.put(`${environment.apiUrl}/projects/${projectId}/orders/${orderId}`, orderData);
    }
  }

  // 4. SUPPRESSION
  deleteOrder(projectId: number, orderId: number): Observable<any> {
    return this.http.delete(`${environment.apiUrl}/projects/${projectId}/orders/${orderId}`);
  }
}
