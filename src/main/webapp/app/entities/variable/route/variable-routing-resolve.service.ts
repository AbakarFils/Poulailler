import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IVariable, Variable } from '../variable.model';
import { VariableService } from '../service/variable.service';

@Injectable({ providedIn: 'root' })
export class VariableRoutingResolveService implements Resolve<IVariable> {
  constructor(protected service: VariableService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IVariable> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((variable: HttpResponse<Variable>) => {
          if (variable.body) {
            return of(variable.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new Variable());
  }
}
