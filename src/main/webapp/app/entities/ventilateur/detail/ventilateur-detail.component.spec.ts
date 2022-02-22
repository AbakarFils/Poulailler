import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { VentilateurDetailComponent } from './ventilateur-detail.component';

describe('Ventilateur Management Detail Component', () => {
  let comp: VentilateurDetailComponent;
  let fixture: ComponentFixture<VentilateurDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [VentilateurDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ ventilateur: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(VentilateurDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(VentilateurDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load ventilateur on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.ventilateur).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
