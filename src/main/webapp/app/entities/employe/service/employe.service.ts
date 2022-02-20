import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IEmploye, getEmployeIdentifier } from '../employe.model';

export type EntityResponseType = HttpResponse<IEmploye>;
export type EntityArrayResponseType = HttpResponse<IEmploye[]>;

@Injectable({ providedIn: 'root' })
export class EmployeService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/employes');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(employe: IEmploye): Observable<EntityResponseType> {
    return this.http.post<IEmploye>(this.resourceUrl, employe, { observe: 'response' });
  }

  update(employe: IEmploye): Observable<EntityResponseType> {
    return this.http.put<IEmploye>(`${this.resourceUrl}/${getEmployeIdentifier(employe) as number}`, employe, { observe: 'response' });
  }

  partialUpdate(employe: IEmploye): Observable<EntityResponseType> {
    return this.http.patch<IEmploye>(`${this.resourceUrl}/${getEmployeIdentifier(employe) as number}`, employe, { observe: 'response' });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IEmploye>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IEmploye[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addEmployeToCollectionIfMissing(employeCollection: IEmploye[], ...employesToCheck: (IEmploye | null | undefined)[]): IEmploye[] {
    const employes: IEmploye[] = employesToCheck.filter(isPresent);
    if (employes.length > 0) {
      const employeCollectionIdentifiers = employeCollection.map(employeItem => getEmployeIdentifier(employeItem)!);
      const employesToAdd = employes.filter(employeItem => {
        const employeIdentifier = getEmployeIdentifier(employeItem);
        if (employeIdentifier == null || employeCollectionIdentifiers.includes(employeIdentifier)) {
          return false;
        }
        employeCollectionIdentifiers.push(employeIdentifier);
        return true;
      });
      return [...employesToAdd, ...employeCollection];
    }
    return employeCollection;
  }
}
