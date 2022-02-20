import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { INH3 } from '../nh-3.model';
import { NH3Service } from '../service/nh-3.service';

@Component({
  templateUrl: './nh-3-delete-dialog.component.html',
})
export class NH3DeleteDialogComponent {
  nH3?: INH3;

  constructor(protected nH3Service: NH3Service, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.nH3Service.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
