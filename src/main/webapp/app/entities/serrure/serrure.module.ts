import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { SerrureComponent } from './list/serrure.component';
import { SerrureDetailComponent } from './detail/serrure-detail.component';
import { SerrureUpdateComponent } from './update/serrure-update.component';
import { SerrureDeleteDialogComponent } from './delete/serrure-delete-dialog.component';
import { SerrureRoutingModule } from './route/serrure-routing.module';

@NgModule({
  imports: [SharedModule, SerrureRoutingModule],
  declarations: [SerrureComponent, SerrureDetailComponent, SerrureUpdateComponent, SerrureDeleteDialogComponent],
  entryComponents: [SerrureDeleteDialogComponent],
})
export class SerrureModule {}
