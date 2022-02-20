import { IUser } from 'app/entities/user/user.model';

export interface IEmploye {
  id?: number;
  numeroIdentite?: string;
  adresse?: string | null;
  user?: IUser;
}

export class Employe implements IEmploye {
  constructor(public id?: number, public numeroIdentite?: string, public adresse?: string | null, public user?: IUser) {}
}

export function getEmployeIdentifier(employe: IEmploye): number | undefined {
  return employe.id;
}
