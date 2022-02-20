import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { SerrureComponent } from '../list/serrure.component';
import { SerrureDetailComponent } from '../detail/serrure-detail.component';
import { SerrureUpdateComponent } from '../update/serrure-update.component';
import { SerrureRoutingResolveService } from './serrure-routing-resolve.service';

const serrureRoute: Routes = [
  {
    path: '',
    component: SerrureComponent,
    data: {
      defaultSort: 'id,asc',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: SerrureDetailComponent,
    resolve: {
      serrure: SerrureRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: SerrureUpdateComponent,
    resolve: {
      serrure: SerrureRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: SerrureUpdateComponent,
    resolve: {
      serrure: SerrureRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(serrureRoute)],
  exports: [RouterModule],
})
export class SerrureRoutingModule {}
