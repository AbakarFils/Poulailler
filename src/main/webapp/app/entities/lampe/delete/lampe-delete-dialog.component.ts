import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { ILampe } from '../lampe.model';
import { LampeService } from '../service/lampe.service';

@Component({
  templateUrl: './lampe-delete-dialog.component.html',
})
export class LampeDeleteDialogComponent {
  lampe?: ILampe;

  constructor(protected lampeService: LampeService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.lampeService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
