import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { NH3DetailComponent } from './nh-3-detail.component';

describe('NH3 Management Detail Component', () => {
  let comp: NH3DetailComponent;
  let fixture: ComponentFixture<NH3DetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [NH3DetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ nH3: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(NH3DetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(NH3DetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load nH3 on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.nH3).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
