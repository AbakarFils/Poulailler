import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { LampeService } from '../service/lampe.service';
import { ILampe, Lampe } from '../lampe.model';
import { IEquipement } from 'app/entities/equipement/equipement.model';
import { EquipementService } from 'app/entities/equipement/service/equipement.service';

import { LampeUpdateComponent } from './lampe-update.component';

describe('Lampe Management Update Component', () => {
  let comp: LampeUpdateComponent;
  let fixture: ComponentFixture<LampeUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let lampeService: LampeService;
  let equipementService: EquipementService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [LampeUpdateComponent],
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
      .overrideTemplate(LampeUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(LampeUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    lampeService = TestBed.inject(LampeService);
    equipementService = TestBed.inject(EquipementService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call equipement query and add missing value', () => {
      const lampe: ILampe = { id: 456 };
      const equipement: IEquipement = { id: 55151 };
      lampe.equipement = equipement;

      const equipementCollection: IEquipement[] = [{ id: 85596 }];
      jest.spyOn(equipementService, 'query').mockReturnValue(of(new HttpResponse({ body: equipementCollection })));
      const expectedCollection: IEquipement[] = [equipement, ...equipementCollection];
      jest.spyOn(equipementService, 'addEquipementToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ lampe });
      comp.ngOnInit();

      expect(equipementService.query).toHaveBeenCalled();
      expect(equipementService.addEquipementToCollectionIfMissing).toHaveBeenCalledWith(equipementCollection, equipement);
      expect(comp.equipementsCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const lampe: ILampe = { id: 456 };
      const equipement: IEquipement = { id: 74825 };
      lampe.equipement = equipement;

      activatedRoute.data = of({ lampe });
      comp.ngOnInit();

      expect(comp.editForm.value).toEqual(expect.objectContaining(lampe));
      expect(comp.equipementsCollection).toContain(equipement);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Lampe>>();
      const lampe = { id: 123 };
      jest.spyOn(lampeService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ lampe });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: lampe }));
      saveSubject.complete();

      // THEN
      expect(comp.previousState).toHaveBeenCalled();
      expect(lampeService.update).toHaveBeenCalledWith(lampe);
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Lampe>>();
      const lampe = new Lampe();
      jest.spyOn(lampeService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ lampe });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: lampe }));
      saveSubject.complete();

      // THEN
      expect(lampeService.create).toHaveBeenCalledWith(lampe);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Lampe>>();
      const lampe = { id: 123 };
      jest.spyOn(lampeService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ lampe });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(lampeService.update).toHaveBeenCalledWith(lampe);
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
