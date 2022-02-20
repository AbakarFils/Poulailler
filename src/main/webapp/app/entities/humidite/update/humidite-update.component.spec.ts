import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { HumiditeService } from '../service/humidite.service';
import { IHumidite, Humidite } from '../humidite.model';
import { IVariable } from 'app/entities/variable/variable.model';
import { VariableService } from 'app/entities/variable/service/variable.service';

import { HumiditeUpdateComponent } from './humidite-update.component';

describe('Humidite Management Update Component', () => {
  let comp: HumiditeUpdateComponent;
  let fixture: ComponentFixture<HumiditeUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let humiditeService: HumiditeService;
  let variableService: VariableService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [HumiditeUpdateComponent],
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
      .overrideTemplate(HumiditeUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(HumiditeUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    humiditeService = TestBed.inject(HumiditeService);
    variableService = TestBed.inject(VariableService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call variable query and add missing value', () => {
      const humidite: IHumidite = { id: 456 };
      const variable: IVariable = { id: 5367 };
      humidite.variable = variable;

      const variableCollection: IVariable[] = [{ id: 53752 }];
      jest.spyOn(variableService, 'query').mockReturnValue(of(new HttpResponse({ body: variableCollection })));
      const expectedCollection: IVariable[] = [variable, ...variableCollection];
      jest.spyOn(variableService, 'addVariableToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ humidite });
      comp.ngOnInit();

      expect(variableService.query).toHaveBeenCalled();
      expect(variableService.addVariableToCollectionIfMissing).toHaveBeenCalledWith(variableCollection, variable);
      expect(comp.variablesCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const humidite: IHumidite = { id: 456 };
      const variable: IVariable = { id: 67850 };
      humidite.variable = variable;

      activatedRoute.data = of({ humidite });
      comp.ngOnInit();

      expect(comp.editForm.value).toEqual(expect.objectContaining(humidite));
      expect(comp.variablesCollection).toContain(variable);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Humidite>>();
      const humidite = { id: 123 };
      jest.spyOn(humiditeService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ humidite });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: humidite }));
      saveSubject.complete();

      // THEN
      expect(comp.previousState).toHaveBeenCalled();
      expect(humiditeService.update).toHaveBeenCalledWith(humidite);
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Humidite>>();
      const humidite = new Humidite();
      jest.spyOn(humiditeService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ humidite });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: humidite }));
      saveSubject.complete();

      // THEN
      expect(humiditeService.create).toHaveBeenCalledWith(humidite);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Humidite>>();
      const humidite = { id: 123 };
      jest.spyOn(humiditeService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ humidite });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(humiditeService.update).toHaveBeenCalledWith(humidite);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Tracking relationships identifiers', () => {
    describe('trackVariableById', () => {
      it('Should return tracked Variable primary key', () => {
        const entity = { id: 123 };
        const trackResult = comp.trackVariableById(0, entity);
        expect(trackResult).toEqual(entity.id);
      });
    });
  });
});
