import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { EquipementService } from '../service/equipement.service';
import { IEquipement, Equipement } from '../equipement.model';
import { IEmploye } from 'app/entities/employe/employe.model';
import { EmployeService } from 'app/entities/employe/service/employe.service';
import { IDirecteur } from 'app/entities/directeur/directeur.model';
import { DirecteurService } from 'app/entities/directeur/service/directeur.service';

import { EquipementUpdateComponent } from './equipement-update.component';

describe('Equipement Management Update Component', () => {
  let comp: EquipementUpdateComponent;
  let fixture: ComponentFixture<EquipementUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let equipementService: EquipementService;
  let employeService: EmployeService;
  let directeurService: DirecteurService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [EquipementUpdateComponent],
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
      .overrideTemplate(EquipementUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(EquipementUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    equipementService = TestBed.inject(EquipementService);
    employeService = TestBed.inject(EmployeService);
    directeurService = TestBed.inject(DirecteurService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call Employe query and add missing value', () => {
      const equipement: IEquipement = { id: 456 };
      const employes: IEmploye[] = [{ id: 95871 }];
      equipement.employes = employes;

      const employeCollection: IEmploye[] = [{ id: 88938 }];
      jest.spyOn(employeService, 'query').mockReturnValue(of(new HttpResponse({ body: employeCollection })));
      const additionalEmployes = [...employes];
      const expectedCollection: IEmploye[] = [...additionalEmployes, ...employeCollection];
      jest.spyOn(employeService, 'addEmployeToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ equipement });
      comp.ngOnInit();

      expect(employeService.query).toHaveBeenCalled();
      expect(employeService.addEmployeToCollectionIfMissing).toHaveBeenCalledWith(employeCollection, ...additionalEmployes);
      expect(comp.employesSharedCollection).toEqual(expectedCollection);
    });

    it('Should call Directeur query and add missing value', () => {
      const equipement: IEquipement = { id: 456 };
      const gestionnaires: IDirecteur[] = [{ id: 49078 }];
      equipement.gestionnaires = gestionnaires;

      const directeurCollection: IDirecteur[] = [{ id: 88987 }];
      jest.spyOn(directeurService, 'query').mockReturnValue(of(new HttpResponse({ body: directeurCollection })));
      const additionalDirecteurs = [...gestionnaires];
      const expectedCollection: IDirecteur[] = [...additionalDirecteurs, ...directeurCollection];
      jest.spyOn(directeurService, 'addDirecteurToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ equipement });
      comp.ngOnInit();

      expect(directeurService.query).toHaveBeenCalled();
      expect(directeurService.addDirecteurToCollectionIfMissing).toHaveBeenCalledWith(directeurCollection, ...additionalDirecteurs);
      expect(comp.directeursSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const equipement: IEquipement = { id: 456 };
      const employes: IEmploye = { id: 81490 };
      equipement.employes = [employes];
      const gestionnaires: IDirecteur = { id: 34875 };
      equipement.gestionnaires = [gestionnaires];

      activatedRoute.data = of({ equipement });
      comp.ngOnInit();

      expect(comp.editForm.value).toEqual(expect.objectContaining(equipement));
      expect(comp.employesSharedCollection).toContain(employes);
      expect(comp.directeursSharedCollection).toContain(gestionnaires);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Equipement>>();
      const equipement = { id: 123 };
      jest.spyOn(equipementService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ equipement });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: equipement }));
      saveSubject.complete();

      // THEN
      expect(comp.previousState).toHaveBeenCalled();
      expect(equipementService.update).toHaveBeenCalledWith(equipement);
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Equipement>>();
      const equipement = new Equipement();
      jest.spyOn(equipementService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ equipement });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: equipement }));
      saveSubject.complete();

      // THEN
      expect(equipementService.create).toHaveBeenCalledWith(equipement);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Equipement>>();
      const equipement = { id: 123 };
      jest.spyOn(equipementService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ equipement });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(equipementService.update).toHaveBeenCalledWith(equipement);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Tracking relationships identifiers', () => {
    describe('trackEmployeById', () => {
      it('Should return tracked Employe primary key', () => {
        const entity = { id: 123 };
        const trackResult = comp.trackEmployeById(0, entity);
        expect(trackResult).toEqual(entity.id);
      });
    });

    describe('trackDirecteurById', () => {
      it('Should return tracked Directeur primary key', () => {
        const entity = { id: 123 };
        const trackResult = comp.trackDirecteurById(0, entity);
        expect(trackResult).toEqual(entity.id);
      });
    });
  });

  describe('Getting selected relationships', () => {
    describe('getSelectedEmploye', () => {
      it('Should return option if no Employe is selected', () => {
        const option = { id: 123 };
        const result = comp.getSelectedEmploye(option);
        expect(result === option).toEqual(true);
      });

      it('Should return selected Employe for according option', () => {
        const option = { id: 123 };
        const selected = { id: 123 };
        const selected2 = { id: 456 };
        const result = comp.getSelectedEmploye(option, [selected2, selected]);
        expect(result === selected).toEqual(true);
        expect(result === selected2).toEqual(false);
        expect(result === option).toEqual(false);
      });

      it('Should return option if this Employe is not selected', () => {
        const option = { id: 123 };
        const selected = { id: 456 };
        const result = comp.getSelectedEmploye(option, [selected]);
        expect(result === option).toEqual(true);
        expect(result === selected).toEqual(false);
      });
    });

    describe('getSelectedDirecteur', () => {
      it('Should return option if no Directeur is selected', () => {
        const option = { id: 123 };
        const result = comp.getSelectedDirecteur(option);
        expect(result === option).toEqual(true);
      });

      it('Should return selected Directeur for according option', () => {
        const option = { id: 123 };
        const selected = { id: 123 };
        const selected2 = { id: 456 };
        const result = comp.getSelectedDirecteur(option, [selected2, selected]);
        expect(result === selected).toEqual(true);
        expect(result === selected2).toEqual(false);
        expect(result === option).toEqual(false);
      });

      it('Should return option if this Directeur is not selected', () => {
        const option = { id: 123 };
        const selected = { id: 456 };
        const result = comp.getSelectedDirecteur(option, [selected]);
        expect(result === option).toEqual(true);
        expect(result === selected).toEqual(false);
      });
    });
  });
});
