import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IVariable, getVariableIdentifier } from '../variable.model';

export type EntityResponseType = HttpResponse<IVariable>;
export type EntityArrayResponseType = HttpResponse<IVariable[]>;

@Injectable({ providedIn: 'root' })
export class VariableService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/variables');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<IVariable>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<IVariable[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map((res: EntityArrayResponseType) => this.convertDateArrayFromServer(res)));
  }

  addVariableToCollectionIfMissing(variableCollection: IVariable[], ...variablesToCheck: (IVariable | null | undefined)[]): IVariable[] {
    const variables: IVariable[] = variablesToCheck.filter(isPresent);
    if (variables.length > 0) {
      const variableCollectionIdentifiers = variableCollection.map(variableItem => getVariableIdentifier(variableItem)!);
      const variablesToAdd = variables.filter(variableItem => {
        const variableIdentifier = getVariableIdentifier(variableItem);
        if (variableIdentifier == null || variableCollectionIdentifiers.includes(variableIdentifier)) {
          return false;
        }
        variableCollectionIdentifiers.push(variableIdentifier);
        return true;
      });
      return [...variablesToAdd, ...variableCollection];
    }
    return variableCollection;
  }

  protected convertDateFromClient(variable: IVariable): IVariable {
    return Object.assign({}, variable, {
      dateCreation: variable.dateCreation?.isValid() ? variable.dateCreation.toJSON() : undefined,
    });
  }

  protected convertDateFromServer(res: EntityResponseType): EntityResponseType {
    if (res.body) {
      res.body.dateCreation = res.body.dateCreation ? dayjs(res.body.dateCreation) : undefined;
    }
    return res;
  }

  protected convertDateArrayFromServer(res: EntityArrayResponseType): EntityArrayResponseType {
    if (res.body) {
      res.body.forEach((variable: IVariable) => {
        variable.dateCreation = variable.dateCreation ? dayjs(variable.dateCreation) : undefined;
      });
    }
    return res;
  }
}
