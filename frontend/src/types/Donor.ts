import type { User } from "./User";

export interface Donor {
    id : number,
    age : number,
  user: User
  bloodGroup: string;
}