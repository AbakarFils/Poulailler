import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { VentilateurService } from '../service/ventilateur.service';
import { IVentilateur, Ventilateur } from '../ventilateur.model';

import { VentilateurUpdateComponent } from './ventilateur-update.component';

describe('Ventilateur Management Update Component', () => {
  let comp: VentilateurUpdateComponent;
  let fixture: ComponentFixture<VentilateurUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let ventilateurService: VentilateurService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [VentilateurUpdateComponent],
      providers: [
        FormBuilder,
        {
          provide: ActivatedRoute,
          useValue: {
            params: from([{}]),
          },
        },
      ],
    })
      .overrideTemplate(VentilateurUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(VentilateurUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    ventilateurService = TestBed.inject(VentilateurService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should update editForm', () => {
      const ventilateur: IVentilateur = { id: 456 };

      activatedRoute.data = of({ ventilateur });
      comp.ngOnInit();

      expect(comp.editForm.value).toEqual(expect.objectContaining(ventilateur));
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Ventilateur>>();
      const ventilateur = { id: 123 };
      jest.spyOn(ventilateurService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ ventilateur });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: ventilateur }));
      saveSubject.complete();

      // THEN
      expect(comp.previousState).toHaveBeenCalled();
      expect(ventilateurService.update).toHaveBeenCalledWith(ventilateur);
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Ventilateur>>();
      const ventilateur = new Ventilateur();
      jest.spyOn(ventilateurService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ ventilateur });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: ventilateur }));
      saveSubject.complete();

      // THEN
      expect(ventilateurService.create).toHaveBeenCalledWith(ventilateur);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Ventilateur>>();
      const ventilateur = { id: 123 };
      jest.spyOn(ventilateurService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ ventilateur });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(ventilateurService.update).toHaveBeenCalledWith(ventilateur);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});
