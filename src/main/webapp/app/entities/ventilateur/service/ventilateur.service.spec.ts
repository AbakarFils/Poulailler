import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IVentilateur, Ventilateur } from '../ventilateur.model';

import { VentilateurService } from './ventilateur.service';

describe('Ventilateur Service', () => {
  let service: VentilateurService;
  let httpMock: HttpTestingController;
  let elemDefault: IVentilateur;
  let expectedResult: IVentilateur | IVentilateur[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(VentilateurService);
    httpMock = TestBed.inject(HttpTestingController);

    elemDefault = {
      id: 0,
      libelle: 'AAAAAAA',
      vitesse: 0,
    };
  });

  describe('Service methods', () => {
    it('should find an element', () => {
      const returnedFromService = Object.assign({}, elemDefault);

      service.find(123).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(elemDefault);
    });

    it('should create a Ventilateur', () => {
      const returnedFromService = Object.assign(
        {
          id: 0,
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.create(new Ventilateur()).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Ventilateur', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          libelle: 'BBBBBB',
          vitesse: 1,
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.update(expected).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Ventilateur', () => {
      const patchObject = Object.assign({}, new Ventilateur());

      const returnedFromService = Object.assign(patchObject, elemDefault);

      const expected = Object.assign({}, returnedFromService);

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Ventilateur', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          libelle: 'BBBBBB',
          vitesse: 1,
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toContainEqual(expected);
    });

    it('should delete a Ventilateur', () => {
      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult);
    });

    describe('addVentilateurToCollectionIfMissing', () => {
      it('should add a Ventilateur to an empty array', () => {
        const ventilateur: IVentilateur = { id: 123 };
        expectedResult = service.addVentilateurToCollectionIfMissing([], ventilateur);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(ventilateur);
      });

      it('should not add a Ventilateur to an array that contains it', () => {
        const ventilateur: IVentilateur = { id: 123 };
        const ventilateurCollection: IVentilateur[] = [
          {
            ...ventilateur,
          },
          { id: 456 },
        ];
        expectedResult = service.addVentilateurToCollectionIfMissing(ventilateurCollection, ventilateur);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Ventilateur to an array that doesn't contain it", () => {
        const ventilateur: IVentilateur = { id: 123 };
        const ventilateurCollection: IVentilateur[] = [{ id: 456 }];
        expectedResult = service.addVentilateurToCollectionIfMissing(ventilateurCollection, ventilateur);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(ventilateur);
      });

      it('should add only unique Ventilateur to an array', () => {
        const ventilateurArray: IVentilateur[] = [{ id: 123 }, { id: 456 }, { id: 63416 }];
        const ventilateurCollection: IVentilateur[] = [{ id: 123 }];
        expectedResult = service.addVentilateurToCollectionIfMissing(ventilateurCollection, ...ventilateurArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const ventilateur: IVentilateur = { id: 123 };
        const ventilateur2: IVentilateur = { id: 456 };
        expectedResult = service.addVentilateurToCollectionIfMissing([], ventilateur, ventilateur2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(ventilateur);
        expect(expectedResult).toContain(ventilateur2);
      });

      it('should accept null and undefined values', () => {
        const ventilateur: IVentilateur = { id: 123 };
        expectedResult = service.addVentilateurToCollectionIfMissing([], null, ventilateur, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(ventilateur);
      });

      it('should return initial array if no Ventilateur is added', () => {
        const ventilateurCollection: IVentilateur[] = [{ id: 123 }];
        expectedResult = service.addVentilateurToCollectionIfMissing(ventilateurCollection, undefined, null);
        expect(expectedResult).toEqual(ventilateurCollection);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
