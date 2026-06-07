import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import {Observable, switchMap} from 'rxjs';
import { environment } from '../../environments/environment';
import { Invoice } from '../models/invoice';
import {S3Service} from './s3-service';

@Injectable({
  providedIn: 'root',
})
export class InvoiceService {
  private apiUrl = `${environment.apiUrl}/projects`;

  constructor(private http: HttpClient, private s3UploadService : S3Service) {}

  getInvoices(projectId: number): Observable<Invoice[]> {
    return this.http.get<Invoice[]>(`${this.apiUrl}/${projectId}/invoices`);
  }

  addInvoice(projectId: number, invoiceData: any, file: File) {

    return this.s3UploadService.uploadFile('invoices', file).pipe(

      switchMap((fileInfo) => {

        const payload = {
          ...invoiceData,
          fileKey: fileInfo.fileKey,
          fileName: fileInfo.fileName
        };

        return this.http.post(
          `${environment.apiUrl}/projects/${projectId}/invoices`,
          payload
        );
      })
    );
  }

  updateInvoice(projectId: number, invoiceId: number, invoiceData: any, file?: File) {

    if(file) {
      return this.s3UploadService.uploadFile('invoices', file).pipe(

        switchMap((fileInfo) => {

          const payload = {
            ...invoiceData,
            fileKey: fileInfo.fileKey,
            fileName: fileInfo.fileName
          };

          return this.http.put(
            `${environment.apiUrl}/projects/${projectId}/invoices/${invoiceId}`,
            payload
          );
        })
      );
    } else {
      const payload = {
        ...invoiceData,
        fileKey: '',
        fileName: ''
      };

      return this.http.put(
        `${environment.apiUrl}/projects/${projectId}/invoices/${invoiceId}`,
        payload
      );
    }


  }

  // updateInvoice(projectId: number, invoiceId: number, data: any, file?: File): Observable<void> {
  //   const formData = new FormData();
  //   if (file) formData.append('file', file); // On n'ajoute que s'il y a un nouveau fichier
  //   formData.append('invoice', JSON.stringify(data));
  //   return this.http.put<void>(`${this.apiUrl}/${projectId}/invoices/${invoiceId}`, formData);
  // }

  toggleCertify(projectId: number, invoiceId: number): Observable<void> {
    return this.http.patch<void>(`${this.apiUrl}/${projectId}/invoices/${invoiceId}/certify`, {});
  }

  deleteInvoice(projectId: number, invoiceId: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${projectId}/invoices/${invoiceId}`);
  }
}
