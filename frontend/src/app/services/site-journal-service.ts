import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, forkJoin, of } from 'rxjs';
import { switchMap, map } from 'rxjs/operators';
import { environment } from '../../environments/environment';
import {S3Service} from './s3-service';

export interface JournalPhotoData {
  file: File;
  description: string;
}

export interface JournalPhotoResponseDTO {
  fileUrl: string;
  description: string;
}

export interface SiteJournalDetailDTO {
  id: number;
  workDate: string | Date;
  location: string;
  serviceType: string;
  authorName: string;
  taskDescription: string;
  pvFileUrls: string[];
  photos: JournalPhotoResponseDTO[];
}

export interface SiteJournalSummaryDTO {
  id : number;
  workDate : Date;
  location : string;
  authorName : string; // Concaténation Nom + Prénom
}

@Injectable({
  providedIn: 'root'
})
export class SiteJournalService {

  // N'oubliez pas le /admin si vos routes backend sont sécurisées de cette façon [1]
  private baseUrl = `${environment.apiUrl}/projects`;

  constructor(private http: HttpClient, private s3UploadService: S3Service) {}

  /**
   * LA MÉTHODE PRINCIPALE : Gère les uploads multiples avec s3UploadService
   */
  createJournal(
    projectId: number,
    journalData: any,
    pvFiles: File[],
    photoItems: JournalPhotoData[]
  ): Observable<any> {

    // 1. Upload des PVs (On met le path 'journals/pvs')
    const pvUploads$ = pvFiles.length > 0
      ? forkJoin(pvFiles.map(file =>
        this.s3UploadService.uploadFile('journals/pvs', file).pipe(
          // Le backend attend une simple liste de Strings (les fileKeys)
          map(fileInfo => fileInfo.fileKey)
        )
      ))
      : of([]);

    // 2. Upload des Photos (On met le path 'journals/photos')
    const photoUploads$ = photoItems.length > 0
      ? forkJoin(photoItems.map(item =>
        this.s3UploadService.uploadFile('journals/photos', item.file).pipe(
          // On associe la clé générée avec la description saisie par l'utilisateur
          map(fileInfo => ({ fileKey: fileInfo.fileKey, description: item.description }))
        )
      ))
      : of([]);

    // 3. Exécuter TOUS les uploads S3 en même temps
    return forkJoin({
      pvKeys: pvUploads$,
      photos: photoUploads$
    }).pipe(
      switchMap(results => {

        // 4. Construction du JSON final (Correspond exactement au DTO Spring Boot !)
        const payload = {
          ...journalData,
          pvFileKeys: results.pvKeys,
          photos: results.photos
        };

        // 5. Envoi final à la base de données
        return this.http.post(`${this.baseUrl}/${projectId}/journals`, payload);
      })
    );
  }

  // Méthode pour lire l'historique
  getJournalsByProject(projectId: number): Observable<SiteJournalSummaryDTO[]> {
    return this.http.get<SiteJournalSummaryDTO[]>(`${this.baseUrl}/${projectId}/journals`);
  }

  /**
   * Récupère le détail complet d'un journal de chantier spécifique,
   * incluant la description et les URLs générées pour les PVs et les photos.
   */
  getJournalDetails(projectId: number, journalId: number): Observable<SiteJournalDetailDTO> {
    return this.http.get<SiteJournalDetailDTO>(`${this.baseUrl}/${projectId}/journals/${journalId}`);
  }
}
