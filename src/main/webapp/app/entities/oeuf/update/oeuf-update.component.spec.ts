import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { OeufService } from '../service/oeuf.service';
import { IOeuf, Oeuf } from '../oeuf.model';
import { IVariable } from 'app/entities/variable/variable.model';
import { VariableService } from 'app/entities/variable/service/variable.service';

import { OeufUpdateComponent } from './oeuf-update.component';

describe('Oeuf Management Update Component', () => {
  let comp: OeufUpdateComponent;
  let fixture: ComponentFixture<OeufUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let oeufService: OeufService;
  let variableService: VariableService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [OeufUpdateComponent],
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
      .overrideTemplate(OeufUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(OeufUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    oeufService = TestBed.inject(OeufService);
    variableService = TestBed.inject(VariableService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call variable query and add missing value', () => {
      const oeuf: IOeuf = { id: 456 };
      const variable: IVariable = { id: 21295 };
      oeuf.variable = variable;

      const variableCollection: IVariable[] = [{ id: 2209 }];
      jest.spyOn(variableService, 'query').mockReturnValue(of(new HttpResponse({ body: variableCollection })));
      const expectedCollection: IVariable[] = [variable, ...variableCollection];
      jest.spyOn(variableService, 'addVariableToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ oeuf });
      comp.ngOnInit();

      expect(variableService.query).toHaveBeenCalled();
      expect(variableService.addVariableToCollectionIfMissing).toHaveBeenCalledWith(variableCollection, variable);
      expect(comp.variablesCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const oeuf: IOeuf = { id: 456 };
      const variable: IVariable = { id: 99242 };
      oeuf.variable = variable;

      activatedRoute.data = of({ oeuf });
      comp.ngOnInit();

      expect(comp.editForm.value).toEqual(expect.objectContaining(oeuf));
      expect(comp.variablesCollection).toContain(variable);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Oeuf>>();
      const oeuf = { id: 123 };
      jest.spyOn(oeufService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ oeuf });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: oeuf }));
      saveSubject.complete();

      // THEN
      expect(comp.previousState).toHaveBeenCalled();
      expect(oeufService.update).toHaveBeenCalledWith(oeuf);
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Oeuf>>();
      const oeuf = new Oeuf();
      jest.spyOn(oeufService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ oeuf });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: oeuf }));
      saveSubject.complete();

      // THEN
      expect(oeufService.create).toHaveBeenCalledWith(oeuf);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Oeuf>>();
      const oeuf = { id: 123 };
      jest.spyOn(oeufService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ oeuf });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(oeufService.update).toHaveBeenCalledWith(oeuf);
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
