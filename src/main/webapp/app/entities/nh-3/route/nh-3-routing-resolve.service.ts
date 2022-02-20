import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { INH3, NH3 } from '../nh-3.model';
import { NH3Service } from '../service/nh-3.service';

@Injectable({ providedIn: 'root' })
export class NH3RoutingResolveService implements Resolve<INH3> {
  constructor(protected service: NH3Service, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<INH3> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((nH3: HttpResponse<NH3>) => {
          if (nH3.body) {
            return of(nH3.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new NH3());
  }
}
