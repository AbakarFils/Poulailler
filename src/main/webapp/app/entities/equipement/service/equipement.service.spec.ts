import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import dayjs from 'dayjs/esm';

import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { TypeEquipement } from 'app/entities/enumerations/type-equipement.model';
import { IEquipement, Equipement } from '../equipement.model';

import { EquipementService } from './equipement.service';

describe('Equipement Service', () => {
  let service: EquipementService;
  let httpMock: HttpTestingController;
  let elemDefault: IEquipement;
  let expectedResult: IEquipement | IEquipement[] | boolean | null;
  let currentDate: dayjs.Dayjs;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(EquipementService);
    httpMock = TestBed.inject(HttpTestingController);
    currentDate = dayjs();

    elemDefault = {
      id: 0,
      statut: false,
      refArduino: 'AAAAAAA',
      dateCrea: currentDate,
      libelle: 'AAAAAAA',
      marque: 'AAAAAAA',
      type: TypeEquipement.CAPTEUR,
    };
  });

  describe('Service methods', () => {
    it('should find an element', () => {
      const returnedFromService = Object.assign(
        {
          dateCrea: currentDate.format(DATE_TIME_FORMAT),
        },
        elemDefault
      );

      service.find(123).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(elemDefault);
    });

    it('should create a Equipement', () => {
      const returnedFromService = Object.assign(
        {
          id: 0,
          dateCrea: currentDate.format(DATE_TIME_FORMAT),
        },
        elemDefault
      );

      const expected = Object.assign(
        {
          dateCrea: currentDate,
        },
        returnedFromService
      );

      service.create(new Equipement()).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Equipement', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          statut: true,
          refArduino: 'BBBBBB',
          dateCrea: currentDate.format(DATE_TIME_FORMAT),
          libelle: 'BBBBBB',
          marque: 'BBBBBB',
          type: 'BBBBBB',
        },
        elemDefault
      );

      const expected = Object.assign(
        {
          dateCrea: currentDate,
        },
        returnedFromService
      );

      service.update(expected).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Equipement', () => {
      const patchObject = Object.assign(
        {
          statut: true,
          libelle: 'BBBBBB',
          marque: 'BBBBBB',
        },
        new Equipement()
      );

      const returnedFromService = Object.assign(patchObject, elemDefault);

      const expected = Object.assign(
        {
          dateCrea: currentDate,
        },
        returnedFromService
      );

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Equipement', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          statut: true,
          refArduino: 'BBBBBB',
          dateCrea: currentDate.format(DATE_TIME_FORMAT),
          libelle: 'BBBBBB',
          marque: 'BBBBBB',
          type: 'BBBBBB',
        },
        elemDefault
      );

      const expected = Object.assign(
        {
          dateCrea: currentDate,
        },
        returnedFromService
      );

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toContainEqual(expected);
    });

    it('should delete a Equipement', () => {
      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult);
    });

    describe('addEquipementToCollectionIfMissing', () => {
      it('should add a Equipement to an empty array', () => {
        const equipement: IEquipement = { id: 123 };
        expectedResult = service.addEquipementToCollectionIfMissing([], equipement);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(equipement);
      });

      it('should not add a Equipement to an array that contains it', () => {
        const equipement: IEquipement = { id: 123 };
        const equipementCollection: IEquipement[] = [
          {
            ...equipement,
          },
          { id: 456 },
        ];
        expectedResult = service.addEquipementToCollectionIfMissing(equipementCollection, equipement);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Equipement to an array that doesn't contain it", () => {
        const equipement: IEquipement = { id: 123 };
        const equipementCollection: IEquipement[] = [{ id: 456 }];
        expectedResult = service.addEquipementToCollectionIfMissing(equipementCollection, equipement);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(equipement);
      });

      it('should add only unique Equipement to an array', () => {
        const equipementArray: IEquipement[] = [{ id: 123 }, { id: 456 }, { id: 21849 }];
        const equipementCollection: IEquipement[] = [{ id: 123 }];
        expectedResult = service.addEquipementToCollectionIfMissing(equipementCollection, ...equipementArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const equipement: IEquipement = { id: 123 };
        const equipement2: IEquipement = { id: 456 };
        expectedResult = service.addEquipementToCollectionIfMissing([], equipement, equipement2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(equipement);
        expect(expectedResult).toContain(equipement2);
      });

      it('should accept null and undefined values', () => {
        const equipement: IEquipement = { id: 123 };
        expectedResult = service.addEquipementToCollectionIfMissing([], null, equipement, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(equipement);
      });

      it('should return initial array if no Equipement is added', () => {
        const equipementCollection: IEquipement[] = [{ id: 123 }];
        expectedResult = service.addEquipementToCollectionIfMissing(equipementCollection, undefined, null);
        expect(expectedResult).toEqual(equipementCollection);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
