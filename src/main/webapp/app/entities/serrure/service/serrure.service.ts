import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { ISerrure, getSerrureIdentifier } from '../serrure.model';

export type EntityResponseType = HttpResponse<ISerrure>;
export type EntityArrayResponseType = HttpResponse<ISerrure[]>;

@Injectable({ providedIn: 'root' })
export class SerrureService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/serrures');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(serrure: ISerrure): Observable<EntityResponseType> {
    return this.http.post<ISerrure>(this.resourceUrl, serrure, { observe: 'response' });
  }

  update(serrure: ISerrure): Observable<EntityResponseType> {
    return this.http.put<ISerrure>(`${this.resourceUrl}/${getSerrureIdentifier(serrure) as number}`, serrure, { observe: 'response' });
  }

  partialUpdate(serrure: ISerrure): Observable<EntityResponseType> {
    return this.http.patch<ISerrure>(`${this.resourceUrl}/${getSerrureIdentifier(serrure) as number}`, serrure, { observe: 'response' });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<ISerrure>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<ISerrure[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addSerrureToCollectionIfMissing(serrureCollection: ISerrure[], ...serruresToCheck: (ISerrure | null | undefined)[]): ISerrure[] {
    const serrures: ISerrure[] = serruresToCheck.filter(isPresent);
    if (serrures.length > 0) {
      const serrureCollectionIdentifiers = serrureCollection.map(serrureItem => getSerrureIdentifier(serrureItem)!);
      const serruresToAdd = serrures.filter(serrureItem => {
        const serrureIdentifier = getSerrureIdentifier(serrureItem);
        if (serrureIdentifier == null || serrureCollectionIdentifiers.includes(serrureIdentifier)) {
          return false;
        }
        serrureCollectionIdentifiers.push(serrureIdentifier);
        return true;
      });
      return [...serruresToAdd, ...serrureCollection];
    }
    return serrureCollection;
  }
}
