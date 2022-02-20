import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { DirecteurDetailComponent } from './directeur-detail.component';

describe('Directeur Management Detail Component', () => {
  let comp: DirecteurDetailComponent;
  let fixture: ComponentFixture<DirecteurDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [DirecteurDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ directeur: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(DirecteurDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(DirecteurDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load directeur on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.directeur).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
