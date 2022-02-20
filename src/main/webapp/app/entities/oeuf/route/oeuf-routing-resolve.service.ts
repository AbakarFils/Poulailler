import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IOeuf, Oeuf } from '../oeuf.model';
import { OeufService } from '../service/oeuf.service';

@Injectable({ providedIn: 'root' })
export class OeufRoutingResolveService implements Resolve<IOeuf> {
  constructor(protected service: OeufService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IOeuf> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((oeuf: HttpResponse<Oeuf>) => {
          if (oeuf.body) {
            return of(oeuf.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new Oeuf());
  }
}
