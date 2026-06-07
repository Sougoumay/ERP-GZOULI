import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import {Observable, switchMap} from 'rxjs';
import { environment } from '../../environments/environment';
import {Expense} from '../models/expense';


@Injectable({
  providedIn: 'root',
})
export class ExpenseService {
  private apiUrl = `${environment.apiUrl}/projects`;

  constructor(private http: HttpClient) {}

  // Récupérer la liste
  getExpenses(projectId: number): Observable<Expense[]> {
    return this.http.get<Expense[]>(`${this.apiUrl}/${projectId}/expenses`);
  }

  // Ajouter (Avec le CORRECTIF Multipart)
  addExpense(projectId: number, expenseData: any, file: File): Observable<any> {

    // ACTION 1 : Obtenir l'URL et la Clé
    return this.http.get<{presignedUrl: string, fileKey: string}>(
      `${environment.apiUrl}/s3/presigned-url?path=expenses&fileName=${file.name}&contentType=${file.type}`
    ).pipe(

      switchMap((s3Response) => {

        // ACTION 2 : Uploader directement chez AWS S3
        // On fait un PUT sur l'URL reçue, en attachant le fichier physique
        return this.http.put(s3Response.presignedUrl, file, {
          headers: {
            'Content-Type': file.type // Obligatoire pour correspondre à la signature
          }
        }).pipe(

          switchMap(() => {

            // ACTION 3 : Confirmer au Backend
            // On prépare le DTO final avec toutes les infos du formulaire + la clé S3
            const payload = {
              ...expenseData,
              fileKey: s3Response.fileKey,
              fileName: file.name
            };

            // On envoie le tout à notre endpoint métier classique
            return this.http.post(`${environment.apiUrl}/projects/${projectId}/expenses`, payload);
          })
        );
      })
    );
  }

  // Supprimer
  deleteExpense(projectId: number, expenseId: number): Observable<void> {
    // Note: L'URL backend dépend de comment vous l'avez routée.
    // Basé sur votre contrôleur précédent : /api/projects/{projectId}/expenses/{expenseId}
    return this.http.delete<void>(`${this.apiUrl}/${projectId}/expenses/${expenseId}`);
  }
}
