import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { ISerrure } from '../serrure.model';
import { SerrureService } from '../service/serrure.service';

@Component({
  templateUrl: './serrure-delete-dialog.component.html',
})
export class SerrureDeleteDialogComponent {
  serrure?: ISerrure;

  constructor(protected serrureService: SerrureService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.serrureService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
