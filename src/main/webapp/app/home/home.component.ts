import { Component, OnInit, OnDestroy, ViewChild, ElementRef } from '@angular/core';
import { Router } from '@angular/router';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';

import { AccountService } from 'app/core/auth/account.service';
import { Account } from 'app/core/auth/account.model';
import { FormBuilder, Validators } from '@angular/forms';
import { LoginService } from '../login/login.service';
//import * as mobilenet from '@tensorflow-models/mobilenet';

export interface Prediction {
  className: string;
  probability: number;
}

@Component({
  selector: 'jhi-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.scss'],
})
export class HomeComponent implements OnInit, OnDestroy {
  public model: any;
  public camView = false;
  public loading = true;

  public imageSrc!: string;

  public predictions!: Prediction[];
  @ViewChild('username', { static: false })
  username!: ElementRef;

  @ViewChild('img')
  public imageEl!: ElementRef;

  account: Account | null = null;

  authenticationError = false;

  loginForm = this.fb.group({
    username: [null, [Validators.required]],
    password: [null, [Validators.required]],
    rememberMe: [false],
  });

  private readonly destroy$ = new Subject<void>();

  constructor(
    private accountService: AccountService,
    private fb: FormBuilder,
    private loginService: LoginService,
    private router: Router
  ) {}

  ngAfterViewInit(): void {
    this.username.nativeElement.focus();
  }

  async ngOnInit(): Promise<void> {
    console.log('loading mobilenet model...');
    this.accountService
      .getAuthenticationState()
      .pipe(takeUntil(this.destroy$))
      .subscribe(account => (this.account = account));
    // this.model = await mobilenet.load().catch(
    //reason =>  console.log('reason', reason)
    //);
    console.log('Sucessfully loaded model');
    this.loading = false;
  }

  // @ts-ignore
  public fileChangeEvent(event: any): Promise<void> {
    if (event.target.files && event.target.files[0]) {
      const reader = new FileReader();
      reader.readAsDataURL(event.target.files[0]);
      reader.onload = (res: any) => {
        this.imageSrc = res.target.result;
        return setTimeout(async () => {
          const imgEl = this.imageEl.nativeElement;
          this.predictions = await this.model.classify(imgEl);
        }, 0);
      };
    }
  }

  login(): void {
    this.router.navigate(['/login']);
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  connexion(): void {
    this.loginService
      .login({
        username: this.loginForm.get('username')!.value,
        password: this.loginForm.get('password')!.value,
        rememberMe: this.loginForm.get('rememberMe')!.value,
      })
      .subscribe({
        next: () => {
          this.authenticationError = false;
          if (!this.router.getCurrentNavigation()) {
            // There were no routing during login (eg from navigationToStoredUrl)
            this.router.navigate(['']);
          }
        },
        error: () => (this.authenticationError = true),
      });
  }
}
