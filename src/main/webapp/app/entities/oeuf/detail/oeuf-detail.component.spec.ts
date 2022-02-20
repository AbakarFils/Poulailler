import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { OeufDetailComponent } from './oeuf-detail.component';

describe('Oeuf Management Detail Component', () => {
  let comp: OeufDetailComponent;
  let fixture: ComponentFixture<OeufDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [OeufDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ oeuf: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(OeufDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(OeufDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load oeuf on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.oeuf).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
