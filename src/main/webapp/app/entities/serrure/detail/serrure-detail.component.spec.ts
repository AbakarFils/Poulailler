import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { SerrureDetailComponent } from './serrure-detail.component';

describe('Serrure Management Detail Component', () => {
  let comp: SerrureDetailComponent;
  let fixture: ComponentFixture<SerrureDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [SerrureDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ serrure: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(SerrureDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(SerrureDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load serrure on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.serrure).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
