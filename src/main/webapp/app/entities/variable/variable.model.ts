import dayjs from 'dayjs/esm';
import { IUser } from 'app/entities/user/user.model';

export interface IVariable {
  id?: number;
  plageMax?: number | null;
  dateCreation?: dayjs.Dayjs | null;
  lue?: boolean | null;
  consulter?: IUser | null;
}

export class Variable implements IVariable {
  constructor(
    public id?: number,
    public plageMax?: number | null,
    public dateCreation?: dayjs.Dayjs | null,
    public lue?: boolean | null,
    public consulter?: IUser | null
  ) {
    this.lue = this.lue ?? false;
  }
}

export function getVariableIdentifier(variable: IVariable): number | undefined {
  return variable.id;
}
