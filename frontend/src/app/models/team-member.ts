export interface TeamMember {
  id: number;
  firstName: string;
  lastName: string;
  role: 'INGENIEUR' | 'TECHNICIEN';  // 'INGENIEUR' | 'TECHNICIEN'
  email: string;
}
