import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { Co2Service } from '../service/co-2.service';
import { ICo2, Co2 } from '../co-2.model';
import { IVariable } from 'app/entities/variable/variable.model';
import { VariableService } from 'app/entities/variable/service/variable.service';

import { Co2UpdateComponent } from './co-2-update.component';

describe('Co2 Management Update Component', () => {
  let comp: Co2UpdateComponent;
  let fixture: ComponentFixture<Co2UpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let co2Service: Co2Service;
  let variableService: VariableService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [Co2UpdateComponent],
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
      .overrideTemplate(Co2UpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(Co2UpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    co2Service = TestBed.inject(Co2Service);
    variableService = TestBed.inject(VariableService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call variable query and add missing value', () => {
      const co2: ICo2 = { id: 456 };
      const variable: IVariable = { id: 71233 };
      co2.variable = variable;

      const variableCollection: IVariable[] = [{ id: 47530 }];
      jest.spyOn(variableService, 'query').mockReturnValue(of(new HttpResponse({ body: variableCollection })));
      const expectedCollection: IVariable[] = [variable, ...variableCollection];
      jest.spyOn(variableService, 'addVariableToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ co2 });
      comp.ngOnInit();

      expect(variableService.query).toHaveBeenCalled();
      expect(variableService.addVariableToCollectionIfMissing).toHaveBeenCalledWith(variableCollection, variable);
      expect(comp.variablesCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const co2: ICo2 = { id: 456 };
      const variable: IVariable = { id: 18083 };
      co2.variable = variable;

      activatedRoute.data = of({ co2 });
      comp.ngOnInit();

      expect(comp.editForm.value).toEqual(expect.objectContaining(co2));
      expect(comp.variablesCollection).toContain(variable);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Co2>>();
      const co2 = { id: 123 };
      jest.spyOn(co2Service, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ co2 });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: co2 }));
      saveSubject.complete();

      // THEN
      expect(comp.previousState).toHaveBeenCalled();
      expect(co2Service.update).toHaveBeenCalledWith(co2);
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Co2>>();
      const co2 = new Co2();
      jest.spyOn(co2Service, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ co2 });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: co2 }));
      saveSubject.complete();

      // THEN
      expect(co2Service.create).toHaveBeenCalledWith(co2);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Co2>>();
      const co2 = { id: 123 };
      jest.spyOn(co2Service, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ co2 });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(co2Service.update).toHaveBeenCalledWith(co2);
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
