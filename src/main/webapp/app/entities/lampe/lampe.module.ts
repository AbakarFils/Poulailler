import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { LampeComponent } from './list/lampe.component';
import { LampeDetailComponent } from './detail/lampe-detail.component';
import { LampeUpdateComponent } from './update/lampe-update.component';
import { LampeDeleteDialogComponent } from './delete/lampe-delete-dialog.component';
import { LampeRoutingModule } from './route/lampe-routing.module';

@NgModule({
  imports: [SharedModule, LampeRoutingModule],
  declarations: [LampeComponent, LampeDetailComponent, LampeUpdateComponent, LampeDeleteDialogComponent],
  entryComponents: [LampeDeleteDialogComponent],
})
export class LampeModule {}
