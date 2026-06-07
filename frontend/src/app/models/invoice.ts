export interface Invoice {
  id?: number;
  invoiceNumber: string;
  submissionDate: string; // YYYY-MM-DD
  amount: number;
  isCertified: boolean;
  fileName?: string;
  downloadUrl?: string; // Le lien temporaire S3
}

