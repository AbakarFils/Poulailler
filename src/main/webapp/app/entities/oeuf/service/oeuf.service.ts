import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IOeuf, getOeufIdentifier } from '../oeuf.model';

export type EntityResponseType = HttpResponse<IOeuf>;
export type EntityArrayResponseType = HttpResponse<IOeuf[]>;

@Injectable({ providedIn: 'root' })
export class OeufService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/oeufs');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(oeuf: IOeuf): Observable<EntityResponseType> {
    return this.http.post<IOeuf>(this.resourceUrl, oeuf, { observe: 'response' });
  }

  update(oeuf: IOeuf): Observable<EntityResponseType> {
    return this.http.put<IOeuf>(`${this.resourceUrl}/${getOeufIdentifier(oeuf) as number}`, oeuf, { observe: 'response' });
  }

  partialUpdate(oeuf: IOeuf): Observable<EntityResponseType> {
    return this.http.patch<IOeuf>(`${this.resourceUrl}/${getOeufIdentifier(oeuf) as number}`, oeuf, { observe: 'response' });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IOeuf>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IOeuf[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addOeufToCollectionIfMissing(oeufCollection: IOeuf[], ...oeufsToCheck: (IOeuf | null | undefined)[]): IOeuf[] {
    const oeufs: IOeuf[] = oeufsToCheck.filter(isPresent);
    if (oeufs.length > 0) {
      const oeufCollectionIdentifiers = oeufCollection.map(oeufItem => getOeufIdentifier(oeufItem)!);
      const oeufsToAdd = oeufs.filter(oeufItem => {
        const oeufIdentifier = getOeufIdentifier(oeufItem);
        if (oeufIdentifier == null || oeufCollectionIdentifiers.includes(oeufIdentifier)) {
          return false;
        }
        oeufCollectionIdentifiers.push(oeufIdentifier);
        return true;
      });
      return [...oeufsToAdd, ...oeufCollection];
    }
    return oeufCollection;
  }
}
