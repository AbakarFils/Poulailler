import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { ICo2, Co2 } from '../co-2.model';
import { Co2Service } from '../service/co-2.service';

@Injectable({ providedIn: 'root' })
export class Co2RoutingResolveService implements Resolve<ICo2> {
  constructor(protected service: Co2Service, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<ICo2> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((co2: HttpResponse<Co2>) => {
          if (co2.body) {
            return of(co2.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new Co2());
  }
}
