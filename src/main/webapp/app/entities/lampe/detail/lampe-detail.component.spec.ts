import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { LampeDetailComponent } from './lampe-detail.component';

describe('Lampe Management Detail Component', () => {
  let comp: LampeDetailComponent;
  let fixture: ComponentFixture<LampeDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [LampeDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ lampe: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(LampeDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(LampeDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load lampe on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.lampe).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
