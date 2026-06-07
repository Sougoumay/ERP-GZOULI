import { Injectable } from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {map, Observable, switchMap} from "rxjs";
import {environment} from '../../environments/environment';

@Injectable({
  providedIn: 'root',
})
export class S3Service {

  constructor(private http: HttpClient) {}

  uploadFile(path: string, file: File): Observable<{ fileKey: string, fileName: string }> {

    return this.http.get<{presignedUrl: string, fileKey: string}>(
      `${environment.apiUrl}/s3/presigned-url?path=${path}&fileName=${file.name}&contentType=${file.type}`
    ).pipe(

      switchMap((s3Response) => {

        return this.http.put(s3Response.presignedUrl, file, {
          headers: {
            'Content-Type': file.type
          }
        }).pipe(

          map(() => ({
            fileKey: s3Response.fileKey,
            fileName: file.name
          }))
        );

      })
    );
  }
}
