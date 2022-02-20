import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { SerrureService } from '../service/serrure.service';
import { ISerrure, Serrure } from '../serrure.model';
import { IEquipement } from 'app/entities/equipement/equipement.model';
import { EquipementService } from 'app/entities/equipement/service/equipement.service';

import { SerrureUpdateComponent } from './serrure-update.component';

describe('Serrure Management Update Component', () => {
  let comp: SerrureUpdateComponent;
  let fixture: ComponentFixture<SerrureUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let serrureService: SerrureService;
  let equipementService: EquipementService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [SerrureUpdateComponent],
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
      .overrideTemplate(SerrureUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(SerrureUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    serrureService = TestBed.inject(SerrureService);
    equipementService = TestBed.inject(EquipementService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call equipement query and add missing value', () => {
      const serrure: ISerrure = { id: 456 };
      const equipement: IEquipement = { id: 17724 };
      serrure.equipement = equipement;

      const equipementCollection: IEquipement[] = [{ id: 25863 }];
      jest.spyOn(equipementService, 'query').mockReturnValue(of(new HttpResponse({ body: equipementCollection })));
      const expectedCollection: IEquipement[] = [equipement, ...equipementCollection];
      jest.spyOn(equipementService, 'addEquipementToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ serrure });
      comp.ngOnInit();

      expect(equipementService.query).toHaveBeenCalled();
      expect(equipementService.addEquipementToCollectionIfMissing).toHaveBeenCalledWith(equipementCollection, equipement);
      expect(comp.equipementsCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const serrure: ISerrure = { id: 456 };
      const equipement: IEquipement = { id: 33783 };
      serrure.equipement = equipement;

      activatedRoute.data = of({ serrure });
      comp.ngOnInit();

      expect(comp.editForm.value).toEqual(expect.objectContaining(serrure));
      expect(comp.equipementsCollection).toContain(equipement);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Serrure>>();
      const serrure = { id: 123 };
      jest.spyOn(serrureService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ serrure });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: serrure }));
      saveSubject.complete();

      // THEN
      expect(comp.previousState).toHaveBeenCalled();
      expect(serrureService.update).toHaveBeenCalledWith(serrure);
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Serrure>>();
      const serrure = new Serrure();
      jest.spyOn(serrureService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ serrure });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: serrure }));
      saveSubject.complete();

      // THEN
      expect(serrureService.create).toHaveBeenCalledWith(serrure);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Serrure>>();
      const serrure = { id: 123 };
      jest.spyOn(serrureService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ serrure });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(serrureService.update).toHaveBeenCalledWith(serrure);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Tracking relationships identifiers', () => {
    describe('trackEquipementById', () => {
      it('Should return tracked Equipement primary key', () => {
        const entity = { id: 123 };
        const trackResult = comp.trackEquipementById(0, entity);
        expect(trackResult).toEqual(entity.id);
      });
    });
  });
});
