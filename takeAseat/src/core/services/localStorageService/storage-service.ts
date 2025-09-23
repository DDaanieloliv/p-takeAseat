import { Injectable, Inject, PLATFORM_ID } from '@angular/core';
import { isPlatformBrowser } from '@angular/common';

@Injectable({
  providedIn: 'root'
})
export class SafeStorageService {

  constructor(
    @Inject(PLATFORM_ID) private platformId: object
  ) {}

  setItem(key: string, value: any): boolean {
    if (!isPlatformBrowser(this.platformId)) {
      return false;
    }

    try {
      localStorage.setItem(key, JSON.stringify(value));
      return true;
    } catch (error) {
      console.error('Storage error:', error);
      return false;
    }
  }

  getItem<T>(key: string): T | null {
    if (!isPlatformBrowser(this.platformId)) {
      return null;
    }

    try {
      const item = localStorage.getItem(key);
      return item ? JSON.parse(item) : null;
    } catch (error) {
      console.error('Storage error:', error);
      return null;
    }
  }

  removeItem(key: string): void {
    if (isPlatformBrowser(this.platformId)) {
      localStorage.removeItem(key);
    }
  }
}
