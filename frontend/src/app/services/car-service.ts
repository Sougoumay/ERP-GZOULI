import { Injectable } from '@angular/core';
import {environment} from '../../environments/environment';
import {Observable} from 'rxjs';
import {HttpClient, HttpParams} from '@angular/common/http';
import {Car} from '../models/car';

@Injectable({
  providedIn: 'root',
})
export class CarService {
  private apiUrl = `${environment.apiUrl}/admin/cars`;

  constructor(private http: HttpClient) {}

  getAllCars(): Observable<Car[]> {
    return this.http.get<Car[]>(this.apiUrl);
  }

  createCar(car: any): Observable<Car> {
    return this.http.post<Car>(this.apiUrl, car);
  }

  updateCar(id: number, car: any): Observable<Car> {
    return this.http.put<Car>(`${this.apiUrl}/${id}`, car);
  }

  deleteCar(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  // Assigner un véhicule à un employé
  assignCar(data: { carId: number, employeeId: number, startDate: Date }): Observable<void> {
    return this.http.post<void>(`${this.apiUrl}/assign`, data);
  }

  // Récupérer le véhicule (Libération)
  releaseCar(carId: number, returnDate: string): Observable<void> {
    const params = new HttpParams().set('date', returnDate);
    return this.http.patch<void>(`${this.apiUrl}/${carId}/release`, {}, { params });
  }
}
