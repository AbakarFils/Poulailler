import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { NH3Component } from './list/nh-3.component';
import { NH3DetailComponent } from './detail/nh-3-detail.component';
import { NH3UpdateComponent } from './update/nh-3-update.component';
import { NH3DeleteDialogComponent } from './delete/nh-3-delete-dialog.component';
import { NH3RoutingModule } from './route/nh-3-routing.module';

@NgModule({
  imports: [SharedModule, NH3RoutingModule],
  declarations: [NH3Component, NH3DetailComponent, NH3UpdateComponent, NH3DeleteDialogComponent],
  entryComponents: [NH3DeleteDialogComponent],
})
export class NH3Module {}
