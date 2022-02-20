import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IHumidite, Humidite } from '../humidite.model';

import { HumiditeService } from './humidite.service';

describe('Humidite Service', () => {
  let service: HumiditeService;
  let httpMock: HttpTestingController;
  let elemDefault: IHumidite;
  let expectedResult: IHumidite | IHumidite[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(HumiditeService);
    httpMock = TestBed.inject(HttpTestingController);

    elemDefault = {
      id: 0,
      niveau: 0,
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

    it('should create a Humidite', () => {
      const returnedFromService = Object.assign(
        {
          id: 0,
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.create(new Humidite()).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Humidite', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          niveau: 1,
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.update(expected).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Humidite', () => {
      const patchObject = Object.assign(
        {
          niveau: 1,
        },
        new Humidite()
      );

      const returnedFromService = Object.assign(patchObject, elemDefault);

      const expected = Object.assign({}, returnedFromService);

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Humidite', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          niveau: 1,
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

    it('should delete a Humidite', () => {
      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult);
    });

    describe('addHumiditeToCollectionIfMissing', () => {
      it('should add a Humidite to an empty array', () => {
        const humidite: IHumidite = { id: 123 };
        expectedResult = service.addHumiditeToCollectionIfMissing([], humidite);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(humidite);
      });

      it('should not add a Humidite to an array that contains it', () => {
        const humidite: IHumidite = { id: 123 };
        const humiditeCollection: IHumidite[] = [
          {
            ...humidite,
          },
          { id: 456 },
        ];
        expectedResult = service.addHumiditeToCollectionIfMissing(humiditeCollection, humidite);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Humidite to an array that doesn't contain it", () => {
        const humidite: IHumidite = { id: 123 };
        const humiditeCollection: IHumidite[] = [{ id: 456 }];
        expectedResult = service.addHumiditeToCollectionIfMissing(humiditeCollection, humidite);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(humidite);
      });

      it('should add only unique Humidite to an array', () => {
        const humiditeArray: IHumidite[] = [{ id: 123 }, { id: 456 }, { id: 93255 }];
        const humiditeCollection: IHumidite[] = [{ id: 123 }];
        expectedResult = service.addHumiditeToCollectionIfMissing(humiditeCollection, ...humiditeArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const humidite: IHumidite = { id: 123 };
        const humidite2: IHumidite = { id: 456 };
        expectedResult = service.addHumiditeToCollectionIfMissing([], humidite, humidite2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(humidite);
        expect(expectedResult).toContain(humidite2);
      });

      it('should accept null and undefined values', () => {
        const humidite: IHumidite = { id: 123 };
        expectedResult = service.addHumiditeToCollectionIfMissing([], null, humidite, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(humidite);
      });

      it('should return initial array if no Humidite is added', () => {
        const humiditeCollection: IHumidite[] = [{ id: 123 }];
        expectedResult = service.addHumiditeToCollectionIfMissing(humiditeCollection, undefined, null);
        expect(expectedResult).toEqual(humiditeCollection);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
