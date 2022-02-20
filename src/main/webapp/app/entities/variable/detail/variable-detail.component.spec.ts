import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { VariableDetailComponent } from './variable-detail.component';

describe('Variable Management Detail Component', () => {
  let comp: VariableDetailComponent;
  let fixture: ComponentFixture<VariableDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [VariableDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ variable: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(VariableDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(VariableDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load variable on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.variable).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
