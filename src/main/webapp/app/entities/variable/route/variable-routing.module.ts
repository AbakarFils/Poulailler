import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { VariableComponent } from '../list/variable.component';
import { VariableDetailComponent } from '../detail/variable-detail.component';
import { VariableRoutingResolveService } from './variable-routing-resolve.service';

const variableRoute: Routes = [
  {
    path: '',
    component: VariableComponent,
    data: {
      defaultSort: 'id,asc',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: VariableDetailComponent,
    resolve: {
      variable: VariableRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(variableRoute)],
  exports: [RouterModule],
})
export class VariableRoutingModule {}
