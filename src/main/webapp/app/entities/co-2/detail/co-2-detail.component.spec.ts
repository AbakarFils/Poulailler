import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { Co2DetailComponent } from './co-2-detail.component';

describe('Co2 Management Detail Component', () => {
  let comp: Co2DetailComponent;
  let fixture: ComponentFixture<Co2DetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [Co2DetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ co2: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(Co2DetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(Co2DetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load co2 on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.co2).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
