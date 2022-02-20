import { IVariable } from 'app/entities/variable/variable.model';

export interface IOeuf {
  id?: number;
  nombreJournalier?: number | null;
  variable?: IVariable;
}

export class Oeuf implements IOeuf {
  constructor(public id?: number, public nombreJournalier?: number | null, public variable?: IVariable) {}
}

export function getOeufIdentifier(oeuf: IOeuf): number | undefined {
  return oeuf.id;
}
