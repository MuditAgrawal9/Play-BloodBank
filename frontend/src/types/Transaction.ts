import type { User } from "./User";

export interface Transaction {
  id: number;
  user : User
  bloodGroup: string;
  transactionType: string;
  units: number;
  status: string;
  transactionDate: string;
}