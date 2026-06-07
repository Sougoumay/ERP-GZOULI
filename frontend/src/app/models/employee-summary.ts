export interface EmployeeSummary {
  id?: number;
  firstName: string;
  lastName: string;
  email: string;
  idCardNumber: string;
  address: string;
  phoneNumber: string;
  salary: string;
  birthday: Date;
  role: 'ADMIN' | 'INGENIEUR' | 'TECHNICIEN';
  active: boolean;
}
