export interface EmployeeRegistration {
  firstName: string;
  lastName: string;
  email: string;
  phoneNumber: string;
  address: string;
  salary: string;
  birthDay: string;
  idCardNumber: string;
  role: 'ADMIN' | 'INGENIEUR' | 'TECHNICIEN';
  active: boolean;
}
