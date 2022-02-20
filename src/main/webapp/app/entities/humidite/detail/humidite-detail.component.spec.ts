import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { HumiditeDetailComponent } from './humidite-detail.component';

describe('Humidite Management Detail Component', () => {
  let comp: HumiditeDetailComponent;
  let fixture: ComponentFixture<HumiditeDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [HumiditeDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ humidite: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(HumiditeDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(HumiditeDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load humidite on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.humidite).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
