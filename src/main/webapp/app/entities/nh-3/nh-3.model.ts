import { IVariable } from 'app/entities/variable/variable.model';

export interface INH3 {
  id?: number;
  volume?: number | null;
  variable?: IVariable;
}

export class NH3 implements INH3 {
  constructor(public id?: number, public volume?: number | null, public variable?: IVariable) {}
}

export function getNH3Identifier(nH3: INH3): number | undefined {
  return nH3.id;
}
