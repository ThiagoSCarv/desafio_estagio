import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { LucideAngularModule, UserPlus, FileText, Upload } from 'lucide-angular';

import { ButtonComponent } from './components/button/button.component';

@NgModule({
  declarations: [ButtonComponent],
  imports: [
    CommonModule,
    MatIconModule,
    MatProgressSpinnerModule,
    LucideAngularModule.pick({ UserPlus, FileText, Upload }),
  ],
  exports: [ButtonComponent],
})
export class SharedModule {}
