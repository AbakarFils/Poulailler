import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { NH3Service } from '../service/nh-3.service';
import { INH3, NH3 } from '../nh-3.model';
import { IVariable } from 'app/entities/variable/variable.model';
import { VariableService } from 'app/entities/variable/service/variable.service';

import { NH3UpdateComponent } from './nh-3-update.component';

describe('NH3 Management Update Component', () => {
  let comp: NH3UpdateComponent;
  let fixture: ComponentFixture<NH3UpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let nH3Service: NH3Service;
  let variableService: VariableService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [NH3UpdateComponent],
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
      .overrideTemplate(NH3UpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(NH3UpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    nH3Service = TestBed.inject(NH3Service);
    variableService = TestBed.inject(VariableService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call variable query and add missing value', () => {
      const nH3: INH3 = { id: 456 };
      const variable: IVariable = { id: 22533 };
      nH3.variable = variable;

      const variableCollection: IVariable[] = [{ id: 33671 }];
      jest.spyOn(variableService, 'query').mockReturnValue(of(new HttpResponse({ body: variableCollection })));
      const expectedCollection: IVariable[] = [variable, ...variableCollection];
      jest.spyOn(variableService, 'addVariableToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ nH3 });
      comp.ngOnInit();

      expect(variableService.query).toHaveBeenCalled();
      expect(variableService.addVariableToCollectionIfMissing).toHaveBeenCalledWith(variableCollection, variable);
      expect(comp.variablesCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const nH3: INH3 = { id: 456 };
      const variable: IVariable = { id: 54905 };
      nH3.variable = variable;

      activatedRoute.data = of({ nH3 });
      comp.ngOnInit();

      expect(comp.editForm.value).toEqual(expect.objectContaining(nH3));
      expect(comp.variablesCollection).toContain(variable);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<NH3>>();
      const nH3 = { id: 123 };
      jest.spyOn(nH3Service, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ nH3 });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: nH3 }));
      saveSubject.complete();

      // THEN
      expect(comp.previousState).toHaveBeenCalled();
      expect(nH3Service.update).toHaveBeenCalledWith(nH3);
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<NH3>>();
      const nH3 = new NH3();
      jest.spyOn(nH3Service, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ nH3 });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: nH3 }));
      saveSubject.complete();

      // THEN
      expect(nH3Service.create).toHaveBeenCalledWith(nH3);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<NH3>>();
      const nH3 = { id: 123 };
      jest.spyOn(nH3Service, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ nH3 });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(nH3Service.update).toHaveBeenCalledWith(nH3);
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
