import { Component, ElementRef, OnInit, ViewChild } from '@angular/core';
import { Prediction } from '../home.component';

//import * as mobilenet from '@tensorflow-models/mobilenet';
//import * as cocossd from '@tensorflow-models/coco-ssd';
//import * as tf from '@tensorflow/tfjs';
@Component({
  selector: 'jhi-video-detection',
  templateUrl: './video-detection.component.html',
  styleUrls: ['./video-detection.component.scss'],
})
export class VideoDetectionComponent implements OnInit {
  @ViewChild('video')
  video!: ElementRef;

  predictions!: Prediction[];
  model: any;
  loading = true;

  /* constructor() {
   }*/

  public async ngOnInit(): Promise<void> {
    //  console.log('loading mobilenet model...');
    //console.log('Using TensorFlow backend: ', tf.getBackend());
    //this.model = await cocossd.load();
    // console.log('Sucessfully loaded model');
    this.loading = false;
    setInterval(async () => {
      // this.predictions = await this.model.classify(this.video.nativeElement);
      // this.predictions = await this.model.detect(this.video.nativeElement);
    }, 3000);
  }

  ngAfterViewInit(): void {
    const vid = this.video.nativeElement;
    if (navigator.mediaDevices.getUserMedia) {
      navigator.mediaDevices
        .getUserMedia({ video: true })
        .then(stream => {
          vid.srcObject = stream;
        })
        .catch(error => {
          //  console.log('Something went wrong!');
        });
    }
  }

  public stopVideo(): void {
    window.location.assign('/');
  }
}
