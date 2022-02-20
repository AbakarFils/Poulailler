import { IVariable } from 'app/entities/variable/variable.model';

export interface ICo2 {
  id?: number;
  volume?: number | null;
  variable?: IVariable;
}

export class Co2 implements ICo2 {
  constructor(public id?: number, public volume?: number | null, public variable?: IVariable) {}
}

export function getCo2Identifier(co2: ICo2): number | undefined {
  return co2.id;
}
