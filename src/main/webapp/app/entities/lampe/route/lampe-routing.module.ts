import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { LampeComponent } from '../list/lampe.component';
import { LampeDetailComponent } from '../detail/lampe-detail.component';
import { LampeUpdateComponent } from '../update/lampe-update.component';
import { LampeRoutingResolveService } from './lampe-routing-resolve.service';

const lampeRoute: Routes = [
  {
    path: '',
    component: LampeComponent,
    data: {
      defaultSort: 'id,asc',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: LampeDetailComponent,
    resolve: {
      lampe: LampeRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: LampeUpdateComponent,
    resolve: {
      lampe: LampeRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: LampeUpdateComponent,
    resolve: {
      lampe: LampeRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(lampeRoute)],
  exports: [RouterModule],
})
export class LampeRoutingModule {}
