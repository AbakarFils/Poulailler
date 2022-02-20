import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { ILampe, getLampeIdentifier } from '../lampe.model';

export type EntityResponseType = HttpResponse<ILampe>;
export type EntityArrayResponseType = HttpResponse<ILampe[]>;

@Injectable({ providedIn: 'root' })
export class LampeService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/lampes');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(lampe: ILampe): Observable<EntityResponseType> {
    return this.http.post<ILampe>(this.resourceUrl, lampe, { observe: 'response' });
  }

  update(lampe: ILampe): Observable<EntityResponseType> {
    return this.http.put<ILampe>(`${this.resourceUrl}/${getLampeIdentifier(lampe) as number}`, lampe, { observe: 'response' });
  }

  partialUpdate(lampe: ILampe): Observable<EntityResponseType> {
    return this.http.patch<ILampe>(`${this.resourceUrl}/${getLampeIdentifier(lampe) as number}`, lampe, { observe: 'response' });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<ILampe>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<ILampe[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addLampeToCollectionIfMissing(lampeCollection: ILampe[], ...lampesToCheck: (ILampe | null | undefined)[]): ILampe[] {
    const lampes: ILampe[] = lampesToCheck.filter(isPresent);
    if (lampes.length > 0) {
      const lampeCollectionIdentifiers = lampeCollection.map(lampeItem => getLampeIdentifier(lampeItem)!);
      const lampesToAdd = lampes.filter(lampeItem => {
        const lampeIdentifier = getLampeIdentifier(lampeItem);
        if (lampeIdentifier == null || lampeCollectionIdentifiers.includes(lampeIdentifier)) {
          return false;
        }
        lampeCollectionIdentifiers.push(lampeIdentifier);
        return true;
      });
      return [...lampesToAdd, ...lampeCollection];
    }
    return lampeCollection;
  }
}
