import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { INH3, getNH3Identifier } from '../nh-3.model';

export type EntityResponseType = HttpResponse<INH3>;
export type EntityArrayResponseType = HttpResponse<INH3[]>;

@Injectable({ providedIn: 'root' })
export class NH3Service {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/nh-3-s');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(nH3: INH3): Observable<EntityResponseType> {
    return this.http.post<INH3>(this.resourceUrl, nH3, { observe: 'response' });
  }

  update(nH3: INH3): Observable<EntityResponseType> {
    return this.http.put<INH3>(`${this.resourceUrl}/${getNH3Identifier(nH3) as number}`, nH3, { observe: 'response' });
  }

  partialUpdate(nH3: INH3): Observable<EntityResponseType> {
    return this.http.patch<INH3>(`${this.resourceUrl}/${getNH3Identifier(nH3) as number}`, nH3, { observe: 'response' });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<INH3>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<INH3[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addNH3ToCollectionIfMissing(nH3Collection: INH3[], ...nH3sToCheck: (INH3 | null | undefined)[]): INH3[] {
    const nH3s: INH3[] = nH3sToCheck.filter(isPresent);
    if (nH3s.length > 0) {
      const nH3CollectionIdentifiers = nH3Collection.map(nH3Item => getNH3Identifier(nH3Item)!);
      const nH3sToAdd = nH3s.filter(nH3Item => {
        const nH3Identifier = getNH3Identifier(nH3Item);
        if (nH3Identifier == null || nH3CollectionIdentifiers.includes(nH3Identifier)) {
          return false;
        }
        nH3CollectionIdentifiers.push(nH3Identifier);
        return true;
      });
      return [...nH3sToAdd, ...nH3Collection];
    }
    return nH3Collection;
  }
}
